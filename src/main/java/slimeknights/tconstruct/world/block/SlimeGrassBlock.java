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

  /* Bonemeal interactions */

  @Override
  public boolean canGrow(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
    return world.getBlockState(pos.up()).isAir(world, pos);
  }

  @Override
  public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
    return true;
  }

  @Override
  public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
    // based on vanilla logic, reimplemented to switch plant types
    BlockPos up = pos.up();
    mainLoop:
    for (int i = 0; i < 128; i++) {
      // locate target
      BlockPos target = up;
      for (int j = 0; j < i / 16; j++) {
        target = target.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);
        if (!world.getBlockState(target.down()).isIn(TinkerTags.Blocks.SLIMY_GRASS) || world.getBlockState(target).hasOpaqueCollisionShape(world, pos)) {
          continue mainLoop;
        }
      }
      // grow the plants if empty
      if (world.isAirBlock(target)) {
        BlockState plantState;
        if (rand.nextInt(8) == 0) {
          plantState = TinkerWorld.slimeFern.get(this.foliageType).getDefaultState();
        } else {
          plantState = TinkerWorld.slimeTallGrass.get(this.foliageType).getDefaultState();
        }

        if (plantState.isValidPosition(world, target)) {
          world.setBlockState(target, plantState, 3);
        }
      }
    }
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
        BlockState newState = this.getStateFromDirt(world.getBlockState(newGrass));
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
  private BlockState getStateFromDirt(BlockState dirtState) {
    Block block = dirtState.getBlock();
    for (SlimeType type : SlimeType.values()) {
      if (TinkerWorld.allDirt.get(type) == block) {
        return TinkerWorld.slimeGrass.get(type).get(this.foliageType).getDefaultState();
      }
    }
    return null;
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.foliageType != SlimeType.ICHOR) {
      super.fillItemGroup(group, items);
    }
  }
}
