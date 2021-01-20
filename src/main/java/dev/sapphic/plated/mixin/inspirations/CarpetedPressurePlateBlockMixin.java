package dev.sapphic.plated.mixin.inspirations;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import knightminer.inspirations.utility.block.CarpetedPressurePlateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static dev.sapphic.plated.PressurePlates.FACING;

@Pseudo
@Mixin(CarpetedPressurePlateBlock.class)
abstract class CarpetedPressurePlateBlockMixin extends PressurePlateBlock {
  @Unique
  private static final ImmutableMap<Direction, VoxelShape> CARPETED_AABBS =
    Maps.immutableEnumMap(ImmutableMap.<Direction, VoxelShape>builder()
      .put(Direction.DOWN, VoxelShapes.or(
        makeCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0),
        makeCuboidShape(1.0, 14.5, 1.0, 15.0, 15.0, 15.0)
      ))
      .put(Direction.UP, VoxelShapes.or(
        makeCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
        makeCuboidShape(1.0, 1.0, 1.0, 15.0, 1.5, 15.0)
      ))
      .put(Direction.NORTH, VoxelShapes.or(
        makeCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0),
        makeCuboidShape(1.0, 1.0, 14.5, 15.0, 15.0, 15.0)
      ))
      .put(Direction.SOUTH, VoxelShapes.or(
        makeCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0),
        makeCuboidShape(1.0, 1.0, 1.0, 15.0, 15.0, 1.5)
      ))
      .put(Direction.WEST, VoxelShapes.or(
        makeCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0),
        makeCuboidShape(14.5, 1.0, 1.0, 15.0, 15.0, 15.0)
      ))
      .put(Direction.EAST, VoxelShapes.or(
        makeCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0),
        makeCuboidShape(1.0, 1.0, 1.0, 1.5, 15.0, 15.0)
      ))
      .build());

  @Unique
  private static final ImmutableMap<Direction, VoxelShape> PRESSED_CARPETED_AABBS =
    Maps.immutableEnumMap(ImmutableMap.<Direction, VoxelShape>builder()
      .put(Direction.DOWN, VoxelShapes.or(
        makeCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0),
        makeCuboidShape(1.0, 14.75, 1.0, 15.0, 15.0, 15.0)
      ))
      .put(Direction.UP, VoxelShapes.or(
        makeCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
        makeCuboidShape(1.0, 1.0, 1.0, 15.0, 1.25, 15.0)
      ))
      .put(Direction.NORTH, VoxelShapes.or(
        makeCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0),
        makeCuboidShape(1.0, 1.0, 14.75, 15.0, 15.0, 15.0)
      ))
      .put(Direction.SOUTH, VoxelShapes.or(
        makeCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0),
        makeCuboidShape(1.0, 1.0, 1.0, 15.0, 15.0, 1.25)
      ))
      .put(Direction.WEST, VoxelShapes.or(
        makeCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0),
        makeCuboidShape(14.75, 1.0, 1.0, 15.0, 15.0, 15.0)
      ))
      .put(Direction.EAST, VoxelShapes.or(
        makeCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0),
        makeCuboidShape(1.0, 1.0, 1.0, 1.25, 15.0, 15.0)
      ))
      .build());

  CarpetedPressurePlateBlockMixin(final Sensitivity sensitivity, final Properties properties) {
    super(sensitivity, properties);
  }

  @Redirect(
    method = "getShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/shapes/ISelectionContext;)Lnet/minecraft/util/math/shapes/VoxelShape;",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lknightminer/inspirations/utility/block/CarpetedPressurePlateBlock;PRESSED_AABB:Lnet/minecraft/util/math/shapes/VoxelShape;"))
  private VoxelShape getPressedDirectionalShape(final BlockState state) {
    return PRESSED_CARPETED_AABBS.get(state.get(FACING));
  }

  @Redirect(
    method = "getShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/shapes/ISelectionContext;)Lnet/minecraft/util/math/shapes/VoxelShape;",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lknightminer/inspirations/utility/block/CarpetedPressurePlateBlock;UNPRESSED_AABB:Lnet/minecraft/util/math/shapes/VoxelShape;"))
  private VoxelShape getDirectionalShape(final BlockState state) {
    return CARPETED_AABBS.get(state.get(FACING));
  }
}
