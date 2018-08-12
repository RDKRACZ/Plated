package net.insomniakitten.plated.compat.vanilla;

import net.insomniakitten.plated.client.StateMapperPlated;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate.Sensitivity;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public final class CompatVanilla {
    private CompatVanilla() {}

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new BlockPressurePlatePlatedVanilla("stone_pressure_plate", "pressurePlateStone", Material.ROCK, SoundType.STONE, Sensitivity.MOBS));
        registry.register(new BlockPressurePlatePlatedVanilla("wooden_pressure_plate", "pressurePlateWood", Material.WOOD, SoundType.WOOD, Sensitivity.EVERYTHING));
        registry.register(new BlockPressurePlateWeightedPlatedVanilla("light_weighted_pressure_plate", "weightedPlate_light", Material.IRON, SoundType.WOOD, 15, MapColor.GOLD));
        registry.register(new BlockPressurePlateWeightedPlatedVanilla("heavy_weighted_pressure_plate", "weightedPlate_heavy", Material.IRON, SoundType.WOOD, 150, MapColor.IRON));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onModelRegistry(ModelRegistryEvent event) {
        StateMapperPlated.registerFor(Blocks.STONE_PRESSURE_PLATE);
        StateMapperPlated.registerFor(Blocks.WOODEN_PRESSURE_PLATE);
        StateMapperPlated.registerFor(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
        StateMapperPlated.registerFor(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
    }

}
