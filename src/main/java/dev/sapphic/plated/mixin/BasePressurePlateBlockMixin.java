/*
 * Copyright 2021 Chloe Dawn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.sapphic.plated.mixin;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.sapphic.plated.PressurePlates.AABBS;
import static dev.sapphic.plated.PressurePlates.FACING;
import static dev.sapphic.plated.PressurePlates.PRESSED_AABBS;
import static dev.sapphic.plated.PressurePlates.WATERLOGGED;

@Mixin(AbstractPressurePlateBlock.class)
abstract class BasePressurePlateBlockMixin extends Block implements IWaterLoggable {
  @Unique(silent = true)
  private static final Direction[] FACES = Direction.values();

  BasePressurePlateBlockMixin(final Properties properties) {
    super(properties);
  }

  @Override
  public @Nullable BlockState getStateForPlacement(final BlockItemUseContext context) {
    final World level = context.getWorld();
    final BlockPos pos = context.getPos();
    final Direction clickedFace = context.getFace();
    BlockState state = this.getDefaultState().with(FACING, clickedFace)
      .with(WATERLOGGED, level.getFluidState(pos).getFluid() == Fluids.WATER);

    // Always prefer the clicked face
    if (state.isValidPosition(level, pos)) {
      return state;
    }

    for (final Direction face : FACES) {
      if (face == clickedFace) {
        continue;
      }

      state = state.with(FACING, face);

      if (state.isValidPosition(level, pos)) {
        return state;
      }
    }

    return null;
  }

  @Override
  @Deprecated
  public FluidState getFluidState(final BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
  }

  @Override
  @Deprecated
  public BlockState rotate(final BlockState state, final Rotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
  }

  @Override
  @Deprecated
  public BlockState mirror(final BlockState state, final Mirror mirror) {
    return state.rotate(mirror.toRotation(state.get(FACING)));
  }

  @Redirect(
    method = "getShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/shapes/ISelectionContext;)Lnet/minecraft/util/math/shapes/VoxelShape;",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lnet/minecraft/block/AbstractPressurePlateBlock;PRESSED_AABB:Lnet/minecraft/util/math/shapes/VoxelShape;"))
  private VoxelShape getPressedDirectionalShape(final BlockState state) {
    return PRESSED_AABBS.get(state.get(FACING));
  }

  @Redirect(
    method = "getShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/shapes/ISelectionContext;)Lnet/minecraft/util/math/shapes/VoxelShape;",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lnet/minecraft/block/AbstractPressurePlateBlock;UNPRESSED_AABB:Lnet/minecraft/util/math/shapes/VoxelShape;"))
  private VoxelShape getDirectionalShape(final BlockState state) {
    return AABBS.get(state.get(FACING));
  }

  @Inject(
    method = "updatePostPlacement(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
    at = @At("HEAD"))
  private void updateFluidState(final BlockState state, final Direction side, final BlockState neighbor, final IWorld world, final BlockPos pos, final BlockPos offset, final CallbackInfoReturnable<BlockState> ci) {
    if (state.get(WATERLOGGED)) {
      world.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
    }
  }

  @Redirect(
    method = "updatePostPlacement(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lnet/minecraft/util/Direction;DOWN:Lnet/minecraft/util/Direction;"))
  private Direction getNeighborFace(final BlockState state) {
    return state.get(FACING).getOpposite();
  }

  @Redirect(
    method = "isValidPosition(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;)Z",
    require = 1, allow = 1,
    at = @At(value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL,
      target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;"))
  private BlockPos getSurfacePos(final BlockPos pos, final BlockState state) {
    return pos.offset(state.get(FACING).getOpposite());
  }

  @Redirect(
    method = "isValidPosition(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;)Z",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lnet/minecraft/util/Direction;UP:Lnet/minecraft/util/Direction;"))
  private Direction getSurvivableFace(final BlockState state) {
    return state.get(FACING);
  }

  @Redirect(
    method = "updateState(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)V",
    at = @At(value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL,
      target = "Lnet/minecraft/block/AbstractPressurePlateBlock;updateNeighbors(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
  private void updateNeighbors(final AbstractPressurePlateBlock block, final World level, final BlockPos pos, final World level1, final BlockPos pos1, final BlockState state) {
    this.updateNeighbors(state, level, pos);
  }

  @Redirect(
    method = "onReplaced(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V",
    require = 1, allow = 1,
    at = @At(value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL,
      target = "Lnet/minecraft/block/AbstractPressurePlateBlock;updateNeighbors(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
  private void updateNeighbors(final AbstractPressurePlateBlock block, final World level, final BlockPos pos, final BlockState state) {
    this.updateNeighbors(state, level, pos);
  }

  @Redirect(
    method = "getStrongPower(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)I",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lnet/minecraft/util/Direction;UP:Lnet/minecraft/util/Direction;"))
  private Direction getConductiveFace(final BlockState state) {
    return state.get(FACING);
  }

  /**
   * We are redirecting the calls rather than injecting into the target as when the block is removed,
   * the state will not be equal to {@code level.getBlockState(pos)} and therefore we need to capture
   * the original state at the call sites. This implementation is fairly inefficient as the first
   * call updates the below position and the second call updates the origin position, but this is how
   * it is implemented in the original method and we want to respect existing semantics
   *
   * @param state The block state
   * @param level The level to update neighbors in
   * @param pos   The origin position
   */
  @Unique
  private void updateNeighbors(final BlockState state, final World level, final BlockPos pos) {
    level.notifyNeighborsOfStateChange(pos, this);
    level.notifyNeighborsOfStateChange(pos.offset(state.get(FACING).getOpposite()), this);
  }
}
