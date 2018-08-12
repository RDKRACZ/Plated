package net.insomniakitten.plated.util;

import com.google.common.collect.Maps;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public final class AxisDirectionalBB extends AxisAlignedBB {
    @SuppressWarnings("SuspiciousNameCombination")
    private final Map<EnumFacing, AxisAlignedBB> aabbMap = Arrays.stream(EnumFacing.VALUES)
        .collect(Maps.toImmutableEnumMap(Function.identity(), facing -> {
            switch (facing) {
                case NORTH: return new AxisAlignedBB(
                    this.minX, this.minZ, this.minY,
                    this.maxX, this.maxZ, this.maxY
                );
                case SOUTH: return new AxisAlignedBB(
                    this.minX, this.minZ, 1 - this.maxY,
                    this.maxX, this.maxZ, 1 - this.minY
                );
                case EAST: return new AxisAlignedBB(
                    1 - this.maxY, this.minZ, this.minX,
                    1 - this.minY, this.maxZ, this.maxX
                );
                case WEST: return new AxisAlignedBB(
                    this.minY, this.minZ, this.minX,
                    this.maxY, this.maxZ, this.maxX
                );
                case UP: return new AxisAlignedBB(
                    this.minX, 1 - this.maxY, this.minZ,
                    this.maxX, 1 - this.minY, this.maxZ
                );
                case DOWN: default: return this;
            }
        }));

    public AxisDirectionalBB(final AxisAlignedBB aabb) {
        super(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public AxisDirectionalBB(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        super(x1, y1, z1, x2, y2, z2);
    }

    public AxisDirectionalBB(final Vec3i vec) {
        super((double) vec.getX(), (double) vec.getY(), (double) vec.getZ(),
            (double) (vec.getX() + 1), (double) (vec.getY() + 1), (double) (vec.getZ() + 1));
    }

    public AxisDirectionalBB(final Vec3i min, final Vec3i max) {
        super((double) min.getX(), (double) min.getY(), (double) min.getZ(),
            (double) max.getX(), (double) max.getY(), (double) max.getZ());
    }

    public AxisDirectionalBB(final Vec3d vec) {
        super(vec.x, vec.y, vec.z, vec.x + 1.0, vec.y + 1.0, vec.z + 1.0);
    }

    public AxisDirectionalBB(final Vec3d min, final Vec3d max) {
        super(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public AxisAlignedBB withFacing(final EnumFacing facing) {
        return this.aabbMap.getOrDefault(facing, this);
    }
}
