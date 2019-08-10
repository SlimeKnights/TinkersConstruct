package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Locale;
import java.util.Random;

public class SlimeGrassBlock extends Block implements IGrowable {

  public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;
  private final FoliageType foliageType;

  public SlimeGrassBlock(FoliageType foliageType) {
    super(Block.Properties.create(Material.ORGANIC).hardnessAndResistance(0.65F).sound(SoundType.PLANT).tickRandomly().slipperiness(0.65F));
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
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.CUTOUT_MIPPED;
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
  public void grow(World worldIn, Random rand, BlockPos pos, BlockState state) {
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
        }
        else if (worldIn.isAirBlock(blockpos2)) {
          BlockState plantState = null;

          if (rand.nextInt(8) == 0) {
            switch (this.foliageType) {
              case BLUE:
                plantState = TinkerWorld.blue_slime_fern.getDefaultState();
                break;
              case PURPLE:
                plantState = TinkerWorld.purple_slime_fern.getDefaultState();
                break;
              case ORANGE:
                plantState = TinkerWorld.orange_slime_fern.getDefaultState();
                break;
            }
          }
          else {
            switch (this.foliageType) {
              case BLUE:
                plantState = TinkerWorld.blue_slime_tall_grass.getDefaultState();
                break;
              case PURPLE:
                plantState = TinkerWorld.purple_slime_tall_grass.getDefaultState();
                break;
              case ORANGE:
                plantState = TinkerWorld.orange_slime_tall_grass.getDefaultState();
                break;
            }
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
  public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
    if (worldIn.isRemote) {
      return;
    }

    // spread to surrounding blocks
    if (worldIn.getLightSubtracted(pos.up(), 0) >= 9) {
      for (int i = 0; i < 4; ++i) {
        BlockPos blockpos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);

        if (blockpos.getY() >= 0 && blockpos.getY() < 256 && !worldIn.isBlockLoaded(blockpos)) {
          return;
        }

        BlockState iblockstate = worldIn.getBlockState(blockpos.up());
        BlockState iblockstate1 = worldIn.getBlockState(blockpos);

        if (worldIn.getLightSubtracted(blockpos.up(), 0) >= 4 && iblockstate.getOpacity(worldIn, pos.up()) <= 2) {
          this.convert(worldIn, blockpos, iblockstate1, this.foliageType);
        }
      }
    }
  }

  private void convert(World world, BlockPos pos, BlockState state, FoliageType foliageType) {
    BlockState newState = this.getStateFromDirt(state);
    if (newState != null) {
      world.setBlockState(pos, newState);
    }
  }

  private BlockState getStateFromDirt(BlockState dirtState) {
    if (dirtState.getBlock() == Blocks.DIRT) {
      switch (this.foliageType) {
        case BLUE:
          return TinkerWorld.blue_vanilla_slime_grass.getDefaultState();
        case PURPLE:
          return TinkerWorld.purple_vanilla_slime_grass.getDefaultState();
        case ORANGE:
          return TinkerWorld.orange_vanilla_slime_grass.getDefaultState();
      }
    }

    if (dirtState.getBlock() == TinkerWorld.green_slime_dirt) {
      switch (this.foliageType) {
        case BLUE:
          return TinkerWorld.blue_green_slime_grass.getDefaultState();
        case PURPLE:
          return TinkerWorld.purple_green_slime_grass.getDefaultState();
        case ORANGE:
          return TinkerWorld.orange_green_slime_grass.getDefaultState();
      }
    }
    else if (dirtState.getBlock() == TinkerWorld.blue_slime_dirt) {
      switch (this.foliageType) {
        case BLUE:
          return TinkerWorld.blue_blue_slime_grass.getDefaultState();
        case PURPLE:
          return TinkerWorld.purple_blue_slime_grass.getDefaultState();
        case ORANGE:
          return TinkerWorld.orange_blue_slime_grass.getDefaultState();
      }
    }
    else if (dirtState.getBlock() == TinkerWorld.purple_slime_dirt) {
      switch (this.foliageType) {
        case BLUE:
          return TinkerWorld.blue_purple_slime_grass.getDefaultState();
        case PURPLE:
          return TinkerWorld.purple_purple_slime_grass.getDefaultState();
        case ORANGE:
          return TinkerWorld.orange_purple_slime_grass.getDefaultState();
      }
    }
    else if (dirtState.getBlock() == TinkerWorld.magma_slime_dirt) {
      switch (this.foliageType) {
        case BLUE:
          return TinkerWorld.blue_magma_slime_grass.getDefaultState();
        case PURPLE:
          return TinkerWorld.purple_magma_slime_grass.getDefaultState();
        case ORANGE:
          return TinkerWorld.orange_magma_slime_grass.getDefaultState();
      }
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
  }
}
