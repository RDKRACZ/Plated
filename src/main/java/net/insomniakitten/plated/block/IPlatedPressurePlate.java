package net.insomniakitten.plated.block;

import net.insomniakitten.plated.util.AxisDirectionalBB;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IPlatedPressurePlate {
    IProperty<EnumFacing> PROPERTY_FACING = PropertyDirection.create("facing");

    AxisDirectionalBB BOUNDING_BOX_UNPRESSED = new AxisDirectionalBB(0.0625, 0.0, 0.0625, 0.9375, 0.0625, 0.9375);
    AxisDirectionalBB BOUNDING_BOX_PRESSED = new AxisDirectionalBB(0.0625, 0.0, 0.0625, 0.9375, 0.03125, 0.9375);
    AxisDirectionalBB BOUNDING_BOX_COLLISION = new AxisDirectionalBB(0.125, 0.0, 0.125, 0.875, 0.25, 0.875);

    boolean isPowered(final IBlockState state);

    default IProperty<EnumFacing> getFacingProperty() {
        return IPlatedPressurePlate.PROPERTY_FACING;
    }

    default EnumFacing getFacing(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return state.getActualState(world, pos).getValue(this.getFacingProperty());
    }

    default AxisAlignedBB getPlateBoundingBox(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        final EnumFacing facing = this.getFacing(state, world, pos);
        if (this.isPowered(state)) {
            return IPlatedPressurePlate.BOUNDING_BOX_PRESSED.withFacing(facing);
        }
        return IPlatedPressurePlate.BOUNDING_BOX_UNPRESSED.withFacing(facing);
    }

    default AxisAlignedBB getPressureBoundingBox(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return IPlatedPressurePlate.BOUNDING_BOX_COLLISION.withFacing(this.getFacing(state, world, pos));
    }

    default boolean canPlaceAt(final World world, final BlockPos pos) {
        for (final EnumFacing side : EnumFacing.VALUES) {
            if (this.canPlaceOnSide(world, pos, side)) {
                return true;
            }
        }
        return false;
    }

    default boolean canPlaceOnSide(final World world, final BlockPos pos, final EnumFacing side) {
        final IBlockState state = world.getBlockState(pos.offset(side.getOpposite()));
        final BlockFaceShape shape = state.getBlockFaceShape(world, pos, side);
        if (BlockFaceShape.SOLID == shape) {
            return true;
        }
        if (EnumFacing.UP == side) {
            return state.getBlock() instanceof BlockFence;
        }
        return false;
    }

    default void notifyNeighbors(final IBlockState state, final World world, final BlockPos pos) {
        final EnumFacing facing = this.getFacing(state, world, pos);
        world.notifyNeighborsOfStateChange(pos, state.getBlock(), false);
        world.notifyNeighborsOfStateChange(pos.offset(facing), state.getBlock(), false);
    }

    default boolean withRotation(final World world, final BlockPos pos, final Rotation rotation) {
        IBlockState state = world.getBlockState(pos);
        EnumFacing facing = this.getFacing(state, world, pos);
        final EnumFacing original = facing;
        do {
            facing = rotation.rotate(facing);
            state = state.withProperty(this.getFacingProperty(), facing);
        } while (original != facing && !this.canPlaceOnSide(world, pos, facing.getOpposite()));
        return this.canPlaceOnSide(world, pos, facing.getOpposite()) && world.setBlockState(pos, state);
    }

    default void checkForDrop(final IBlockState state, final World world, final BlockPos pos) {
        if (!this.canPlaceOnSide(world, pos, this.getFacing(state, world, pos).getOpposite())) {
            Block.spawnAsEntity(world, pos, new ItemStack(state.getBlock()));
            world.setBlockToAir(pos);
        }
    }
}
