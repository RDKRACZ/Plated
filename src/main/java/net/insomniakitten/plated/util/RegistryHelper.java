package net.insomniakitten.plated.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Optional;

public final class RegistryHelper {
    private RegistryHelper() {}

    public static <V extends IForgeRegistryEntry<V>> void setRegistryName(final ModContainer container, final V entry, final String name) {
        final ModContainer active = Loader.instance().activeModContainer();
        try {
            Loader.instance().setActiveModContainer(container);
            entry.setRegistryName(new ResourceLocation(container.getModId(), name));
        } finally {
            Loader.instance().setActiveModContainer(active);
        }
    }

    public static <V extends IForgeRegistryEntry<V>> void setRegistryName(final String modid, final V entry, final String name) {
        for (ModContainer container : Loader.instance().getActiveModList()) {
            if (container != null && modid.equals(container.getModId())) {
                RegistryHelper.setRegistryName(container, entry, name);
                return;
            }
        }
        throw new NullPointerException("No ModContainer matching modId '" + modid + "'");
    }

    public static Optional<CreativeTabs> findCreativeTab(final String label) {
        for (final CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY) {
            if (tab.tabLabel.equals(label)) {
                return Optional.of(tab);
            }
        }
        return Optional.empty();
    }

}
