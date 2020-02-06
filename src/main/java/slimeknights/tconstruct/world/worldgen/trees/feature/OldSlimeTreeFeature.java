package slimeknights.tconstruct.world.worldgen.trees.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractSmallTreeFeature;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.common.Tags;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class OldSlimeTreeFeature extends AbstractSmallTreeFeature<TreeFeatureConfig> {

  public OldSlimeTreeFeature(Function<Dynamic<?>, ? extends TreeFeatureConfig> config) {
    super(config);
  }

  @Override
  protected boolean func_225557_a_(IWorldGenerationReader worldIn, Random random, BlockPos blockPos, Set<BlockPos> trunkBlockPosSet, Set<BlockPos> foliageBlockPosSet, MutableBoundingBox boundingBox, TreeFeatureConfig treeFeatureConfig) {
    int baseHeight = treeFeatureConfig.baseHeight + random.nextInt(treeFeatureConfig.heightRandA + 1);
    int trunkHeight = treeFeatureConfig.trunkHeight >= 0 ? treeFeatureConfig.trunkHeight + random.nextInt(treeFeatureConfig.trunkHeightRandom + 1) : baseHeight - (treeFeatureConfig.foliageHeight + random.nextInt(treeFeatureConfig.foliageHeightRandom + 1));
    int foliageHeight = treeFeatureConfig.foliagePlacer.func_225573_a_(random, trunkHeight, baseHeight, treeFeatureConfig);
    Optional<BlockPos> optional = this.func_227212_a_(worldIn, baseHeight, trunkHeight, foliageHeight, blockPos, treeFeatureConfig);
    if (!optional.isPresent()) {
      return false;
    } else {
      BlockPos blockpos = optional.get();
      this.setDirtAt(worldIn, blockpos.down(), blockpos);
      treeFeatureConfig.foliagePlacer.func_225571_a_(worldIn, random, treeFeatureConfig, baseHeight, trunkHeight, foliageHeight, blockpos, foliageBlockPosSet);
      this.func_227213_a_(worldIn, random, baseHeight, blockpos, 0, trunkBlockPosSet, boundingBox, treeFeatureConfig);
      return true;
    }
  }

  @Override
  protected void func_227213_a_(IWorldGenerationReader worldIn, Random randomIn, int treeHeight, BlockPos blockPos, int trunkTopOffset, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn, TreeFeatureConfig treeFeatureConfigIn) {
    for (int i = 0; i < treeHeight - trunkTopOffset; ++i) {
      this.func_227216_a_(worldIn, randomIn, blockPos.up(i), blockPosSet, mutableBoundingBoxIn, treeFeatureConfigIn);
    }
  }

  @Override
  protected boolean func_227216_a_(IWorldGenerationReader worldIn, Random randomIn, BlockPos blockPos, Set<BlockPos> blockPosSet, MutableBoundingBox mutableBoundingBoxIn, BaseTreeFeatureConfig treeFeatureConfigIn) {
    if (!isAirOrLeaves(worldIn, blockPos) && !isTallPlants(worldIn, blockPos) && !isWater(worldIn, blockPos)) {
      return false;
    } else {
      this.func_227217_a_(worldIn, blockPos, treeFeatureConfigIn.trunkProvider.func_225574_a_(randomIn, blockPos), mutableBoundingBoxIn);
      blockPosSet.add(blockPos.toImmutable());
      return true;
    }
  }

  @Override
  public Optional<BlockPos> func_227212_a_(IWorldGenerationReader worldIn, int baseHeightIn, int trunkHeightIn, int foliageHeightIn, BlockPos blockPos, TreeFeatureConfig treeFeatureConfigIn) {
    BlockPos blockpos;
    if (!treeFeatureConfigIn.forcePlacement) {
      int i = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR, blockPos).getY();
      int j = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE, blockPos).getY();
      blockpos = new BlockPos(blockPos.getX(), i, blockPos.getZ());
      if (j - i > treeFeatureConfigIn.maxWaterDepth) {
        return Optional.empty();
      }
    } else {
      blockpos = blockPos;
    }

    if (blockpos.getY() >= 1 && blockpos.getY() + baseHeightIn + 1 <= worldIn.getMaxHeight()) {
      for (int i1 = 0; i1 <= baseHeightIn + 1; ++i1) {
        int j1 = treeFeatureConfigIn.foliagePlacer.func_225570_a_(trunkHeightIn, baseHeightIn, foliageHeightIn, i1);
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int k = -j1; k <= j1; ++k) {
          int l = -j1;

          while (l <= j1) {
            if (i1 + blockpos.getY() >= 0 && i1 + blockpos.getY() < worldIn.getMaxHeight()) {
              mutable.setPos(k + blockpos.getX(), i1 + blockpos.getY(), l + blockpos.getZ());
              if (isAirLeavesLogsSaplingsDirtOrVine(worldIn, mutable) && (treeFeatureConfigIn.ignoreVines || !isVine(worldIn, mutable))) {
                ++l;
                continue;
              }

              return Optional.empty();
            }

            return Optional.empty();
          }
        }
      }

      return isSoilOrFarm(worldIn, blockpos.down(), treeFeatureConfigIn.getSapling()) && blockpos.getY() < worldIn.getMaxHeight() - baseHeightIn - 1 ? Optional.of(blockpos) : Optional.empty();
    } else {
      return Optional.empty();
    }
  }

  @Deprecated
  protected static boolean isSlimyDirtOrGrass(IWorldGenerationBaseReader worldIn, BlockPos pos) {
    return worldIn.hasBlockState(pos, (p_227220_0_) -> {
      Block block = p_227220_0_.getBlock();
      return block == WorldBlocks.green_slime_dirt || block == WorldBlocks.blue_slime_dirt || block == WorldBlocks.purple_slime_dirt || block == WorldBlocks.magma_slime_dirt || block == WorldBlocks.blue_vanilla_slime_grass || block == WorldBlocks.purple_vanilla_slime_grass || block == WorldBlocks.orange_vanilla_slime_grass || block == WorldBlocks.blue_green_slime_grass || block == WorldBlocks.purple_green_slime_grass || block == WorldBlocks.orange_green_slime_grass || block == WorldBlocks.blue_blue_slime_grass || block == WorldBlocks.purple_blue_slime_grass || block == WorldBlocks.orange_blue_slime_grass || block == WorldBlocks.blue_purple_slime_grass || block == WorldBlocks.purple_purple_slime_grass || block == WorldBlocks.orange_purple_slime_grass
        || block == WorldBlocks.blue_magma_slime_grass || block == WorldBlocks.purple_magma_slime_grass || block == WorldBlocks.orange_magma_slime_grass;
    });
  }

  protected static boolean isSoilOrFarm(IWorldGenerationBaseReader reader, BlockPos pos, net.minecraftforge.common.IPlantable sapling) {
    if (!(reader instanceof net.minecraft.world.IBlockReader) || sapling == null)
      return isSlimyDirtOrGrass(reader, pos);
    return reader.hasBlockState(pos, state -> state.canSustainPlant((net.minecraft.world.IBlockReader) reader, pos, Direction.UP, sapling));
  }

  protected static boolean isAirLeavesLogsSaplingsDirtOrVine(IWorldGenerationBaseReader reader, BlockPos blockPos) {
    if (reader instanceof net.minecraft.world.IWorldReader) // FORGE: Redirect to state method when possible
      return reader.hasBlockState(blockPos, state -> state.canBeReplacedByLogs((net.minecraft.world.IWorldReader) reader, blockPos));
    return reader.hasBlockState(blockPos, (p_214573_0_) -> {
      Block block = p_214573_0_.getBlock();
      return p_214573_0_.isAir() || p_214573_0_.isIn(BlockTags.LEAVES) || isDirt(block) || block.isIn(BlockTags.LOGS) || block.isIn(BlockTags.SAPLINGS) || block.isIn(Tags.Blocks.SLIMY_LOGS) || block.isIn(Tags.Blocks.SLIMY_LEAVES) || block.isIn(Tags.Blocks.SLIMY_SAPLINGS) || block == Blocks.VINE || block == WorldBlocks.purple_slime_vine || block == WorldBlocks.purple_slime_vine_middle || block == WorldBlocks.purple_slime_vine_end || block == WorldBlocks.blue_slime_vine || block == WorldBlocks.blue_slime_vine_middle || block == WorldBlocks.blue_slime_vine_end;
    });
  }

  protected static boolean isVine(IWorldGenerationBaseReader reader, BlockPos blockPos) {
    return reader.hasBlockState(blockPos, (p_227224_0_) -> {
      Block block = p_227224_0_.getBlock();
      return block == Blocks.VINE || block == WorldBlocks.purple_slime_vine || block == WorldBlocks.purple_slime_vine_middle || block == WorldBlocks.purple_slime_vine_end || block == WorldBlocks.blue_slime_vine || block == WorldBlocks.blue_slime_vine_middle || block == WorldBlocks.blue_slime_vine_end;
    });
  }

  public static boolean isAirOrLeaves(IWorldGenerationBaseReader worldIn, BlockPos pos) {
    if (worldIn instanceof net.minecraft.world.IWorldReader) // FORGE: Redirect to state method when possible
      return worldIn.hasBlockState(pos, state -> state.canBeReplacedByLeaves((net.minecraft.world.IWorldReader) worldIn, pos));
    return worldIn.hasBlockState(pos, (p_227223_0_) -> {
      return p_227223_0_.isAir() || p_227223_0_.isIn(BlockTags.LEAVES) || p_227223_0_.isIn(Tags.Blocks.SLIMY_LEAVES);
    });
  }

  @Override
  protected void setDirtAt(IWorldGenerationReader reader, BlockPos pos, BlockPos origin) {
    if (!(reader instanceof IWorld)) {
      return;
    }
    ((IWorld) reader).getBlockState(pos).onPlantGrow((IWorld) reader, pos, origin);
  }

  private BlockPos findGround(IWorld reader, BlockPos position) {
    do {
      BlockState state = reader.getBlockState(position);
      Block block = state.getBlock();
      BlockState upState = reader.getBlockState(position.up());
      if ((block == WorldBlocks.green_slime_dirt || block == WorldBlocks.blue_slime_dirt || block == WorldBlocks.purple_slime_dirt || block == WorldBlocks.magma_slime_dirt || block == WorldBlocks.blue_vanilla_slime_grass || block == WorldBlocks.purple_vanilla_slime_grass || block == WorldBlocks.orange_vanilla_slime_grass || block == WorldBlocks.blue_green_slime_grass || block == WorldBlocks.purple_green_slime_grass || block == WorldBlocks.orange_green_slime_grass || block == WorldBlocks.blue_blue_slime_grass || block == WorldBlocks.purple_blue_slime_grass || block == WorldBlocks.orange_blue_slime_grass || block == WorldBlocks.blue_purple_slime_grass || block == WorldBlocks.purple_purple_slime_grass || block == WorldBlocks.orange_purple_slime_grass || block == WorldBlocks.blue_magma_slime_grass || block == WorldBlocks.purple_magma_slime_grass || block == WorldBlocks.orange_magma_slime_grass) && !upState.getBlock().isOpaqueCube(upState, reader, position)) {
        return position.up();
      }
      position = position.down();
    }
    while (position.getY() > 0);

    return position;
  }
}
