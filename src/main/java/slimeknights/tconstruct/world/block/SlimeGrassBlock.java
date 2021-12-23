package slimeknights.tconstruct.world.block;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.SnowyDirtBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.server.ServerWorld;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class SlimeGrassBlock extends SnowyDirtBlock implements IGrowable {
  @Getter
  private final SlimeType foliageType;
  public SlimeGrassBlock(Properties properties, SlimeType foliageType) {
    super(properties);
    this.foliageType = foliageType;
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.foliageType != SlimeType.ICHOR) {
      super.fillItemGroup(group, items);
    }
  }

  /* Bonemeal interactions */

  @Override
  public boolean canGrow(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
    return world.getBlockState(pos.up()).isAir(world, pos);
  }

  @Override
  public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
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
  public static void growGrass(ServerWorld world, Random rand, BlockPos pos, ITag<Block> validBase, SlimeType foliageType, boolean includeSapling, boolean spread) {
    // based on vanilla logic, reimplemented to switch plant types
    BlockPos up = pos.up();
    mainLoop:
    for (int i = 0; i < 128; i++) {
      // locate target
      BlockPos target = up;
      for (int j = 0; j < i / 16; j++) {
        target = target.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);
        BlockPos below = target.down();
        BlockState belowState = world.getBlockState(below);
        // stop if opaque above
        if (world.getBlockState(target).hasOpaqueCollisionShape(world, target)) {
          continue mainLoop;
        }
        // spread if requested
        if (spread && TinkerWorld.allDirt.contains(belowState.getBlock())) {
          BlockState grassState = getStateFromDirt(belowState, foliageType);
          if (grassState != null) {
            world.setBlockState(below, grassState);
          }
          continue mainLoop;
        }
        // stop if not a valid base block
        if (!belowState.isIn(validBase)) {
          continue mainLoop;
        }
      }
      // grow the plants if empty
      if (world.isAirBlock(target)) {
        BlockState plantState;
        int plant = rand.nextInt(32);
        if (plant == 0 && includeSapling) {
          plantState = TinkerWorld.slimeSapling.get(foliageType).getDefaultState();
        } else if (plant < 6) {
          plantState = TinkerWorld.slimeFern.get(foliageType).getDefaultState();
        } else {
          plantState = TinkerWorld.slimeTallGrass.get(foliageType).getDefaultState();
        }
        if (plantState.isValidPosition(world, target)) {
          world.setBlockState(target, plantState, 3);
        }
      }
    }
  }

  @Override
  public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
    growGrass(world, rand, pos, TinkerTags.Blocks.SLIMY_GRASS, foliageType, false, false);
  }

  /* Spreading */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    // based on vanilla logic, reimplemented to remove dirt hardcode
    // prevent loading unloaded chunks
    if (!world.isAreaLoaded(pos, 3)) return;

    // if this is no longer valid grass, destroy
    if (!isValidPos(state, world, pos)) {
      world.setBlockState(pos, getDirtState(state));
    } else if (world.getLight(pos.up()) >= 9) {
      // otherwise, attempt spreading
      for (int i = 0; i < 4; ++i) {
        BlockPos newGrass = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
        BlockState newState = getStateFromDirt(world.getBlockState(newGrass), foliageType);
        if (newState != null && canSpread(newState, world, newGrass)) {
          world.setBlockState(newGrass, newState.with(SNOWY, world.getBlockState(newGrass.up()).matchesBlock(Blocks.SNOW)));
        }
      }
    }
  }

  /** Checks if the position can be slime grass */
  private static boolean isValidPos(BlockState targetState, IWorldReader world, BlockPos pos) {
    BlockPos above = pos.up();
    BlockState aboveState = world.getBlockState(above);
    // under snow is fine
    if (aboveState.matchesBlock(Blocks.SNOW) && aboveState.get(SnowBlock.LAYERS) == 1) {
      return true;
    }
    // under liquid is not fine
    if (aboveState.getFluidState().getLevel() == 8) {
      return false;
    }
    // fallback to light level check
    return LightEngine.func_215613_a(world, targetState, pos, aboveState, above, Direction.UP, aboveState.getOpacity(world, above)) < world.getMaxLightLevel();
  }

  /** Checks if the grass at the given position can spread */
  private static boolean canSpread(BlockState state, IWorldReader world, BlockPos pos) {
    BlockPos above = pos.up();
    return isValidPos(state, world, pos) && !world.getFluidState(above).isTagged(FluidTags.WATER);
  }


  /* Helpers */

  /**
   * Gets the dirt state for the given grass state
   * @param grassState  Grass state
   * @return Dirt state
   */
  public static BlockState getDirtState(BlockState grassState) {
    Block block = grassState.getBlock();
    for (SlimeType type : SlimeType.values()) {
      if (TinkerWorld.slimeGrass.get(type).contains(block)) {
        return TinkerWorld.allDirt.get(type).getDefaultState();
      }
    }
    // includes vanilla slime grass
    return Blocks.DIRT.getDefaultState();
  }

  /**
   * Gets the grass state for this plus the given dirt state
   * @param dirtState  dirt state
   * @return Grass state, null if cannot spread there
   */
  @Nullable
  public static BlockState getStateFromDirt(BlockState dirtState, SlimeType foliageType) {
    Block block = dirtState.getBlock();
    for (SlimeType type : SlimeType.values()) {
      if (TinkerWorld.allDirt.get(type) == block) {
        return TinkerWorld.slimeGrass.get(type).get(foliageType).getDefaultState();
      }
    }
    return null;
  }
}
