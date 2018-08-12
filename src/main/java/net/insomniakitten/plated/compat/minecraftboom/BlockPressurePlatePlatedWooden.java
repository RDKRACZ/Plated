package net.insomniakitten.plated.compat.minecraftboom;

import net.insomniakitten.plated.block.BlockPressurePlatePlated;
import net.insomniakitten.plated.util.RegistryHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public final class BlockPressurePlatePlatedWooden extends BlockPressurePlatePlated {
    BlockPressurePlatePlatedWooden(final String name) {
        super(Material.WOOD, SoundType.WOOD, Sensitivity.EVERYTHING);
        RegistryHelper.setRegistryName("minecraftboom", this, name);
        RegistryHelper.findCreativeTab("minecraftboom_tab").ifPresent(this::setCreativeTab);
        this.setTranslationKey("minecraftboom." + name);
        this.setHardness(0.5F);
    }
}
