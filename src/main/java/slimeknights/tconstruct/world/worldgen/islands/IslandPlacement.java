package slimeknights.tconstruct.world.worldgen.islands;

import com.mojang.serialization.Codec;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.Locale;

/** Represents island height generation options */
public enum IslandPlacement implements StringRepresentable {
  /** Island that generates in the air */
  SKY {
    @Override
    int getHeight(ChunkPos chunkPos, ChunkGenerator generator, LevelHeightAccessor pLevel, Rotation rotation, RandomSource random, RandomState randomState) {
      int xOffset;
      int yOffset;
      switch (rotation) {
        case CLOCKWISE_90 -> {
          xOffset = -5;
          yOffset = 5;
        }
        case CLOCKWISE_180 -> {
          xOffset = -5;
          yOffset = -5;
        }
        case COUNTERCLOCKWISE_90 -> {
          xOffset = 5;
          yOffset = -5;
        }
        default -> {
          xOffset = 5;
          yOffset = 5;
        }
      }

      // determine height
      int x = chunkPos.getBlockX(7);
      int z = chunkPos.getBlockZ(7);
      int minXMinZ = generator.getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, pLevel, randomState);
      int minXMaxZ = generator.getFirstOccupiedHeight(x, z + yOffset, Heightmap.Types.WORLD_SURFACE_WG, pLevel, randomState);
      int maxXMinZ = generator.getFirstOccupiedHeight(x + xOffset, z, Heightmap.Types.WORLD_SURFACE_WG, pLevel, randomState);
      int maxXMaxZ = generator.getFirstOccupiedHeight(x + xOffset, z + yOffset, Heightmap.Types.WORLD_SURFACE_WG, pLevel, randomState);
      // from the smallest of the 4 positions, add 60 plus another random 50, limit to 20 blocks below world height (tallest island is 13 blocks, 7 blocks for trees)
      return Math.min(Math.min(Math.min(minXMinZ, minXMaxZ), Math.min(maxXMinZ, maxXMaxZ)) + 60 + random.nextInt(50), generator.getGenDepth() - 20);
    }
  },
  /** Island that generates on the ocean surface */
  SEA {
    @Override
    int getHeight(ChunkPos chunkPos, ChunkGenerator generator, LevelHeightAccessor pLevel, Rotation rotation, RandomSource random, RandomState randomState) {
      return Math.max(generator.getSeaLevel() - 7, 0);
    }

    @Override
    public boolean isPositionValid(WorldGenLevel world, BlockPos pos, ChunkGenerator generator) {
      BlockPos up = pos.above();
      if (isFluidOrEmpty(world, up)) {
        for (Direction direction : Plane.HORIZONTAL) {
          if (!isFluidOrEmpty(world, up.relative(direction))) {
            return false;
          }
        }
        return true;
      }
      return false;
    }
  };

  public static final Codec<IslandPlacement> CODEC = StringRepresentable.fromEnum(IslandPlacement::values);

  /** Checks if the given position is either empty or a fluid block */
  private static boolean isFluidOrEmpty(WorldGenLevel world, BlockPos pos) {
    return world.isEmptyBlock(pos) || world.getBlockState(pos).getBlock() instanceof LiquidBlock;
  }

  @Getter
  private final String serializedName = this.name().toLowerCase(Locale.ROOT);

  /** Checks if the given position is valid for this island */
  public boolean isPositionValid(WorldGenLevel world, BlockPos pos, ChunkGenerator generator) {
    return true;
  }

  /** Gets the height to generate this island */
  abstract int getHeight(ChunkPos chunkPos, ChunkGenerator generator, LevelHeightAccessor pLevel, Rotation rotation, RandomSource random, RandomState randomState);
}
