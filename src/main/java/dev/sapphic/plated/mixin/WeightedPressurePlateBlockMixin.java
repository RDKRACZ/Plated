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
import net.minecraft.block.WeightedPressurePlateBlock;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.sapphic.plated.PressurePlates.FACING;
import static dev.sapphic.plated.PressurePlates.TOUCH_AABBS;
import static dev.sapphic.plated.PressurePlates.WATERLOGGED;

@Mixin(WeightedPressurePlateBlock.class)
abstract class WeightedPressurePlateBlockMixin extends AbstractPressurePlateBlock {
  WeightedPressurePlateBlockMixin(final Properties properties) {
    super(properties);
  }

  @ModifyArg(
    method = "<init>(ILnet/minecraft/block/AbstractBlock$Properties;)V",
    require = 1, allow = 1,
    at = @At(value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL,
      target = "Lnet/minecraft/block/WeightedPressurePlateBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"))
  private BlockState setDefaultFacing(final BlockState state) {
    return state.with(FACING, Direction.UP).with(WATERLOGGED, false);
  }

  @Redirect(
    method = "computeRedstoneStrength(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)I",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lnet/minecraft/block/WeightedPressurePlateBlock;PRESSURE_AABB:Lnet/minecraft/util/math/AxisAlignedBB;"))
  private AxisAlignedBB getTouchAABB(final World world, final BlockPos pos) {
    return TOUCH_AABBS.get(world.getBlockState(pos).get(FACING));
  }

  @Inject(
    method = "fillStateContainer(Lnet/minecraft/state/StateContainer$Builder;)V",
    at = @At("TAIL"))
  private void addProperties(final StateContainer.Builder<Block, BlockState> builder, final CallbackInfo ci) {
    builder.add(FACING, WATERLOGGED);
  }
}
