package dev.sapphic.plated;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.MoreCollectors;
import dev.sapphic.plated.block.DirectionalPressurePlate;
import dev.sapphic.plated.block.DirectionalWeightedPressurePlate;
import dev.sapphic.plated.block.FacingDataBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockPressurePlate.Sensitivity;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Arrays;

@EventBusSubscriber
@Mod(modid = PressurePlates.PLATED, acceptedMinecraftVersions = "[1.12,1.13)", useMetadata = true)
public final class PressurePlates {
  public static final PropertyDirection FACING = PropertyDirection.create("facing");

  public static final ImmutableMap<EnumFacing, AxisAlignedBB> AABBS =
    Maps.immutableEnumMap(ImmutableMap.<EnumFacing, AxisAlignedBB>builder()
      .put(EnumFacing.DOWN, new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.0625, 0.9375))
      .put(EnumFacing.UP, new AxisAlignedBB(0.0625, 0.9375, 0.0625, 0.9375, 1.0, 0.9375))
      .put(EnumFacing.NORTH, new AxisAlignedBB(0.0625, 0.0625, 0.0, 0.9375, 0.9375, 0.0625))
      .put(EnumFacing.SOUTH, new AxisAlignedBB(0.0625, 0.0625, 0.9375, 0.9375, 0.9375, 1.0))
      .put(EnumFacing.WEST, new AxisAlignedBB(0.0, 0.0625, 0.0625, 0.0625, 0.9375, 0.9375))
      .put(EnumFacing.EAST, new AxisAlignedBB(0.9375, 0.0625, 0.0625, 1.0, 0.9375, 0.9375))
      .build());

  public static final ImmutableMap<EnumFacing, AxisAlignedBB> PRESSED_AABBS =
    Maps.immutableEnumMap(ImmutableMap.<EnumFacing, AxisAlignedBB>builder()
      .put(EnumFacing.DOWN, new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.03125, 0.9375))
      .put(EnumFacing.UP, new AxisAlignedBB(0.0625, 0.96875, 0.0625, 0.9375, 1.0, 0.9375))
      .put(EnumFacing.NORTH, new AxisAlignedBB(0.0625, 0.0625, 0.0, 0.9375, 0.9375, 0.03125))
      .put(EnumFacing.SOUTH, new AxisAlignedBB(0.0625, 0.0625, 0.96875, 0.9375, 0.9375, 1.0))
      .put(EnumFacing.WEST, new AxisAlignedBB(0.0, 0.0625, 0.0625, 0.03125, 0.9375, 0.9375))
      .put(EnumFacing.EAST, new AxisAlignedBB(0.96875, 0.0625, 0.0625, 1.0, 0.9375, 0.9375))
      .build());

  public static final ImmutableMap<EnumFacing, AxisAlignedBB> TOUCH_AABBS =
    Maps.immutableEnumMap(ImmutableMap.<EnumFacing, AxisAlignedBB>builder()
      .put(EnumFacing.DOWN, new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 0.25, 0.875))
      .put(EnumFacing.UP, new AxisAlignedBB(0.125, 0.75, 0.125, 0.875, 1.0, 0.875))
      .put(EnumFacing.NORTH, new AxisAlignedBB(0.125, 0.125, 0.0, 0.875, 0.875, 0.25))
      .put(EnumFacing.SOUTH, new AxisAlignedBB(0.125, 0.125, 0.75, 0.875, 0.875, 1.0))
      .put(EnumFacing.WEST, new AxisAlignedBB(0.0, 0.125, 0.125, 0.25, 0.875, 0.875))
      .put(EnumFacing.EAST, new AxisAlignedBB(0.75, 0.125, 0.125, 1.0, 0.875, 0.875))
      .build());

  static final String PLATED = "plated";
  static final String MINECRAFT = "minecraft";
  static final String MINECRAFTBOOM = "minecraftboom";
  static final String MOESWITCHES = "moeswitches";
  static final String QUARK = "quark";
  static final String THEBETWEENLANDS = "thebetweenlands";

  private static final MethodHandle CREATIVE_TAB_LABEL;

  static {
    final FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
    final String owner = remapper.unmap(Type.getInternalName(CreativeTabs.class));
    final String fieldName = remapper.mapFieldName(owner, "field_78034_o", null);

    try {
      final Field field = CreativeTabs.class.getDeclaredField(fieldName);
      field.setAccessible(true);
      CREATIVE_TAB_LABEL = MethodHandles.lookup().unreflectGetter(field);
    } catch (final IllegalAccessException | NoSuchFieldException e) {
      throw new IllegalStateException(e);
    }
  }

  public static boolean shouldBreak(final IBlockState state, final World world, final BlockPos pos) {
    return !canSurvive(state.getValue(FACING).getOpposite(), world, pos);
  }

  public static boolean canSurvive(final EnumFacing facing, final World world, final BlockPos pos) {
    final BlockPos offset = pos.offset(facing.getOpposite());
    final IBlockState neighbor = world.getBlockState(offset);

    if (DirectionalPressurePlate.PLACEMENT_EXCEPTIONS.test(neighbor.getBlock())) {
      return false;
    }

    return (facing.getAxis().isVertical() && (neighbor.getBlock() instanceof BlockFence))
      || (neighbor.getBlockFaceShape(world, offset, facing) == BlockFaceShape.SOLID);
  }

  public static void updateNeighbors(final Block self, final IBlockState state, final World world, final BlockPos pos) {
    world.notifyNeighborsOfStateChange(pos, self, false);
    world.notifyNeighborsOfStateChange(pos.offset(state.getValue(FACING).getOpposite()), self, false);
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void registerBlocks(final RegistryEvent.Register<Block> event) {
    GameRegistry.registerTileEntity(FacingDataBlockEntity.class, new ResourceLocation(PLATED, "facing_data"));

    final IForgeRegistry<Block> registry = event.getRegistry();

    register(registry, new DirectionalPressurePlate(Material.ROCK, Sensitivity.MOBS)
      .setSoundType(SoundType.WOOD)
      .setRegistryName(MINECRAFT, "stone_pressure_plate")
      .setTranslationKey("pressurePlateStone")
      .setCreativeTab(CreativeTabs.REDSTONE)
      .setHardness(0.5F));

    register(registry, new DirectionalPressurePlate(Material.WOOD, Sensitivity.EVERYTHING)
      .setSoundType(SoundType.WOOD)
      .setRegistryName(MINECRAFT, "wooden_pressure_plate")
      .setTranslationKey("pressurePlateWood")
      .setCreativeTab(CreativeTabs.REDSTONE)
      .setHardness(0.5F));

    register(registry, new DirectionalWeightedPressurePlate(Material.WOOD, 15, MapColor.GOLD)
      .setSoundType(SoundType.WOOD)
      .setRegistryName(MINECRAFT, "light_weighted_pressure_plate")
      .setTranslationKey("weightedPlate_light")
      .setCreativeTab(CreativeTabs.REDSTONE)
      .setHardness(0.5F));

    register(registry, new DirectionalWeightedPressurePlate(Material.WOOD, 150)
      .setSoundType(SoundType.WOOD)
      .setRegistryName(MINECRAFT, "heavy_weighted_pressure_plate")
      .setTranslationKey("weightedPlate_heavy")
      .setCreativeTab(CreativeTabs.REDSTONE)
      .setHardness(0.5F));

    if (Loader.isModLoaded(MINECRAFTBOOM)) {
      final CreativeTabs minecraftBoomTab = creativeTab("minecraftboom_tab");

      register(registry, new DirectionalPressurePlate(Material.WOOD, Sensitivity.EVERYTHING)
        .setSoundType(SoundType.WOOD)
        .setRegistryName(MINECRAFTBOOM, "spruce_pressure_plate")
        .setTranslationKey(MINECRAFTBOOM + ".spruce_pressure_plate")
        .setCreativeTab(minecraftBoomTab)
        .setHardness(0.5F));

      register(registry, new DirectionalPressurePlate(Material.WOOD, Sensitivity.EVERYTHING)
        .setSoundType(SoundType.WOOD)
        .setRegistryName(MINECRAFTBOOM, "birch_pressure_plate")
        .setTranslationKey(MINECRAFTBOOM + ".birch_pressure_plate")
        .setCreativeTab(minecraftBoomTab)
        .setHardness(0.5F));

      register(registry, new DirectionalPressurePlate(Material.WOOD, Sensitivity.EVERYTHING)
        .setSoundType(SoundType.WOOD)
        .setRegistryName(MINECRAFTBOOM, "jungle_pressure_plate")
        .setTranslationKey(MINECRAFTBOOM + ".jungle_pressure_plate")
        .setCreativeTab(minecraftBoomTab)
        .setHardness(0.5F));

      register(registry, new DirectionalPressurePlate(Material.WOOD, Sensitivity.EVERYTHING)
        .setSoundType(SoundType.WOOD)
        .setRegistryName(MINECRAFTBOOM, "acacia_pressure_plate")
        .setTranslationKey(MINECRAFTBOOM + ".acacia_pressure_plate")
        .setCreativeTab(minecraftBoomTab)
        .setHardness(0.5F));

      register(registry, new DirectionalPressurePlate(Material.WOOD, Sensitivity.EVERYTHING)
        .setSoundType(SoundType.WOOD)
        .setRegistryName(MINECRAFTBOOM, "dark_oak_pressure_plate")
        .setTranslationKey(MINECRAFTBOOM + ".dark_oak_pressure_plate")
        .setCreativeTab(minecraftBoomTab)
        .setHardness(0.5F));
    }

    if (Loader.isModLoaded(QUARK)) {
      if (registry.containsKey(new ResourceLocation(QUARK, "spruce_pressure_plate"))) {
        register(registry, new DirectionalPressurePlate(Material.WOOD, Sensitivity.EVERYTHING)
          .setSoundType(SoundType.WOOD)
          .setRegistryName(QUARK, "spruce_pressure_plate")
          .setTranslationKey("spruce_pressure_plate")
          .setCreativeTab(CreativeTabs.REDSTONE)
          .setHardness(0.5F));

        register(registry, new DirectionalPressurePlate(Material.WOOD, Sensitivity.EVERYTHING)
          .setSoundType(SoundType.WOOD)
          .setRegistryName(QUARK, "birch_pressure_plate")
          .setTranslationKey("birch_pressure_plate")
          .setCreativeTab(CreativeTabs.REDSTONE)
          .setHardness(0.5F));

        register(registry, new DirectionalPressurePlate(Material.WOOD, Sensitivity.EVERYTHING)
          .setSoundType(SoundType.WOOD)
          .setRegistryName(QUARK, "jungle_pressure_plate")
          .setTranslationKey("jungle_pressure_plate")
          .setCreativeTab(CreativeTabs.REDSTONE)
          .setHardness(0.5F));

        register(registry, new DirectionalPressurePlate(Material.WOOD, Sensitivity.EVERYTHING)
          .setSoundType(SoundType.WOOD)
          .setRegistryName(QUARK, "acacia_pressure_plate")
          .setTranslationKey("acacia_pressure_plate")
          .setCreativeTab(CreativeTabs.REDSTONE)
          .setHardness(0.5F));

        register(registry, new DirectionalPressurePlate(Material.WOOD, Sensitivity.EVERYTHING)
          .setSoundType(SoundType.WOOD)
          .setRegistryName(QUARK, "dark_oak_pressure_plate")
          .setTranslationKey("dark_oak_pressure_plate")
          .setCreativeTab(CreativeTabs.REDSTONE)
          .setHardness(0.5F));
      }

      if (registry.containsKey(new ResourceLocation(QUARK, "obsidian_pressure_plate"))) {
        register(registry, new DirectionalPressurePlate(Material.ROCK, EntityPlayer.class)
          .setSoundType(SoundType.STONE)
          .setRegistryName(QUARK, "obsidian_pressure_plate")
          .setTranslationKey("obsidian_pressure_plate")
          .setCreativeTab(CreativeTabs.REDSTONE)
          .setHardness(0.5F));
      }
    }

    if (Loader.isModLoaded(THEBETWEENLANDS)) {
      final CreativeTabs theBetweenlandsTab = creativeTab("thebetweenlands.block");

      register(registry, new DirectionalPressurePlate(Material.WOOD, Sensitivity.EVERYTHING)
        .setSoundType(SoundType.WOOD)
        .setRegistryName(THEBETWEENLANDS, "weedwood_plank_pressure_plate")
        .setTranslationKey(THEBETWEENLANDS + ".weedwood_plank_pressure_plate")
        .setCreativeTab(theBetweenlandsTab)
        .setHardness(2.0F)
        .setResistance(5.0F));

      register(registry, new DirectionalPressurePlate(Material.ROCK, Sensitivity.MOBS)
        .setSoundType(SoundType.STONE)
        .setRegistryName(THEBETWEENLANDS, "betweenstone_pressure_plate")
        .setTranslationKey(THEBETWEENLANDS + ".betweenstone_pressure_plate")
        .setCreativeTab(theBetweenlandsTab)
        .setHardness(1.5F)
        .setResistance(10.0F));

      register(registry, new DirectionalPressurePlate(Material.IRON, EntityPlayer.class)
        .setSoundType(SoundType.METAL)
        .setRegistryName(THEBETWEENLANDS, "syrmorite_pressure_plate")
        .setTranslationKey(THEBETWEENLANDS + ".syrmorite_pressure_plate")
        .setCreativeTab(theBetweenlandsTab)
        .setHardness(1.5F)
        .setResistance(10.0F));
    }
  }

  private static void register(final IForgeRegistry<Block> registry, final Block block) {
    if (!registry.containsKey(block.getRegistryName())) {
      throw new IllegalArgumentException(String.valueOf(block.getRegistryName()));
    }

    registry.register(block);
  }

  private static CreativeTabs creativeTab(final String label) {
    return Arrays.stream(CreativeTabs.CREATIVE_TAB_ARRAY).filter(tab -> {
      try {
        return ((String) CREATIVE_TAB_LABEL.invokeExact(tab)).equals(label);
      } catch (final Throwable throwable) {
        throw new IllegalStateException(throwable);
      }
    }).collect(MoreCollectors.onlyElement());
  }
}
