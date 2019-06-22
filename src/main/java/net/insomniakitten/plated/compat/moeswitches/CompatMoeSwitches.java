/*
 * Copyright (C) 2018 InsomniaKitten
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.insomniakitten.plated.compat.moeswitches;

import net.insomniakitten.plated.client.ModelResourcePlated;
import net.insomniakitten.plated.client.StateMapperPlated;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

@GameRegistry.ObjectHolder("moeswitches")
public final class CompatMoeSwitches {
    public static final Block MOEPLATE_WOODEN = Blocks.AIR;
    public static final Block MOEPLATE_STONE = Blocks.AIR;

    private static final PropertyDirection FACING_PROPERTY;
    private static final MethodHandle CHAT_RATE_HANDLE;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            final Class<?> moePlate = Class.forName("com.bafomdad.moeswitches.blocks.MoePlate");
            final Field facing = moePlate.getDeclaredField("FACING");
            FACING_PROPERTY = (PropertyDirection) facing.get(null);
            final Class<?> config = Class.forName("com.bafomdad.moeswitches.MoeSwitches$MoeConfig");
            final Field chatRate = config.getDeclaredField("chatRate");
            CHAT_RATE_HANDLE = lookup.unreflectGetter(chatRate);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("I blame anzu", e);
        }
    }

    private CompatMoeSwitches() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterBlocks(final RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new BlockPressurePlatePlatedMoe(Moeterial.WOODEN));
        registry.register(new BlockPressurePlatePlatedMoe(Moeterial.STONE));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterModels(final ModelRegistryEvent event) {
        StateMapperPlated.registerFor(CompatMoeSwitches.MOEPLATE_WOODEN);
        StateMapperPlated.registerFor(CompatMoeSwitches.MOEPLATE_STONE);
        ModelResourcePlated.registerFor(CompatMoeSwitches.MOEPLATE_WOODEN, "inventory");
        ModelResourcePlated.registerFor(CompatMoeSwitches.MOEPLATE_STONE, "inventory");
    }

    static PropertyDirection getFacingProperty() {
        return CompatMoeSwitches.FACING_PROPERTY;
    }

    static int getChatRate() {
        try {
            return (int) CompatMoeSwitches.CHAT_RATE_HANDLE.invokeExact();
        } catch (final Throwable e) {
            throw new IllegalStateException("Failed to get 'chatRate' int value", e);
        }
    }

}
