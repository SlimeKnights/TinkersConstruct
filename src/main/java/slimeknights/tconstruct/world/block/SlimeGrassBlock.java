package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
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
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.shared.block.SlimeBlock;

import java.util.Locale;
import java.util.Random;

// todo: evaluate this block
public class SlimeGrassBlock extends Block implements IGrowable {

  private static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;
  private final FoliageType foliageType;

  public SlimeGrassBlock(Properties properties, FoliageType foliageType) {
    super(properties);
    this.setDefaultState(this.stateContainer.getBaseState().with(SNOWY, Boolean.FALSE));
    this.foliageType = foliageType;
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(SNOWY);
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    items.add(new ItemStack(this));
  }

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
    BlockPos blockpos1 = pos.up();
    int i = 0;

    while (i < 128) {
      BlockPos blockpos2 = blockpos1;
      int j = 0;

      while (true) {
        if (j < i / 16) {
          blockpos2 = blockpos2.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

          if (worldIn.getBlockState(blockpos2.down()).getBlock() == this && !worldIn.getBlockState(blockpos2).getBlock().isNormalCube(state, worldIn, pos)) {
            ++j;
            continue;
          }
        } else if (worldIn.isAirBlock(blockpos2)) {
          BlockState plantState = null;

          if (rand.nextInt(8) == 0) {
            plantState = WorldBlocks.slime_fern.get(this.foliageType).getDefaultState();
          } else {
            plantState = WorldBlocks.slime_tall_grass.get(this.foliageType).getDefaultState();
          }

          if (plantState != null) {
            if (plantState.isValidPosition(worldIn, blockpos2)) {
              worldIn.setBlockState(blockpos2, plantState, 3);
            }
          }
        }

        ++i;
        break;
      }
    }
  }

  public FoliageType getFoliageType() {
    return this.foliageType;
  }

  @Override
  public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
    if (!worldIn.isRemote) {
      if (!worldIn.isAreaLoaded(pos, 3)) {
        return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
      }
      if (!canBecomeSlimeGrass(state, worldIn, pos)) {
        BlockState dirtState = this.getDirtState(state);

        if (dirtState != null) {
          worldIn.setBlockState(pos, dirtState);
        }
      } else {
        if (worldIn.getLight(pos.up()) >= 9) {
          for (int i = 0; i < 4; ++i) {
            BlockPos blockpos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
            BlockState newState = this.getStateFromDirt(worldIn.getBlockState(blockpos));
            if (newState != null && canSlimeGrassSpread(newState, worldIn, blockpos)) {
              worldIn.setBlockState(blockpos, newState);
            }
          }
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

  public BlockState getDirtState(BlockState grassState) {
    if (WorldBlocks.vanilla_slime_grass.contains(grassState.getBlock())) {
      return Blocks.DIRT.getDefaultState();
    }
    if (WorldBlocks.green_slime_grass.contains(grassState.getBlock())) {
      return WorldBlocks.slime_dirt.get(SlimeDirtBlock.SlimeDirtType.GREEN).getDefaultState();
    }
    if (WorldBlocks.blue_slime_grass.contains(grassState.getBlock())) {
      return WorldBlocks.slime_dirt.get(SlimeDirtBlock.SlimeDirtType.BLUE).getDefaultState();
    }
    if (WorldBlocks.purple_slime_grass.contains(grassState.getBlock())) {
      return WorldBlocks.slime_dirt.get(SlimeDirtBlock.SlimeDirtType.PURPLE).getDefaultState();
    }
    if (WorldBlocks.magma_slime_grass.contains(grassState.getBlock())) {
      return WorldBlocks.slime_dirt.get(SlimeDirtBlock.SlimeDirtType.MAGMA).getDefaultState();
    }

    return null;
  }

  private BlockState getStateFromDirt(BlockState dirtState) {
    if (dirtState.getBlock() == Blocks.DIRT) {
      return WorldBlocks.vanilla_slime_grass.get(this.foliageType).getDefaultState();
    }

    if (dirtState.getBlock() == WorldBlocks.slime_dirt.get(SlimeDirtBlock.SlimeDirtType.GREEN)) {
      return WorldBlocks.green_slime_grass.get(this.foliageType).getDefaultState();
    } else if (dirtState.getBlock() == WorldBlocks.slime_dirt.get(SlimeDirtBlock.SlimeDirtType.BLUE)) {
      return WorldBlocks.blue_slime_grass.get(this.foliageType).getDefaultState();
    } else if (dirtState.getBlock() == WorldBlocks.slime_dirt.get(SlimeDirtBlock.SlimeDirtType.PURPLE)) {
      return WorldBlocks.purple_slime_grass.get(this.foliageType).getDefaultState();
    } else if (dirtState.getBlock() == WorldBlocks.slime_dirt.get(SlimeDirtBlock.SlimeDirtType.MAGMA)) {
      return WorldBlocks.magma_slime_grass.get(this.foliageType).getDefaultState();
    }

    return null;
  }

  public enum FoliageType implements IStringSerializable {
    BLUE,
    PURPLE,
    ORANGE;

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }


    @Override
    public String toString() {
      return name().toLowerCase(Locale.US);
    }
  }
}
