package dev.sapphic.plated.mixin.quark;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.quark.content.automation.block.ObsidianPressurePlateBlock;

import static dev.sapphic.plated.PressurePlates.FACING;
import static dev.sapphic.plated.PressurePlates.TOUCH_AABBS;

// TODO Superclass hierarchy

@Pseudo
@Mixin(ObsidianPressurePlateBlock.class)
abstract class ObsidianPressurePlateBlockMixin {
  @Redirect(
    method = "computeRedstoneStrength(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)I",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lvazkii/quark/content/automation/block/ObsidianPressurePlateBlock;PRESSURE_AABB:Lnet/minecraft/util/math/AxisAlignedBB;"))
  private AxisAlignedBB getTouchAABB(final World world, final BlockPos pos) {
    return TOUCH_AABBS.get(world.getBlockState(pos).get(FACING));
  }
}
