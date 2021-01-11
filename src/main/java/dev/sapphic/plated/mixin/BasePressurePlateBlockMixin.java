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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
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

@Mixin(BasePressurePlateBlock.class)
abstract class BasePressurePlateBlockMixin extends Block implements SimpleWaterloggedBlock {
  @Unique(silent = true)
  private static final Direction[] FACES = Direction.values();

  BasePressurePlateBlockMixin(final Properties properties) {
    super(properties);
  }

  @Override
  public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
    final Level level = context.getLevel();
    final BlockPos pos = context.getClickedPos();
    final Direction clickedFace = context.getClickedFace();
    BlockState state = this.defaultBlockState().setValue(FACING, clickedFace);

    // Always prefer the clicked face
    if (state.canSurvive(level, pos)) {
      return this.waterlogged(state, level, pos);
    }

    for (final Direction face : FACES) {
      if (face == clickedFace) {
        continue;
      }

      state = state.setValue(FACING, face);

      if (state.canSurvive(level, pos)) {
        return this.waterlogged(state, level, pos);
      }
    }

    return null;
  }

  @Override
  @Deprecated
  public FluidState getFluidState(final BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  @Deprecated
  public BlockState rotate(final BlockState state, final Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  @Deprecated
  public BlockState mirror(final BlockState state, final Mirror mirror) {
    return state.rotate(mirror.getRotation(state.getValue(FACING)));
  }

  @Redirect(
    method = "getShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lnet/minecraft/world/level/block/BasePressurePlateBlock;PRESSED_AABB:Lnet/minecraft/world/phys/shapes/VoxelShape;"))
  private VoxelShape getPressedDirectionalShape(final BlockState state) {
    return PRESSED_AABBS.get(state.getValue(FACING));
  }

  @Redirect(
    method = "getShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lnet/minecraft/world/level/block/BasePressurePlateBlock;AABB:Lnet/minecraft/world/phys/shapes/VoxelShape;"))
  private VoxelShape getDirectionalShape(final BlockState state) {
    return AABBS.get(state.getValue(FACING));
  }

  @Inject(
    method = "updateShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
    at = @At("HEAD"))
  private void updateFluidState(final BlockState state, final Direction side, final BlockState neighbor, final LevelAccessor level, final BlockPos pos, final BlockPos offset, final CallbackInfoReturnable<BlockState> ci) {
    if (state.getValue(WATERLOGGED)) {
      level.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
    }
  }

  @Redirect(
    method = "updateShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lnet/minecraft/core/Direction;DOWN:Lnet/minecraft/core/Direction;"))
  private Direction getNeighborFace(final BlockState state) {
    return state.getValue(FACING).getOpposite();
  }

  @Redirect(
    method = "canSurvive(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z",
    require = 1, allow = 1,
    at = @At(value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL,
      target = "Lnet/minecraft/core/BlockPos;below()Lnet/minecraft/core/BlockPos;"))
  private BlockPos getSurfacePos(final BlockPos pos, final BlockState state) {
    return pos.relative(state.getValue(FACING).getOpposite());
  }

  @Redirect(
    method = "canSurvive(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lnet/minecraft/core/Direction;UP:Lnet/minecraft/core/Direction;"))
  private Direction getSurvivableFace(final BlockState state) {
    return state.getValue(FACING);
  }

  @Redirect(
    method = "checkPressed(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)V",
    at = @At(value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL,
      target = "Lnet/minecraft/world/level/block/BasePressurePlateBlock;updateNeighbours(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
  private void updateNeighbors116(final BasePressurePlateBlock block, final Level level, final BlockPos pos, final Level level1, final BlockPos pos1, final BlockState state) {
    this.updateNeighbors(state, level, pos);
  }

  @Redirect(
    method = "onRemove(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V",
    require = 1, allow = 1,
    at = @At(value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL,
      target = "Lnet/minecraft/world/level/block/BasePressurePlateBlock;updateNeighbours(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
  private void updateNeighbors(final BasePressurePlateBlock block, final Level level, final BlockPos pos, final BlockState state) {
    this.updateNeighbors(state, level, pos);
  }

  @Redirect(
    method = "getDirectSignal(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lnet/minecraft/core/Direction;UP:Lnet/minecraft/core/Direction;"))
  private Direction getConductiveFace(final BlockState state) {
    return state.getValue(FACING);
  }

  @Unique
  private BlockState waterlogged(final BlockState state, final Level level, final BlockPos pos) {
    return state.setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
  }

  @Unique
  private void updateNeighbors(final BlockState state, final Level level, final BlockPos pos) {
    // This is inefficient but so is the default implementation - we just change the relative offset
    level.updateNeighborsAt(pos, this);
    level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
  }
}
