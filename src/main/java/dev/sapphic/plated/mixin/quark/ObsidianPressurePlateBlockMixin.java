package dev.sapphic.plated.mixin.quark;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import vazkii.quark.content.automation.block.ObsidianPressurePlateBlock;

import static dev.sapphic.plated.PressurePlates.FACING;
import static dev.sapphic.plated.PressurePlates.TOUCH_AABBS;
import static dev.sapphic.plated.PressurePlates.WATERLOGGED;

// TODO https://github.com/Vazkii/Quark/pull/2905

@Mixin(ObsidianPressurePlateBlock.class)
abstract class ObsidianPressurePlateBlockMixin {
  @ModifyArg(
    method = "<init>(Ljava/lang/String;Lvazkii/quark/base/module/QuarkModule;Lnet/minecraft/item/ItemGroup;Lnet/minecraft/block/AbstractBlock$Properties;)V",
    require = 1, allow = 1,
    at = @At(value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL,
      target = "Lvazkii/quark/content/automation/block/ObsidianPressurePlateBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"))
  private BlockState setDefaultFacing(final BlockState state) {
    return state.with(FACING, Direction.UP).with(WATERLOGGED, false);
  }

  @Redirect(
    method = "computeRedstoneStrength(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)I",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lvazkii/quark/content/automation/block/ObsidianPressurePlateBlock;PRESSURE_AABB:Lnet/minecraft/util/math/AxisAlignedBB;"))
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
