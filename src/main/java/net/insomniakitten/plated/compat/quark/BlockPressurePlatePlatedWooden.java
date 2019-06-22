package net.insomniakitten.plated.compat.quark;

import net.insomniakitten.plated.block.BlockPressurePlatePlated;
import net.insomniakitten.plated.util.RegistryHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public final class BlockPressurePlatePlatedWooden extends BlockPressurePlatePlated {
    BlockPressurePlatePlatedWooden(final String name) {
        super(Material.WOOD, SoundType.WOOD, Sensitivity.EVERYTHING);
        RegistryHelper.setRegistryName("quark", this, name);
        this.setTranslationKey("quark:" + name);
        this.setHardness(0.5F);
    }
}
