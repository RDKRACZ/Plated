package net.insomniakitten.plated.compat.quark;

import net.insomniakitten.plated.block.BlockPlatedPressurePlate;
import net.insomniakitten.plated.util.RegistryHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

public final class BlockObsidianPlatedPressurePlate extends BlockPlatedPressurePlate {
    BlockObsidianPlatedPressurePlate(final String name) {
        super(Material.ROCK, SoundType.STONE, Sensitivity.MOBS);
        RegistryHelper.setRegistryName("quark", this, name);
        this.setTranslationKey(name);
        this.setHardness(50.0F);
        this.setResistance(2000.0F);
    }

    @Override
    protected int computeRedstoneStrength(final World world, final BlockPos pos) {
        final AxisAlignedBB box = this.getPressureBoundingBox(world.getBlockState(pos), world, pos);
        final Collection<? extends Entity> entities = world.getEntitiesWithinAABB(EntityPlayer.class, box.offset(pos));
        if (!entities.isEmpty()) {
            for (final Entity entity : entities) {
                if (!entity.doesEntityNotTriggerPressurePlate()) {
                    return 15;
                }
            }
        }
        return 0;
    }
}
