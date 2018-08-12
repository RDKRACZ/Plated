package net.insomniakitten.plated.compat.thebetweenlands;

import net.insomniakitten.plated.block.BlockPressurePlatePlated;
import net.insomniakitten.plated.util.RegistryHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;

public final class BlockPressurePlatePlatedBetweenlands extends BlockPressurePlatePlated {
    private final Class<? extends Entity> entityClazz;

    BlockPressurePlatePlatedBetweenlands(final String name, final Material material, final SoundType soundType, @Nullable final Class<? extends Entity> entityClazz) {
        super(material, soundType, Sensitivity.MOBS);
        this.entityClazz = entityClazz;
        RegistryHelper.setRegistryName("thebetweenlands", this, name);
        this.setTranslationKey("thebetweenlands." + name);
        this.setHardness(material == Material.WOOD ? 2.0F : 1.5F);
        this.setResistance(material == Material.WOOD ? 5.0F : 10.0F);
        RegistryHelper.findCreativeTab("thebetweenlands.block").ifPresent(this::setCreativeTab);
    }

    @Override
    protected int computeRedstoneStrength(final World world, final BlockPos pos) {
        final AxisAlignedBB box = this.getPressureBoundingBox(world.getBlockState(pos), world, pos);
        final Collection<? extends Entity> entities;
        if (this.entityClazz == null) {
            entities = world.getEntitiesWithinAABBExcludingEntity(null, box.offset(pos));
        } else {
            entities = world.getEntitiesWithinAABB(this.entityClazz, box.offset(pos));
        }
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
