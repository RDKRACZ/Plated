package net.insomniakitten.plated.compat.vanilla;

import net.insomniakitten.plated.block.BlockPlatedPressurePlateWeighted;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public final class BlockWeightedVanillaPlatedPressurePlate extends BlockPlatedPressurePlateWeighted {
    BlockWeightedVanillaPlatedPressurePlate(final String registryName, final String translationKey, final Material material, final SoundType soundType, final int maxWeight, final MapColor color) {
        super(material, soundType, maxWeight, color);
        this.setRegistryName("minecraft", registryName);
        this.setTranslationKey(translationKey);
        this.setHardness(0.5F);
    }
}
