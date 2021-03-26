package slimeknights.tconstruct.world.block;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SnowyDirtBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.server.ServerWorld;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;

public class SlimeGrassBlock extends SnowyDirtBlock implements IGrowable {
  private final FoliageType foliageType;
  public SlimeGrassBlock(Properties properties, FoliageType foliageType) {
    super(properties);
    this.foliageType = foliageType;
  }

  public FoliageType getFoliageType() {
    return this.foliageType;
  }

  /* Bonemeal interactions */

  @Override
  public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
    return true;
  }

  @Override
  public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
    return true;
  }

  @Override
  public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
    BlockPos up = pos.up();
    int i = 0;

    while (i < 128) {
      BlockPos target = up;
      int j = 0;

      while (true) {
        if (j < i / 16) {
          target = target.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

          if (worldIn.getBlockState(target.down()).getBlock() == this && !worldIn.getBlockState(target).isNormalCube(worldIn, pos)) {
            ++j;
            continue;
          }
        } else if (worldIn.isAirBlock(target)) {
          BlockState plantState;

          if (rand.nextInt(8) == 0) {
            plantState = TinkerWorld.slimeFern.get(this.foliageType).getDefaultState();
          } else {
            plantState = TinkerWorld.slimeTallGrass.get(this.foliageType).getDefaultState();
          }

          if (plantState.isValidPosition(worldIn, target)) {
            worldIn.setBlockState(target, plantState, 3);
          }
        }

        ++i;
        break;
      }
    }
  }

  /* Spreading */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
    if (!worldIn.isAreaLoaded(pos, 3)) return;
    if (!canBecomeSlimeGrass(state, worldIn, pos)) {
      worldIn.setBlockState(pos, getDirtState(state));
    } else if (worldIn.getLight(pos.up()) >= 9) {
      for (int i = 0; i < 4; ++i) {
        BlockPos newGrass = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
        BlockState newState = this.getStateFromDirt(worldIn.getBlockState(newGrass));
        if (newState != null && canSlimeGrassSpread(newState, worldIn, newGrass)) {
          worldIn.setBlockState(newGrass, newState);
        }
      }
    }
  }

  private static boolean canBecomeSlimeGrass(BlockState stateIn, IWorldReader worldReader, BlockPos pos) {
    BlockPos blockpos = pos.up();
    BlockState state = worldReader.getBlockState(blockpos);
    int i = LightEngine.func_215613_a(worldReader, stateIn, pos, state, blockpos, Direction.UP, state.getOpacity(worldReader, blockpos));
    return i < worldReader.getMaxLightLevel();
  }

  private static boolean canSlimeGrassSpread(BlockState state, IWorldReader worldReader, BlockPos pos) {
    BlockPos blockpos = pos.up();
    return canBecomeSlimeGrass(state, worldReader, pos) && !worldReader.getFluidState(blockpos).isTagged(FluidTags.WATER);
  }

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
    if (this.foliageType != FoliageType.ICHOR) {
      super.fillItemGroup(group, items);
    }
  }

  @RequiredArgsConstructor
  public enum FoliageType implements IStringSerializable {
    SKY(0x00F4DA, "blue"),
    ICHOR(0xd09800, "magma"),
    ENDER(0xa92dff, "purple"),
    BLOOD(0xb80000, "blood");

    /** Original foliage types for migration */
    @Deprecated
    public static FoliageType[] ORIGINAL = {SKY, ICHOR, ENDER};

    @Getter
    private final int defaultColor;
    @Getter @Deprecated
    private final String originalName;

    @Override
    public String getString() {
      return this.toString();
    }

    @Override
    public String toString() {
      return name().toLowerCase(Locale.US);
    }
  }
}
