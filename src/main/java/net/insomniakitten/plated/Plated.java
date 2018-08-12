package net.insomniakitten.plated;

import net.insomniakitten.plated.compat.CompatMinecraftBoom;
import net.insomniakitten.plated.compat.CompatQuark;
import net.insomniakitten.plated.compat.CompatTheBetweenlands;
import net.insomniakitten.plated.compat.CompatVanilla;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Plated.ID, acceptedMinecraftVersions = "[1.12,1.13)", useMetadata = true)
public final class Plated {
    public static final String ID = "plated";

    private static final Plated INSTANCE = new Plated();

    private Plated() {}

    @Mod.InstanceFactory
    public static Plated getInstance() {
        return Plated.INSTANCE;
    }

    @Mod.EventHandler
    public void onPreInitialization(final FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(CompatVanilla.class);
        if (Loader.isModLoaded("minecraftboom")) {
            MinecraftForge.EVENT_BUS.register(CompatMinecraftBoom.class);
        }
        if (Loader.isModLoaded("quark")) {
            MinecraftForge.EVENT_BUS.register(CompatQuark.class);
        }
        if (Loader.isModLoaded("thebetweenlands")) {
            MinecraftForge.EVENT_BUS.register(CompatTheBetweenlands.class);
        }
    }
}
