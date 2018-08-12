package net.insomniakitten.plated.compat;

import net.insomniakitten.plated.block.BlockPlatedPressurePlate;
import net.insomniakitten.plated.client.PlatedModelResource;
import net.insomniakitten.plated.client.PlatedStateMapper;
import net.insomniakitten.plated.util.RegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.Collection;

@GameRegistry.ObjectHolder("thebetweenlands")
public final class CompatTheBetweenlands {
    public static final Block WEEDWOOD_PLANK_PRESSURE_PLATE = Blocks.AIR;
    public static final Block BETWEENSTONE_PRESSURE_PLATE = Blocks.AIR;
    public static final Block SYRMORITE_PRESSURE_PLATE = Blocks.AIR;

    private CompatTheBetweenlands() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterBlocks(final RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new BlockDirectionalBetweenlandsPlate("weedwood_plank_pressure_plate", Material.WOOD, SoundType.WOOD, null));
        registry.register(new BlockDirectionalBetweenlandsPlate("betweenstone_pressure_plate", Material.ROCK, SoundType.STONE, EntityLivingBase.class));
        registry.register(new BlockDirectionalBetweenlandsPlate("syrmorite_pressure_plate", Material.IRON, SoundType.METAL, EntityPlayer.class));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterModels(final ModelRegistryEvent event) {
        PlatedStateMapper.registerFor(CompatTheBetweenlands.WEEDWOOD_PLANK_PRESSURE_PLATE);
        PlatedStateMapper.registerFor(CompatTheBetweenlands.BETWEENSTONE_PRESSURE_PLATE);
        PlatedStateMapper.registerFor(CompatTheBetweenlands.SYRMORITE_PRESSURE_PLATE);
        PlatedModelResource.registerFor(CompatTheBetweenlands.WEEDWOOD_PLANK_PRESSURE_PLATE, "facing=down,powered=false");
        PlatedModelResource.registerFor(CompatTheBetweenlands.BETWEENSTONE_PRESSURE_PLATE, "facing=down,powered=false");
        PlatedModelResource.registerFor(CompatTheBetweenlands.SYRMORITE_PRESSURE_PLATE, "facing=down,powered=false");
    }

    private static final class BlockDirectionalBetweenlandsPlate extends BlockPlatedPressurePlate {
        private final Class<? extends Entity> entityClazz;

        private BlockDirectionalBetweenlandsPlate(String name, Material material, SoundType soundType, @Nullable Class<? extends Entity> entityClazz) {
            super(material, soundType, Sensitivity.MOBS);
            this.entityClazz = entityClazz;
            RegistryHelper.setRegistryName("thebetweenlands", this, name);
            this.setTranslationKey("thebetweenlands." + name);
            this.setHardness(material == Material.WOOD ? 2.0F : 1.5F);
            this.setResistance(material == Material.WOOD ? 5.0F : 10.0F);
            RegistryHelper.findCreativeTab("thebetweenlands.block").ifPresent(this::setCreativeTab);
        }

        @Override
        protected int computeRedstoneStrength(final World world, final BlockPos pos) {
            final AxisAlignedBB box = this.getPressureBoundingBox(world.getBlockState(pos), world, pos);
            final Collection<? extends Entity> entities;
            if (this.entityClazz == null) {
                entities = world.getEntitiesWithinAABBExcludingEntity(null, box.offset(pos));
            } else {
                entities = world.getEntitiesWithinAABB(this.entityClazz, box.offset(pos));
            }
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
