package slimeknights.tconstruct.world.worldgen.trees.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.common.Tags;
import slimeknights.tconstruct.world.block.SlimeDirtBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class SlimeTreeFeature extends AbstractTreeFeature<SlimeTreeFeatureConfig> {

  public SlimeTreeFeature(Function<Dynamic<?>, ? extends SlimeTreeFeatureConfig> config) {
    super(config);
  }

  @Override
  protected boolean place(IWorldGenerationReader worldIn, Random random, BlockPos blockPos, Set<BlockPos> trunkBlockPosSet, Set<BlockPos> foliageBlockPosSet, MutableBoundingBox boundingBox, SlimeTreeFeatureConfig treeFeatureConfig) {
    int height = random.nextInt(treeFeatureConfig.randomTreeHeight) + treeFeatureConfig.baseHeight;

    if (!(worldIn instanceof IWorld)) {
      return false;
    }

    blockPos = this.findGround((IWorld) worldIn, blockPos);

    if (blockPos.getY() < 0) {
      return false;
    }

    if (blockPos.getY() >= 1 && blockPos.getY() + height + 1 <= worldIn.getMaxHeight()) {
      if (isSoilOrFarm(worldIn, blockPos.down(), treeFeatureConfig.getSapling())) {
        this.setDirtAt(worldIn, blockPos.down(), blockPos);

        this.placeTrunk(worldIn, random, height, blockPos, trunkBlockPosSet, boundingBox, treeFeatureConfig);

        this.placeCanopy(worldIn, random, height, blockPos, trunkBlockPosSet, boundingBox, treeFeatureConfig);

        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  protected void placeTrunk(IWorldGenerationReader worldIn, Random randomIn, int treeHeight, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn, SlimeTreeFeatureConfig treeFeatureConfigIn) {
    while (treeHeight > 0) {
      this.func_227216_a_(worldIn, randomIn, blockPos, blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);

      blockPos = blockPos.up();
      treeHeight--;
    }
  }

  protected void placeCanopy(IWorldGenerationReader worldIn, Random randomIn, int treeHeight, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn, SlimeTreeFeatureConfig treeFeatureConfigIn) {
    blockPos = blockPos.up(treeHeight);
    for (int i = 0; i < 4; i++) {
      this.placeDiamondLayer(worldIn, randomIn, i + 1, blockPos.down(i), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
    }

    blockPos = blockPos.down(3);

    this.placeAir(worldIn, randomIn, blockPos.add(+4, 0, 0), blockPosSet, mutableBoundingBoxIn);
    this.placeAir(worldIn, randomIn, blockPos.add(-4, 0, 0), blockPosSet, mutableBoundingBoxIn);
    this.placeAir(worldIn, randomIn, blockPos.add(0, 0, +4), blockPosSet, mutableBoundingBoxIn);
    this.placeAir(worldIn, randomIn, blockPos.add(0, 0, -4), blockPosSet, mutableBoundingBoxIn);

    if (treeFeatureConfigIn.hasVines) {
      this.placeAir(worldIn, randomIn, blockPos.add(+1, 0, +1), blockPosSet, mutableBoundingBoxIn);
      this.placeAir(worldIn, randomIn, blockPos.add(+1, 0, -1), blockPosSet, mutableBoundingBoxIn);
      this.placeAir(worldIn, randomIn, blockPos.add(-1, 0, +1), blockPosSet, mutableBoundingBoxIn);
      this.placeAir(worldIn, randomIn, blockPos.add(-1, 0, -1), blockPosSet, mutableBoundingBoxIn);
    }

    //Drippers
    // stuck with only one block down because of leaf decay distance
    blockPos = blockPos.down();
    this.func_227219_b_(worldIn, randomIn, blockPos.add(+3, 0, 0), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
    this.func_227219_b_(worldIn, randomIn, blockPos.add(-3, 0, 0), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
    this.func_227219_b_(worldIn, randomIn, blockPos.add(0, 0, -3), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
    this.func_227219_b_(worldIn, randomIn, blockPos.add(0, 0, +3), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);

    if (!treeFeatureConfigIn.hasVines) {
      this.func_227219_b_(worldIn, randomIn, blockPos.add(+1, 0, +1), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
      this.func_227219_b_(worldIn, randomIn, blockPos.add(-3, 0, 0), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
      this.func_227219_b_(worldIn, randomIn, blockPos.add(-1, 0, +1), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
      this.func_227219_b_(worldIn, randomIn, blockPos.add(-1, 0, -1), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
    }

    if (treeFeatureConfigIn.hasVines) {
      blockPos = blockPos.down();
      this.placeVine(worldIn, randomIn, blockPos.add(+3, 0, 0), blockPosSet, mutableBoundingBoxIn,
        this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn).with(VineBlock.UP, true));

      this.placeVine(worldIn, randomIn, blockPos.add(-3, 0, 0), blockPosSet, mutableBoundingBoxIn,
        this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn).with(VineBlock.UP, true));

      this.placeVine(worldIn, randomIn, blockPos.add(0, 0, -3), blockPosSet, mutableBoundingBoxIn,
        this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn).with(VineBlock.UP, true));

      this.placeVine(worldIn, randomIn, blockPos.add(0, 0, +3), blockPosSet, mutableBoundingBoxIn,
        this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn).with(VineBlock.UP, true));

      BlockState randomVine = this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn);
      this.placeVine(worldIn, randomIn, blockPos.add(+2, 1, +2), blockPosSet, mutableBoundingBoxIn,
        randomVine.with(VineBlock.UP, true));
      this.placeVine(worldIn, randomIn, blockPos.add(+2, 0, +2), blockPosSet, mutableBoundingBoxIn,
        randomVine);

      randomVine = this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn);
      this.placeVine(worldIn, randomIn, blockPos.add(+2, 1, -2), blockPosSet, mutableBoundingBoxIn,
        randomVine.with(VineBlock.UP, true));
      this.placeVine(worldIn, randomIn, blockPos.add(+2, 0, -2), blockPosSet, mutableBoundingBoxIn,
        randomVine);

      randomVine = this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn);
      this.placeVine(worldIn, randomIn, blockPos.add(-2, 1, +2), blockPosSet, mutableBoundingBoxIn,
        randomVine.with(VineBlock.UP, true));
      this.placeVine(worldIn, randomIn, blockPos.add(-2, 0, +2), blockPosSet, mutableBoundingBoxIn,
        randomVine);

      randomVine = this.getRandomizedVine(randomIn, blockPos, treeFeatureConfigIn);
      this.placeVine(worldIn, randomIn, blockPos.add(-2, 1, -2), blockPosSet, mutableBoundingBoxIn,
        randomVine.with(VineBlock.UP, true));
      this.placeVine(worldIn, randomIn, blockPos.add(-2, 0, -2), blockPosSet, mutableBoundingBoxIn,
        randomVine);
    }
  }

  private void placeDiamondLayer(IWorldGenerationReader worldIn, Random randomIn, int range, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn, SlimeTreeFeatureConfig treeFeatureConfigIn) {
    for (int x = -range; x <= range; x++) {
      for (int z = -range; z <= range; z++) {
        if (Math.abs(x) + Math.abs(z) <= range) {
          BlockPos blockpos = blockPos.add(x, 0, z);
          this.func_227219_b_(worldIn, randomIn, blockpos, blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
        }
      }
    }
  }

  @Override
  protected boolean func_227216_a_(IWorldGenerationReader worldIn, Random randomIn, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn, BaseTreeFeatureConfig treeFeatureConfigIn) {
    if (!isAirOrLeaves(worldIn, blockPos)) {
      return false;
    } else {
      this.func_227217_a_(worldIn, blockPos, treeFeatureConfigIn.trunkProvider.getBlockState(randomIn, blockPos), mutableBoundingBoxIn);
      blockPosSet.add(blockPos.toImmutable());
      return true;
    }
  }

  protected boolean placeAir(IWorldGenerationReader worldIn, Random randomIn, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn) {
    if (!isAirOrLeaves(worldIn, blockPos)) {
      return false;
    } else {
      this.func_227217_a_(worldIn, blockPos, Blocks.AIR.getDefaultState(), mutableBoundingBoxIn);
      blockPosSet.add(blockPos.toImmutable());
      return true;
    }
  }

  @Override
  protected boolean func_227219_b_(IWorldGenerationReader worldIn, Random random, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn, BaseTreeFeatureConfig treeFeatureConfigIn) {
    if (!isAirOrLeaves(worldIn, blockPos)) {
      return false;
    } else {
      this.func_227217_a_(worldIn, blockPos, treeFeatureConfigIn.leavesProvider.getBlockState(random, blockPos), mutableBoundingBoxIn);
      blockPosSet.add(blockPos.toImmutable());
      return true;
    }
  }

  protected boolean placeVine(IWorldGenerationReader worldIn, Random random, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn, BlockState vineState) {
    if (!isAirOrLeaves(worldIn, blockPos)) {
      return false;
    } else {
      this.func_227217_a_(worldIn, blockPos, vineState, mutableBoundingBoxIn);
      blockPosSet.add(blockPos.toImmutable());
      return true;
    }
  }

  private BlockState getRandomizedVine(Random random, BlockPos blockPos, SlimeTreeFeatureConfig treeFeatureConfigIn) {
    BlockState state = treeFeatureConfigIn.vineProvider.getBlockState(random, blockPos);

    BooleanProperty[] sides = new BooleanProperty[]{VineBlock.NORTH, VineBlock.EAST, VineBlock.SOUTH, VineBlock.WEST};

    for (BooleanProperty side : sides) {
      state = state.with(side, false);
    }

    for (int i = random.nextInt(3) + 1; i > 0; i--) {
      state = state.with(sides[random.nextInt(sides.length)], true);
    }

    return state;
  }

  @Deprecated
  protected static boolean isSlimyDirtOrGrass(IWorldGenerationBaseReader worldIn, BlockPos pos) {
    return worldIn.hasBlockState(pos, (state) -> {
      Block block = state.getBlock();
      return WorldBlocks.slime_dirt.contains(block) || WorldBlocks.vanilla_slime_grass.contains(block) || WorldBlocks.green_slime_grass.contains(block) || WorldBlocks.blue_slime_grass.contains(block) || WorldBlocks.purple_slime_grass.contains(block) || WorldBlocks.magma_slime_grass.contains(block);
    });
  }

  @Override
  protected void setDirtAt(IWorldGenerationReader reader, BlockPos pos, BlockPos origin) {
    if (!(reader instanceof IWorld)) {
      return;
    }
    ((IWorld) reader).getBlockState(pos).onPlantGrow((IWorld) reader, pos, origin);
  }

  protected static boolean isSoilOrFarm(IWorldGenerationBaseReader reader, BlockPos pos, net.minecraftforge.common.IPlantable sapling) {
    if (!(reader instanceof net.minecraft.world.IBlockReader) || sapling == null)
      return isSlimyDirtOrGrass(reader, pos);
    return reader.hasBlockState(pos, state -> state.canSustainPlant((net.minecraft.world.IBlockReader) reader, pos, Direction.UP, sapling));
  }

  public static boolean isAirOrLeaves(IWorldGenerationBaseReader worldIn, BlockPos pos) {
    if (worldIn instanceof net.minecraft.world.IWorldReader) // FORGE: Redirect to state method when possible
      return worldIn.hasBlockState(pos, state -> state.canBeReplacedByLeaves((net.minecraft.world.IWorldReader) worldIn, pos));
    return worldIn.hasBlockState(pos, (p_227223_0_) -> p_227223_0_.isAir() || p_227223_0_.isIn(BlockTags.LEAVES) || p_227223_0_.isIn(Tags.Blocks.SLIMY_LEAVES));
  }

  private BlockPos findGround(IWorld reader, BlockPos position) {
    do {
      BlockState state = reader.getBlockState(position);
      Block block = state.getBlock();
      BlockState upState = reader.getBlockState(position.up());

      if ((WorldBlocks.slime_dirt.contains(block) || WorldBlocks.vanilla_slime_grass.contains(block) || WorldBlocks.green_slime_grass.contains(block) || WorldBlocks.blue_slime_grass.contains(block) || WorldBlocks.purple_slime_grass.contains(block) || WorldBlocks.magma_slime_grass.contains(block)) && !upState.getBlock().isOpaqueCube(upState, reader, position)) {
        return position.up();
      }
      position = position.down();
    }
    while (position.getY() > 0);

    return position;
  }
}
