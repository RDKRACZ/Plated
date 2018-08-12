package net.insomniakitten.plated.compat.thebetweenlands;

import net.insomniakitten.plated.client.ModelResourcePlated;
import net.insomniakitten.plated.client.StateMapperPlated;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@GameRegistry.ObjectHolder("thebetweenlands")
public final class CompatTheBetweenlands {
    public static final Block WEEDWOOD_PLANK_PRESSURE_PLATE = Blocks.AIR;
    public static final Block BETWEENSTONE_PRESSURE_PLATE = Blocks.AIR;
    public static final Block SYRMORITE_PRESSURE_PLATE = Blocks.AIR;

    private CompatTheBetweenlands() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterBlocks(final RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new BlockPressurePlatePlatedBetweenlands("weedwood_plank_pressure_plate", Material.WOOD, SoundType.WOOD, null));
        registry.register(new BlockPressurePlatePlatedBetweenlands("betweenstone_pressure_plate", Material.ROCK, SoundType.STONE, EntityLivingBase.class));
        registry.register(new BlockPressurePlatePlatedBetweenlands("syrmorite_pressure_plate", Material.IRON, SoundType.METAL, EntityPlayer.class));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterModels(final ModelRegistryEvent event) {
        StateMapperPlated.registerFor(CompatTheBetweenlands.WEEDWOOD_PLANK_PRESSURE_PLATE);
        StateMapperPlated.registerFor(CompatTheBetweenlands.BETWEENSTONE_PRESSURE_PLATE);
        StateMapperPlated.registerFor(CompatTheBetweenlands.SYRMORITE_PRESSURE_PLATE);
        ModelResourcePlated.registerFor(CompatTheBetweenlands.WEEDWOOD_PLANK_PRESSURE_PLATE, "facing=down,powered=false");
        ModelResourcePlated.registerFor(CompatTheBetweenlands.BETWEENSTONE_PRESSURE_PLATE, "facing=down,powered=false");
        ModelResourcePlated.registerFor(CompatTheBetweenlands.SYRMORITE_PRESSURE_PLATE, "facing=down,powered=false");
    }

}
