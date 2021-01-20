package dev.sapphic.plated.mixin.inspirations;

import dev.sapphic.plated.PressurePlates;
import knightminer.inspirations.utility.UtilityEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(UtilityEvents.class)
abstract class PressurePlateCarpetingMixin {
  @Unique
  private static final Logger LOGGER = LogManager.getLogger("plated");

  @Redirect(
    method = "placeCarpetOnPressurePlate(Lnet/minecraftforge/event/entity/player/PlayerInteractEvent$RightClickBlock;)V",
    require = 1, allow = 1,
    at = @At(
      value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL,
      target = "Lnet/minecraft/block/BlockState;updatePostPlacement(Lnet/minecraft/util/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
  private static BlockState getDirectionalCarpetedPressurePlate(final BlockState state, final Direction face, final BlockState neighbor, final IWorld world, final BlockPos pos, final BlockPos offset) {
    final BlockState originalState = world.getBlockState(pos); // Cannot capture locals within a redirect

    if (!originalState.isIn(Blocks.STONE_PRESSURE_PLATE)) {
      LOGGER.warn("Original block state changed in world during method execution, skipping");
      return state.updatePostPlacement(face, neighbor, world, pos, offset);
    }

    final Direction facing = originalState.get(PressurePlates.FACING);

    if (facing == Direction.UP) {
      // Minor optimization, the necessary calls have already been made
      return state.updatePostPlacement(face, neighbor, world, pos, offset);
    }

    final Direction actualFace = facing.getOpposite();
    final BlockPos actualOffset = pos.offset(actualFace);
    final BlockState actualNeighbor = world.getBlockState(actualOffset);
    return state.with(PressurePlates.FACING, facing)
      .updatePostPlacement(actualFace, actualNeighbor, world, pos, actualOffset);
  }
}
