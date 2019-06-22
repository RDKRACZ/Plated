package net.insomniakitten.plated.compat.moeswitches;

import net.insomniakitten.plated.block.BlockPressurePlatePlated;
import net.insomniakitten.plated.util.BlockEntityPlated;
import net.insomniakitten.plated.util.RegistryHelper;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class BlockPressurePlatePlatedMoe extends BlockPressurePlatePlated {
    public static final IProperty<EnumFacing> WORKAROUND_FACING = PropertyDirection.create("plated_facing");

    public BlockPressurePlatePlatedMoe(final Moeterial moeMaterial) {
        super(moeMaterial.getMaterial(), moeMaterial.getSoundType(), moeMaterial.getSensitivity());
        RegistryHelper.setRegistryName("moeswitches", this, "moeplate_" + moeMaterial);
        this.setTranslationKey("moeswitches.moeplate" + moeMaterial);
        this.setHardness(0.5F);
    }

    @Override
    public IProperty<EnumFacing> getFacingProperty() {
        return WORKAROUND_FACING;
    }

    @Override
    public IBlockState getStateForPlacement(final World world, final BlockPos pos, final EnumFacing face, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer, final EnumHand hand) {
        final IBlockState state = super.getStateForPlacement(world, pos, face, hitX, hitY, hitZ, meta, placer, hand);
        return face.getAxis().isVertical() ? state.withProperty(CompatMoeSwitches.getFacingProperty(), placer.getHorizontalFacing()) : state;
    }

    @Override
    public IBlockState getStateFromMeta(final int meta) {
        final EnumFacing facing = EnumFacing.byHorizontalIndex(meta >> 1);
        final boolean powered = (meta & 1) == 1;
        return this.getDefaultState().withProperty(CompatMoeSwitches.getFacingProperty(), facing);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        final int facing = state.getValue(CompatMoeSwitches.getFacingProperty()).getHorizontalIndex() << 1;
        final int powered = state.getValue(BlockPressurePlate.POWERED) ? 1 : 0;
        return facing | powered;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this)
            .add(WORKAROUND_FACING).add(POWERED)
            .add(CompatMoeSwitches.getFacingProperty())
            .build();
    }

    @Override
    public void onEntityCollision(final World world, final BlockPos pos, final IBlockState state, final Entity entity) {
        super.onEntityCollision(world, pos, state, entity);
        final int rate = Math.max(CompatMoeSwitches.getChatRate(), 1);
        if (!world.isRemote && entity instanceof EntityPlayer && 0 == world.rand.nextInt(rate)) {
            final boolean isPowered = this.getRedstoneStrength(state) > 0;
            final boolean isTriggered = this.computeRedstoneStrength(world, pos) > 0;
            if (isTriggered && !isPowered) {
                final boolean stone = Material.ROCK == state.getMaterial();
                final String key = "moeswitches.message." + (stone ? "stoneplate" : "woodplate");
                entity.sendMessage(new TextComponentTranslation(key + ".on"));
            } else if (!isTriggered && isPowered) {
                final boolean stone = Material.ROCK == state.getMaterial();
                final String key = "moeswitches.message." + (stone ? "stoneplate" : "woodplate");
                entity.sendMessage(new TextComponentTranslation(key + ".off"));
            }
        }
    }

    @Override
    @Deprecated
    public IBlockState getActualState(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        final TileEntity tileEntity = access.getTileEntity(pos);
        if (tileEntity instanceof BlockEntityPlated) {
            final EnumFacing facing = ((BlockEntityPlated) tileEntity).getFacing();
            return state.withProperty(WORKAROUND_FACING, facing);
        }
        return state;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean hasTileEntity(final IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(final World world, final IBlockState state) {
        return new BlockEntityPlated(state.getValue(WORKAROUND_FACING));
    }
}
