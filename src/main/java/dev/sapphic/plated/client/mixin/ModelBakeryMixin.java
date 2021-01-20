/*
 * Copyright 2021 Chloe Dawn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.sapphic.plated.client.mixin;

import com.google.common.collect.ObjectArrays;
import com.mojang.datafixers.util.Pair;
import dev.sapphic.plated.PressurePlates;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.client.renderer.model.BlockModelDefinition;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.Variant;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.resources.IResource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ModelBakery.class)
@OnlyIn(Dist.CLIENT)
abstract class ModelBakeryMixin {
  @Unique
  private static final Pattern BLOCK_STATE_KEY_PATTERN = Pattern.compile("blockstates/(?<key>.*)[.]json");

  @Unique
  private static final Direction[] DIRECTIONS = Direction.values();

  @Unique
  private static @Nullable ResourceLocation resolve(final ResourceLocation rl) {
    final Matcher matcher = BLOCK_STATE_KEY_PATTERN.matcher(rl.getPath());

    if (matcher.find()) {
      return ResourceLocation.tryCreate(rl.getNamespace() + ':' + matcher.group("key"));
    }

    return null;
  }

  @Unique
  private static Variant cloneRotated(final Variant variant, final TransformationMatrix rotation) {
    return new Variant(variant.getModelLocation(), rotation, variant.isUvLock(), variant.getWeight());
  }

  @Unique
  private static String withFacing(final Direction dir, final String variant) {
    final String facing = PressurePlates.FACING.getName() + '=' + dir.getString();
    final String[] variants = ObjectArrays.concat(facing, variant.split("[,]"));

    Arrays.sort(variants);

    return String.join(",", variants);
  }

  @SuppressWarnings("UnresolvedMixinReference") // IDE plugin cannot resolve synthetic targets
  @ModifyVariable(
    method = "lambda$loadBlockstate$17(Lnet/minecraft/resources/IResource;)Lcom/mojang/datafixers/util/Pair;",
    require = 1, allow = 1,
    at = @At("RETURN"))
  private Pair<String, BlockModelDefinition> applyPressurePlateRotation(final Pair<String, BlockModelDefinition> pair, final IResource resource) {
    if (ForgeRegistries.BLOCKS.getValue(resolve(resource.getLocation())) instanceof AbstractPressurePlateBlock) {
      final Map<String, VariantList> ogs = pair.getSecond().getVariants();
      final Map<String, VariantList> ngs = new HashMap<>(ogs.size() * 6);

      for (final Direction dir : DIRECTIONS) {
        final TransformationMatrix rot = new TransformationMatrix(null, dir.getRotation(), null, null);

        for (final Map.Entry<String, VariantList> e : ogs.entrySet()) {
          final List<Variant> ovs = e.getValue().getVariantList();
          final List<Variant> nvs = new ArrayList<>(ovs.size() * 6);

          for (final Variant variant : ovs) {
            nvs.add(cloneRotated(variant, rot));
          }

          ngs.put(withFacing(dir, e.getKey()), new VariantList(nvs));
        }
      }

      ogs.clear();
      ogs.putAll(ngs);
    }

    return pair;
  }
}
