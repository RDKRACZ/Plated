package net.insomniakitten.plated.util;

import net.insomniakitten.plated.Plated;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@EventBusSubscriber(modid = Plated.ID)
public final class BlockEntityPlated extends TileEntity {
    private static final String KEY_FACING_INDEX = "facing_index";

    @Nullable
    private EnumFacing facing;

    @SuppressWarnings("unused")
    public BlockEntityPlated() {}

    public BlockEntityPlated(@Nullable final EnumFacing facing) {
        this.facing = facing;
    }

    @SubscribeEvent
    public static void onRegister(final RegistryEvent.Register<Block> event) {
        final ResourceLocation key = new ResourceLocation(Plated.ID, "facing_data");
        GameRegistry.registerTileEntity(BlockEntityPlated.class, key);
    }

    public EnumFacing getFacing() {
        return this.facing != null ? this.facing : EnumFacing.UP;
    }

    @Override
    public void readFromNBT(final NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(BlockEntityPlated.KEY_FACING_INDEX, Constants.NBT.TAG_INT)) {
            final int index = compound.getInteger(BlockEntityPlated.KEY_FACING_INDEX);
            this.facing = EnumFacing.byIndex(index);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (this.facing != null) {
            final int index = this.facing.getIndex();
            compound.setInteger(BlockEntityPlated.KEY_FACING_INDEX, index);
        }
        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.getPos(), 0, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        final Block block = this.getBlockType();
        final String key = block.getTranslationKey();
        return new TextComponentTranslation(key + ".name");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(final NBTTagCompound compound) {
        this.readFromNBT(compound);
    }

    @Override
    public boolean shouldRefresh(final World world, final BlockPos pos, final IBlockState oldState, final IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
