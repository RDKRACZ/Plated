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

package dev.sapphic.plated;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.fml.common.Mod;

/**
 * Reference constants used by pressure plate mixins
 */
@Mod("plated")
public final class PressurePlates {
  /**
   * The property that represents the new facing direction of pressure plates
   */
  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  /**
   * The property that represents the new waterlogging state of pressure plates
   */
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  /**
   * A collection of {@link VoxelShape} bounding boxes that represent
   * each orientation of a unpressed (unpowered) plate's interaction shape
   */
  public static final ImmutableMap<Direction, VoxelShape> AABBS =
    Maps.immutableEnumMap(ImmutableMap.<Direction, VoxelShape>builder()
      .put(Direction.DOWN, Block.makeCuboidShape(1.0, 15.0, 1.0, 15.0, 16.0, 15.0))
      .put(Direction.UP, Block.makeCuboidShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0))
      .put(Direction.NORTH, Block.makeCuboidShape(1.0, 1.0, 15.0, 15.0, 15.0, 16.0))
      .put(Direction.SOUTH, Block.makeCuboidShape(1.0, 1.0, 0.0, 15.0, 15.0, 1.0))
      .put(Direction.WEST, Block.makeCuboidShape(15.0, 1.0, 1.0, 16.0, 15.0, 15.0))
      .put(Direction.EAST, Block.makeCuboidShape(0.0, 1.0, 1.0, 1.0, 15.0, 15.0))
      .build());

  /**
   * A collection of {@link VoxelShape} bounding boxes that represent
   * each orientation of a pressed (powered) plate's interaction shape
   */
  public static final ImmutableMap<Direction, VoxelShape> PRESSED_AABBS =
    Maps.immutableEnumMap(ImmutableMap.<Direction, VoxelShape>builder()
      .put(Direction.DOWN, Block.makeCuboidShape(1.0, 15.5, 1.0, 15.0, 16.0, 15.0))
      .put(Direction.UP, Block.makeCuboidShape(1.0, 0.0, 1.0, 15.0, 0.5, 15.0))
      .put(Direction.NORTH, Block.makeCuboidShape(1.0, 1.0, 15.5, 15.0, 15.0, 16.0))
      .put(Direction.SOUTH, Block.makeCuboidShape(1.0, 1.0, 0.0, 15.0, 15.0, 0.5))
      .put(Direction.WEST, Block.makeCuboidShape(15.5, 1.0, 1.0, 16.0, 15.0, 15.0))
      .put(Direction.EAST, Block.makeCuboidShape(0.0, 1.0, 1.0, 0.5, 15.0, 15.0))
      .build());

  /**
   * A collection of {@link AxisAlignedBB} bounding boxes that represent
   * each orientation of a pressure plate's intersection bounds
   */
  public static final ImmutableMap<Direction, AxisAlignedBB> TOUCH_AABBS =
    Maps.immutableEnumMap(ImmutableMap.<Direction, AxisAlignedBB>builder()
      .put(Direction.DOWN, new AxisAlignedBB(0.125, 0.75, 0.125, 0.875, 1.0, 0.875))
      .put(Direction.UP, new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 0.25, 0.875))
      .put(Direction.NORTH, new AxisAlignedBB(0.125, 0.125, 0.75, 0.875, 0.875, 1.0))
      .put(Direction.SOUTH, new AxisAlignedBB(0.125, 0.125, 0.0, 0.875, 0.875, 0.25))
      .put(Direction.WEST, new AxisAlignedBB(0.75, 0.125, 0.125, 1.0, 0.875, 0.875))
      .put(Direction.EAST, new AxisAlignedBB(0.0, 0.125, 0.125, 0.25, 0.875, 0.875))
      .build());
}
