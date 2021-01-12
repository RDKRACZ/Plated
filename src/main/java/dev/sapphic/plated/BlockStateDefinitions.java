package dev.sapphic.plated;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

@EventBusSubscriber(value = Side.CLIENT, modid = PressurePlates.PLATED)
public final class BlockStateDefinitions {
  private static final Logger LOGGER = LogManager.getLogger();

  private BlockStateDefinitions() {
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void registerAll(final ModelRegistryEvent event) {
    remapDefinition(PressurePlates.MINECRAFT, "wooden_pressure_plate");
    remapDefinition(PressurePlates.MINECRAFT, "stone_pressure_plate");
    remapDefinition(PressurePlates.MINECRAFT, "light_weighted_pressure_plate");
    remapDefinition(PressurePlates.MINECRAFT, "heavy_weighted_pressure_plate");

    if (Loader.isModLoaded(PressurePlates.MINECRAFTBOOM)) {
      remapDefinition(PressurePlates.MINECRAFTBOOM, "spruce_pressure_plate");
      remapDefinition(PressurePlates.MINECRAFTBOOM, "birch_pressure_plate");
      remapDefinition(PressurePlates.MINECRAFTBOOM, "jungle_pressure_plate");
      remapDefinition(PressurePlates.MINECRAFTBOOM, "acacia_pressure_plate");
      remapDefinition(PressurePlates.MINECRAFTBOOM, "dark_oak_pressure_plate");
    }

    if (Loader.isModLoaded(PressurePlates.QUARK)) {
      if (ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(PressurePlates.QUARK, "spruce_pressure_plate"))) {
        remapDefinition(PressurePlates.QUARK, "spruce_pressure_plate");
        remapDefinition(PressurePlates.QUARK, "birch_pressure_plate");
        remapDefinition(PressurePlates.QUARK, "jungle_pressure_plate");
        remapDefinition(PressurePlates.QUARK, "acacia_pressure_plate");
        remapDefinition(PressurePlates.QUARK, "dark_oak_pressure_plate");
      }

      if (ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(PressurePlates.QUARK, "obsidian_pressure_plate"))) {
        remapDefinition(PressurePlates.QUARK, "obsidian_pressure_plate");
      }
    }

    if (Loader.isModLoaded(PressurePlates.THEBETWEENLANDS)) {
      remapDefinition(PressurePlates.THEBETWEENLANDS, "weedwood_plank_pressure_plate");
      remapDefinition(PressurePlates.THEBETWEENLANDS, "betweenstone_pressure_plate");
      remapDefinition(PressurePlates.THEBETWEENLANDS, "syrmorite_pressure_plate");
      remapItemToBlockStateDefinition(PressurePlates.THEBETWEENLANDS, "weedwood_plank_pressure_plate");
      remapItemToBlockStateDefinition(PressurePlates.THEBETWEENLANDS, "betweenstone_pressure_plate");
      remapItemToBlockStateDefinition(PressurePlates.THEBETWEENLANDS, "syrmorite_pressure_plate");
    }
  }

  private static void remapDefinition(final String modid, final String name) {
    final ResourceLocation id = new ResourceLocation(modid, name);
    final @Nullable Block block = ForgeRegistries.BLOCKS.getValue(id);

    if ((block == null) || (block == Blocks.AIR)) {
      throw new IllegalArgumentException(id.toString());
    }

    final String path = id.getNamespace() + '/' + id.getPath();
    final ResourceLocation definition = new ResourceLocation(PressurePlates.PLATED, path);

    ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
      @Override
      protected ModelResourceLocation getModelResourceLocation(final IBlockState state) {
        return new ModelResourceLocation(definition, this.getPropertyString(state.getProperties()));
      }
    });
  }

  private static void remapItemToBlockStateDefinition(final String modid, final String name) {
    final ResourceLocation id = new ResourceLocation(modid, name);
    final @Nullable Item item = ForgeRegistries.ITEMS.getValue(id);

    if ((item == null) || (item == Items.AIR)) {
      throw new IllegalArgumentException(id.toString());
    }

    final String path = id.getNamespace() + '/' + id.getPath();
    final ResourceLocation model = new ResourceLocation(PressurePlates.PLATED, path);

    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(model, "facing=down,powered=false"));
  }
}
