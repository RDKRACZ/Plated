package net.insomniakitten.plated.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Collection;

public class BlockPressurePlatePlated extends BlockPressurePlate implements IPlatedPressurePlate {
    private final Sensitivity sensitivity;

    public BlockPressurePlatePlated(final Material material, final SoundType soundType, final Sensitivity sensitivity) {
        super(material, sensitivity);
        this.sensitivity = sensitivity;
        this.setSoundType(soundType);
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        return this.getPlateBoundingBox(state, access, pos);
    }

    @Override
    public boolean canPlaceBlockAt(final World world, final BlockPos pos) {
        return this.canPlaceAt(world, pos);
    }

    @Override
    public void neighborChanged(final IBlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighbor) {
        this.checkForDrop(state, world, pos);
    }

    @Override
    protected void updateState(final World world, final BlockPos pos, final IBlockState state, final int oldRedstoneStrength) {
        final int redstoneStrength = this.computeRedstoneStrength(world, pos);
        final boolean wasPowered = oldRedstoneStrength > 0;
        final boolean isPowered = redstoneStrength > 0;
        if (oldRedstoneStrength != redstoneStrength) {
            final IBlockState newState = this.setRedstoneStrength(state, redstoneStrength);
            world.setBlockState(pos, newState, 2);
            this.notifyNeighbors(newState, world, pos);
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
        if (!isPowered && wasPowered) {
            this.playClickOffSound(world, pos);
        } else if (isPowered && !wasPowered) {
            this.playClickOnSound(world, pos);
        }
        if (isPowered) {
            world.scheduleUpdate(pos, this, this.tickRate(world));
        }
    }

    @Override
    public void breakBlock(final World world, final BlockPos pos, final IBlockState state) {
        if (this.getRedstoneStrength(state) > 0) {
            this.notifyNeighbors(state, world, pos);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    @Deprecated
    public int getStrongPower(final IBlockState state, final IBlockAccess access, final BlockPos pos, final EnumFacing face) {
        if (face == state.getValue(IPlatedPressurePlate.PROPERTY_FACING).getOpposite()) {
            return this.getRedstoneStrength(state);
        }
        return 0;
    }

    @Override
    public boolean canPlaceBlockOnSide(final World world, final BlockPos pos, final EnumFacing face) {
        return this.canPlaceOnSide(world, pos, face);
    }

    @Override
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing face) {
        final boolean neg = EnumFacing.AxisDirection.NEGATIVE == face.getAxisDirection();
        return this.withRotation(world, pos, neg ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90);
    }

    @Override
    public IBlockState getStateForPlacement(final World world, final BlockPos pos, final EnumFacing face, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer, final EnumHand hand) {
        return this.getDefaultState().withProperty(IPlatedPressurePlate.PROPERTY_FACING, face.getOpposite());
    }

    @Override
    public boolean isPowered(final IBlockState state) {
        return this.getRedstoneStrength(state) > 0;
    }

    @Override
    protected int computeRedstoneStrength(final World world, final BlockPos pos) {
        final IBlockState state = world.getBlockState(pos);
        final AxisAlignedBB box = this.getPressureBoundingBox(state, world, pos);
        final Collection<? extends Entity> entities;
        if (this.sensitivity == Sensitivity.EVERYTHING) {
            entities = world.getEntitiesWithinAABBExcludingEntity(null, box.offset(pos));

        } else if (this.sensitivity == Sensitivity.MOBS) {
            entities = world.getEntitiesWithinAABB(EntityLivingBase.class, box.offset(pos));
        } else {
            entities = null;
        }
        if (entities != null && !entities.isEmpty()) {
            for (final Entity entity : entities) {
                if (!entity.doesEntityNotTriggerPressurePlate()) {
                    return 15;
                }
            }
        }
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(final int meta) {
        final EnumFacing facing = EnumFacing.byIndex(meta >> 1);
        final boolean powered = (meta & 1) == 1;
        return this.getDefaultState()
            .withProperty(IPlatedPressurePlate.PROPERTY_FACING, facing)
            .withProperty(BlockPressurePlate.POWERED, powered);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        final int facing = state.getValue(IPlatedPressurePlate.PROPERTY_FACING).getIndex() << 1;
        final int powered = state.getValue(BlockPressurePlate.POWERED) ? 1 : 0;
        return facing | powered;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IPlatedPressurePlate.PROPERTY_FACING, BlockPressurePlate.POWERED);
    }
}
