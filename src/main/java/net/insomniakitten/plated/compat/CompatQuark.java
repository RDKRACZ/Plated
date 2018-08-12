package net.insomniakitten.plated.compat;

import net.insomniakitten.plated.block.BlockPlatedPressurePlate;
import net.insomniakitten.plated.client.PlatedStateMapper;
import net.insomniakitten.plated.util.RegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;

@ObjectHolder("quark")
public final class CompatQuark {
    public static final Block OBSIDIAN_PRESSURE_PLATE = Blocks.AIR;

    private CompatQuark() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterBlocks(final RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new BlockDirectionalObsidianPlate("obsidian_pressure_plate"));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterModels(final ModelRegistryEvent event) {
        PlatedStateMapper.registerFor(CompatQuark.OBSIDIAN_PRESSURE_PLATE);
    }

    private static final class BlockDirectionalObsidianPlate extends BlockPlatedPressurePlate {
        private BlockDirectionalObsidianPlate(final String name) {
            super(Material.ROCK, SoundType.STONE, Sensitivity.MOBS);
            RegistryHelper.setRegistryName("quark", this, name);
            this.setTranslationKey(name);
            this.setHardness(50.0F);
            this.setResistance(2000.0F);
        }

        @Override
        protected int computeRedstoneStrength(final World world, final BlockPos pos) {
            final AxisAlignedBB box = this.getPressureBoundingBox(world.getBlockState(pos), world, pos);
            final Collection<? extends Entity> entities = world.getEntitiesWithinAABB(EntityPlayer.class, box.offset(pos));
            if (!entities.isEmpty()) {
                for (final Entity entity : entities) {
                    if (!entity.doesEntityNotTriggerPressurePlate()) {
                        return 15;
                    }
                }
            }
            return 0;
        }
    }
}
