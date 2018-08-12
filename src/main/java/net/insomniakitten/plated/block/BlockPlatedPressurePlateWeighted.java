package net.insomniakitten.plated.block;

import net.insomniakitten.plated.util.PlatedBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlateWeighted;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Optional;

public class BlockPlatedPressurePlateWeighted extends BlockPressurePlateWeighted implements IPlatedPressurePlate {

    private final int maxWeight;

    public BlockPlatedPressurePlateWeighted(final Material material, final SoundType soundType, final int maxWeight, final MapColor color) {
        super(material, maxWeight, color);
        this.maxWeight = maxWeight;
        this.setSoundType(soundType);
    }

    public BlockPlatedPressurePlateWeighted(final Material material, final SoundType soundType, final int maxWeight) {
        this(material, soundType, maxWeight, material.getMaterialMapColor());
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
        if (face == state.getActualState(access, pos).getValue(IPlatedPressurePlate.PROPERTY_FACING).getOpposite()) {
            return this.getRedstoneStrength(state);
        }
        return 0;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        final TileEntity tileEntity = access.getTileEntity(pos);
        if (tileEntity instanceof PlatedBlockEntity) {
            final EnumFacing facing = ((PlatedBlockEntity) tileEntity).getFacing();
            return state.withProperty(IPlatedPressurePlate.PROPERTY_FACING, facing);
        }
        return state;
    }

    @Override
    public boolean canPlaceBlockOnSide(final World world, final BlockPos pos, final EnumFacing face) {
        return this.canPlaceOnSide(world, pos, face);
    }

    @Override
    public boolean hasTileEntity(final IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(final World world, final IBlockState state) {
        return new PlatedBlockEntity(state.getValue(IPlatedPressurePlate.PROPERTY_FACING));
    }

    @Override
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing face) {
        final boolean neg = face.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE;
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
        final AxisAlignedBB box = this.getPressureBoundingBox(world.getBlockState(pos), world, pos);
        final int weight = Math.min(world.getEntitiesWithinAABB(Entity.class, box.offset(pos)).size(), this.maxWeight);
        if (weight > 0) {
            float power = Math.min((float) this.maxWeight, weight) / (float) this.maxWeight;
            return MathHelper.ceil(power * 15.0F);
        }
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IPlatedPressurePlate.PROPERTY_FACING, BlockPressurePlateWeighted.POWER);
    }

}
