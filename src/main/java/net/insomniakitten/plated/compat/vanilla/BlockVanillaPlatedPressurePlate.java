package net.insomniakitten.plated.compat.vanilla;

import net.insomniakitten.plated.block.BlockPlatedPressurePlate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public final class BlockVanillaPlatedPressurePlate extends BlockPlatedPressurePlate {
    BlockVanillaPlatedPressurePlate(final String registryName, final String translationKey, final Material material, final SoundType soundType, final Sensitivity sensitivity) {
        super(material, soundType, sensitivity);
        this.setRegistryName("minecraft", registryName);
        this.setTranslationKey(translationKey);
        this.setHardness(0.5F);
    }
}
