package slimeknights.tconstruct.world.block;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;

public class SlimeGrassBlock extends SnowyDirtBlock implements BonemealableBlock {
  @Getter
  private final FoliageType foliageType;
  public SlimeGrassBlock(Properties properties, FoliageType foliageType) {
    super(properties);
    this.foliageType = foliageType;
  }

  /* Bonemeal interactions */

  @Override
  public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
    return world.getBlockState(pos.above()).isAir();
  }

  @Override
  public boolean isBonemealSuccess(Level worldIn, RandomSource rand, BlockPos pos, BlockState state) {
    return true;
  }

  /**
   * Shared logic to grow slimy grass from bonemeal
   * @param world            World instance
   * @param rand             Random instnace
   * @param pos              Position to grow
   * @param validBase        Tag of valid base blocks to grow on
   * @param foliageType      Foliage type to grow
   * @param includeSapling   If true, sapling may be grown
   * @param spread           If true, spreads foliage to relevant dirt blocks
   */
  public static void growGrass(ServerLevel world, RandomSource rand, BlockPos pos, TagKey<Block> validBase, FoliageType foliageType, boolean includeSapling, boolean spread) {
    // based on vanilla logic, reimplemented to switch plant types
    BlockPos up = pos.above();
    mainLoop:
    for (int i = 0; i < 128; i++) {
      // locate target
      BlockPos target = up;
      for (int j = 0; j < i / 16; j++) {
        target = target.offset(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);
        BlockPos below = target.below();
        BlockState belowState = world.getBlockState(below);
        // stop if opaque above
        if (world.getBlockState(target).isCollisionShapeFullBlock(world, target)) {
          continue mainLoop;
        }
        // spread if requested
        if (spread && TinkerWorld.allDirt.contains(belowState.getBlock())) {
          BlockState grassState = getStateFromDirt(belowState, foliageType);
          if (grassState != null) {
            world.setBlockAndUpdate(below, grassState);
          }
          continue mainLoop;
        }
        // stop if not a valid base block
        if (!belowState.is(validBase)) {
          continue mainLoop;
        }
      }
      // grow the plants if empty
      if (world.isEmptyBlock(target)) {
        BlockState plantState;
        int plant = rand.nextInt(32);
        if (plant == 0 && includeSapling) {
          plantState = TinkerWorld.slimeSapling.get(foliageType).defaultBlockState();
        } else if (plant < 6) {
          plantState = TinkerWorld.slimeFern.get(foliageType).defaultBlockState();
        } else {
          plantState = TinkerWorld.slimeTallGrass.get(foliageType).defaultBlockState();
        }
        if (plantState.canSurvive(world, target)) {
          world.setBlock(target, plantState, 3);
        }
      }
    }
  }

  @Override
  public void performBonemeal(ServerLevel world, RandomSource rand, BlockPos pos, BlockState state) {
    growGrass(world, rand, pos, TinkerTags.Blocks.SLIMY_GRASS, foliageType, false, false);
  }

  /* Spreading */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
    // based on vanilla logic, reimplemented to remove dirt hardcode
    // prevent loading unloaded chunks
    if (!world.isAreaLoaded(pos, 3)) return;

    // if this is no longer valid grass, destroy
    if (!isValidPos(state, world, pos)) {
      world.setBlockAndUpdate(pos, getDirtState(state));
    } else if (world.getMaxLocalRawBrightness(pos.above()) >= 9) {
      // otherwise, attempt spreading
      for (int i = 0; i < 4; ++i) {
        BlockPos newGrass = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
        BlockState newState = getStateFromDirt(world.getBlockState(newGrass), foliageType);
        if (newState != null && canSpread(newState, world, newGrass)) {
          world.setBlockAndUpdate(newGrass, newState.setValue(SNOWY, world.getBlockState(newGrass.above()).is(Blocks.SNOW)));
        }
      }
    }
  }

  /** Checks if the position can be slime grass */
  private static boolean isValidPos(BlockState targetState, LevelReader world, BlockPos pos) {
    BlockPos above = pos.above();
    BlockState aboveState = world.getBlockState(above);
    // under snow is fine
    if (aboveState.is(Blocks.SNOW) && aboveState.getValue(SnowLayerBlock.LAYERS) == 1) {
      return true;
    }
    // under liquid is not fine
    if (aboveState.getFluidState().getAmount() == 8) {
      return false;
    }
    // fallback to light level check
    return LightEngine.getLightBlockInto(world, targetState, pos, aboveState, above, Direction.UP, aboveState.getLightBlock(world, above)) < world.getMaxLightLevel();
  }

  /** Checks if the grass at the given position can spread */
  private static boolean canSpread(BlockState state, LevelReader world, BlockPos pos) {
    BlockPos above = pos.above();
    return isValidPos(state, world, pos) && !world.getFluidState(above).is(FluidTags.WATER);
  }


  /* Helpers */

  /**
   * Gets the dirt state for the given grass state
   * @param grassState  Grass state
   * @return Dirt state
   */
  public static BlockState getDirtState(BlockState grassState) {
    Block block = grassState.getBlock();
    for (DirtType type : DirtType.values()) {
      if (TinkerWorld.slimeGrass.get(type).contains(block)) {
        return TinkerWorld.allDirt.get(type).defaultBlockState();
      }
    }
    // includes vanilla slime grass
    return Blocks.DIRT.defaultBlockState();
  }

  /**
   * Gets the grass state for this plus the given dirt state
   * @param dirtState  dirt state
   * @return Grass state, null if cannot spread there
   */
  @Nullable
  public static BlockState getStateFromDirt(BlockState dirtState, FoliageType foliageType) {
    Block block = dirtState.getBlock();
    for (DirtType type : DirtType.values()) {
      if (TinkerWorld.allDirt.get(type) == block) {
        return TinkerWorld.slimeGrass.get(type).get(foliageType).defaultBlockState();
      }
    }
    return null;
  }
}
