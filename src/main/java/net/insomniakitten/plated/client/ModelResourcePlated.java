package net.insomniakitten.plated.client;

import net.insomniakitten.plated.Plated;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public final class ModelResourcePlated extends ModelResourceLocation {
    private ModelResourcePlated(final ResourceLocation name, final String variant) {
        super(new ResourceLocation(Plated.ID, name.getNamespace() + "/" + name.getPath()), variant);
    }

    public static void registerFor(final Item item, final String variant) {
        @Nullable final ResourceLocation registryName = item.getRegistryName();
        Objects.requireNonNull(registryName, "registryName");
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourcePlated(registryName, variant));
    }

    public static void registerFor(final Block block,  final String variant) {
        ModelResourcePlated.registerFor(Item.getItemFromBlock(block), variant);
    }
}
