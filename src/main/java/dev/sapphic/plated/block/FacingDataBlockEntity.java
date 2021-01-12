package dev.sapphic.plated.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

public final class FacingDataBlockEntity extends TileEntity {
  private static final EnumFacing[] FACINGS = EnumFacing.values();
  private static final String FACING_INDEX = "facing_index";

  private @MonotonicNonNull EnumFacing facing;

  public FacingDataBlockEntity() {
    this(EnumFacing.UP);
  }

  FacingDataBlockEntity(final EnumFacing facing) {
    this.facing = facing;
  }

  EnumFacing getFacing() {
    return this.facing;
  }

  @Override
  public void readFromNBT(final NBTTagCompound tag) {
    super.readFromNBT(tag);
    if (tag.hasKey(FACING_INDEX, NBT.TAG_INT)) {
      this.facing = EnumFacing.byIndex(tag.getInteger(FACING_INDEX));
    }
  }

  @Override
  public NBTTagCompound writeToNBT(final NBTTagCompound tag) {
    super.writeToNBT(tag);
    tag.setInteger(FACING_INDEX, this.facing.getIndex());
    return tag;
  }

  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
  }

  @Override
  public NBTTagCompound getUpdateTag() {
    return this.writeToNBT(new NBTTagCompound());
  }

  @Override
  public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity packet) {
    this.readFromNBT(packet.getNbtCompound());
  }

  @Override
  public void handleUpdateTag(final NBTTagCompound tag) {
    this.readFromNBT(tag);
  }

  @Override
  public boolean shouldRefresh(final World world, final BlockPos pos, final IBlockState prev, final IBlockState next) {
    return prev.getBlock() != next.getBlock();
  }
}
