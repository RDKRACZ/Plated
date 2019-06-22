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

import net.minecraft.block.BlockPressurePlate.Sensitivity;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import java.util.Locale;

enum Moeterial {
    WOODEN(Material.WOOD, SoundType.WOOD, Sensitivity.EVERYTHING),
    STONE(Material.ROCK, SoundType.STONE, Sensitivity.MOBS);

    private final Material material;
    private final SoundType soundType;
    private final Sensitivity sensitivity;

    Moeterial(final Material material, final SoundType soundType, final Sensitivity sensitivity) {
        this.material = material;
        this.soundType = soundType;
        this.sensitivity = sensitivity;
    }

    public final Material getMaterial() {
        return this.material;
    }

    public final SoundType getSoundType() {
        return this.soundType;
    }

    public final Sensitivity getSensitivity() {
        return this.sensitivity;
    }

    @Override
    public final String toString() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}