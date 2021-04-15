package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.SnowyBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Locale;
import java.util.Random;

public class SlimeGrassBlock extends SnowyBlock implements Fertilizable {
  private final FoliageType foliageType;
  public SlimeGrassBlock(Settings properties, FoliageType foliageType) {
    super(properties);
    this.foliageType = foliageType;
  }

  public FoliageType getFoliageType() {
    return this.foliageType;
  }

  /* Bonemeal interactions */

  @Override
  public boolean isFertilizable(BlockView worldIn, BlockPos pos, BlockState state, boolean isClient) {
    return true;
  }

  @Override
  public boolean canGrow(World worldIn, Random rand, BlockPos pos, BlockState state) {
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

          if (worldIn.getBlockState(target.down()).getBlock() == this && !worldIn.getBlockState(target).isSolidBlock(worldIn, pos)) {
            ++j;
            continue;
          }
        } else if (worldIn.isAir(target)) {
          BlockState plantState;

          if (rand.nextInt(8) == 0) {
            plantState = TinkerWorld.slimeFern.get(this.foliageType).getDefaultState();
          } else {
            plantState = TinkerWorld.slimeTallGrass.get(this.foliageType).getDefaultState();
          }

          if (plantState.canPlaceAt(worldIn, target)) {
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
    if (!worldIn.isChunkLoaded(pos)) return;
    if (!canBecomeSlimeGrass(state, worldIn, pos)) {
      worldIn.setBlockState(pos, getDirtState(state));
    } else if (worldIn.getLightLevel(pos.up()) >= 9) {
      for (int i = 0; i < 4; ++i) {
        BlockPos newGrass = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
        BlockState newState = this.getStateFromDirt(worldIn.getBlockState(newGrass));
        if (newState != null && canSlimeGrassSpread(newState, worldIn, newGrass)) {
          worldIn.setBlockState(newGrass, newState);
        }
      }
    }
  }

  private static boolean canBecomeSlimeGrass(BlockState stateIn, WorldView worldReader, BlockPos pos) {
    BlockPos blockpos = pos.up();
    BlockState state = worldReader.getBlockState(blockpos);
    int i = ChunkLightProvider.getRealisticOpacity(worldReader, stateIn, pos, state, blockpos, Direction.UP, state.getOpacity(worldReader, blockpos));
    return i < worldReader.getMaxLightLevel();
  }

  private static boolean canSlimeGrassSpread(BlockState state, WorldView worldReader, BlockPos pos) {
    BlockPos blockpos = pos.up();
    return canBecomeSlimeGrass(state, worldReader, pos) && !worldReader.getFluidState(blockpos).isIn(FluidTags.WATER);
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
  public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> items) {
    if (this.foliageType != FoliageType.ICHOR) {
      super.addStacksForDisplay(group, items);
    }
  }

  public enum FoliageType implements StringIdentifiable {
    SKY(0xFF00F4DA, "blue"),
    ICHOR(0xFFd09800, "magma"),
    ENDER(0xFFa92dff, "purple"),
    BLOOD(0xFFb80000, "blood");

    /** Original foliage types for migration */
    @Deprecated
    public static FoliageType[] ORIGINAL = {SKY, ICHOR, ENDER};

    private final int defaultColor;
    @Deprecated
    private final String originalName;

    FoliageType(int defaultColor, String originalName) {
      this.defaultColor = defaultColor;
      this.originalName = originalName;
    }

    public int getDefaultColor() {
      return defaultColor;
    }

    @Deprecated
    public String getOriginalName() {
      return originalName;
    }

    @Override
    public String asString() {
      return this.toString();
    }

    @Override
    public String toString() {
      return name().toLowerCase(Locale.US);
    }
  }
}
