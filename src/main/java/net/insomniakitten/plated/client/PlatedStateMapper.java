package net.insomniakitten.plated.client;

import net.insomniakitten.plated.Plated;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

@SideOnly(Side.CLIENT)
public final class PlatedStateMapper extends StateMapperBase {
    private final ResourceLocation modelPath;

    private PlatedStateMapper(final String domain, final ResourceLocation name) {
        String namespace = name.getNamespace();
        String path = name.getPath();
        this.modelPath = new ResourceLocation(domain, namespace + "/" + path);
    }

    public static void registerFor(final Block block) {
        final ResourceLocation registryName = block.getRegistryName();
        Objects.requireNonNull(registryName, "registryName");
        ModelLoader.setCustomStateMapper(block, new PlatedStateMapper(Plated.ID, registryName));
    }

    @Override
    protected ModelResourceLocation getModelResourceLocation(final IBlockState state) {
        return new ModelResourceLocation(this.modelPath, this.getPropertyString(state.getProperties()));
    }
}
