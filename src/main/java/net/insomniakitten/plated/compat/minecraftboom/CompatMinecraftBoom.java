package net.insomniakitten.plated.compat.minecraftboom;

import net.insomniakitten.plated.client.PlatedModelResource;
import net.insomniakitten.plated.client.PlatedStateMapper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder("minecraftboom")
public final class CompatMinecraftBoom {
    public static final Block SPRUCE_PRESSURE_PLATE = Blocks.AIR;
    public static final Block BIRCH_PRESSURE_PLATE = Blocks.AIR;
    public static final Block JUNGLE_PRESSURE_PLATE = Blocks.AIR;
    public static final Block ACACIA_PRESSURE_PLATE = Blocks.AIR;
    public static final Block DARK_OAK_PRESSURE_PLATE = Blocks.AIR;

    private CompatMinecraftBoom() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterBlocks(final RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new BlockWoodenPlatedPressurePlate("spruce_pressure_plate"));
        registry.register(new BlockWoodenPlatedPressurePlate("birch_pressure_plate"));
        registry.register(new BlockWoodenPlatedPressurePlate("jungle_pressure_plate"));
        registry.register(new BlockWoodenPlatedPressurePlate("acacia_pressure_plate"));
        registry.register(new BlockWoodenPlatedPressurePlate("dark_oak_pressure_plate"));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterModels(final ModelRegistryEvent event) {
        PlatedStateMapper.registerFor(CompatMinecraftBoom.SPRUCE_PRESSURE_PLATE);
        PlatedStateMapper.registerFor(CompatMinecraftBoom.BIRCH_PRESSURE_PLATE);
        PlatedStateMapper.registerFor(CompatMinecraftBoom.JUNGLE_PRESSURE_PLATE);
        PlatedStateMapper.registerFor(CompatMinecraftBoom.ACACIA_PRESSURE_PLATE);
        PlatedStateMapper.registerFor(CompatMinecraftBoom.DARK_OAK_PRESSURE_PLATE);
        PlatedModelResource.registerFor(CompatMinecraftBoom.SPRUCE_PRESSURE_PLATE, "facing=down,powered=false");
        PlatedModelResource.registerFor(CompatMinecraftBoom.BIRCH_PRESSURE_PLATE, "facing=down,powered=false");
        PlatedModelResource.registerFor(CompatMinecraftBoom.JUNGLE_PRESSURE_PLATE, "facing=down,powered=false");
        PlatedModelResource.registerFor(CompatMinecraftBoom.ACACIA_PRESSURE_PLATE, "facing=down,powered=false");
        PlatedModelResource.registerFor(CompatMinecraftBoom.DARK_OAK_PRESSURE_PLATE, "facing=down,powered=false");
    }
}
