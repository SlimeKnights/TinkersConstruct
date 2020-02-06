package slimeknights.tconstruct.world.worldgen.trees.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import slimeknights.tconstruct.blocks.CommonBlocks;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Random;
import java.util.Set;

public class SlimeFoliagePlacer extends FoliagePlacer {

  public SlimeFoliagePlacer(int radiusIn, int randomRadiusIn) {
    super(radiusIn, randomRadiusIn, TinkerWorld.SLIME_FOLIAGE_PLACER);
  }

  public <T> SlimeFoliagePlacer(Dynamic<T> placer) {
    this(placer.get("radius").asInt(0), placer.get("radius_random").asInt(0));
  }

  @Override
  public void func_225571_a_(IWorldGenerationReader worldIn, Random randomIn, TreeFeatureConfig treeFeatureConfigIn, int baseHeightIn, int trunkHeightIn, int foliageHeightIn, BlockPos blockPosIn, Set<BlockPos> blockPosSetIn) {
    for (int i = 0; i < 4; i++) {
      this.func_227384_a_(worldIn, randomIn, treeFeatureConfigIn, baseHeightIn, blockPosIn, baseHeightIn - i, i + 1, blockPosSetIn);
    }

    BlockPos pos = blockPosIn.up(baseHeightIn - 3);

    this.setAir(worldIn, randomIn, pos.add(+4, 0, 0), treeFeatureConfigIn, blockPosSetIn);
    this.setAir(worldIn, randomIn, pos.add(-4, 0, 0), treeFeatureConfigIn, blockPosSetIn);
    this.setAir(worldIn, randomIn, pos.add(0, 0, +4), treeFeatureConfigIn, blockPosSetIn);
    this.setAir(worldIn, randomIn, pos.add(0, 0, -4), treeFeatureConfigIn, blockPosSetIn);

    this.setVineAirPlaceholder(worldIn, randomIn, pos.add(+1, 0, +1), treeFeatureConfigIn, blockPosSetIn);
    this.setVineAirPlaceholder(worldIn, randomIn, pos.add(+1, 0, -1), treeFeatureConfigIn, blockPosSetIn);
    this.setVineAirPlaceholder(worldIn, randomIn, pos.add(-1, 0, +1), treeFeatureConfigIn, blockPosSetIn);
    this.setVineAirPlaceholder(worldIn, randomIn, pos.add(-1, 0, -1), treeFeatureConfigIn, blockPosSetIn);

    //Drippers
    // stuck with only one block down because of leaf decay distance
    pos = pos.down();
    this.func_227385_a_(worldIn, randomIn, pos.add(+3, 0, 0), treeFeatureConfigIn, blockPosSetIn);
    this.func_227385_a_(worldIn, randomIn, pos.add(-3, 0, 0), treeFeatureConfigIn, blockPosSetIn);
    this.func_227385_a_(worldIn, randomIn, pos.add(0, 0, +3), treeFeatureConfigIn, blockPosSetIn);
    this.func_227385_a_(worldIn, randomIn, pos.add(0, 0, -3), treeFeatureConfigIn, blockPosSetIn);
    this.setNoVinePlaceholder(worldIn, randomIn, pos.add(+1, 0, +1), treeFeatureConfigIn, blockPosSetIn);
    this.setNoVinePlaceholder(worldIn, randomIn, pos.add(+1, 0, -1), treeFeatureConfigIn, blockPosSetIn);
    this.setNoVinePlaceholder(worldIn, randomIn, pos.add(-1, 0, +1), treeFeatureConfigIn, blockPosSetIn);
    this.setNoVinePlaceholder(worldIn, randomIn, pos.add(-1, 0, -1), treeFeatureConfigIn, blockPosSetIn);

    pos = pos.down();

    this.setVinePlaceholder(worldIn, randomIn, pos.add(+3, 0, 0), treeFeatureConfigIn, blockPosSetIn);
    this.setVinePlaceholder(worldIn, randomIn, pos.add(-3, 0, 0), treeFeatureConfigIn, blockPosSetIn);
    this.setVinePlaceholder(worldIn, randomIn, pos.add(0, 0, -3), treeFeatureConfigIn, blockPosSetIn);
    this.setVinePlaceholder(worldIn, randomIn, pos.add(0, 0, +3), treeFeatureConfigIn, blockPosSetIn);

    this.setUpVinePlaceholder(worldIn, randomIn, pos.add(+2, 1, +2), treeFeatureConfigIn, blockPosSetIn);
    this.setVinePlaceholder(worldIn, randomIn, pos.add(+2, 0, +2), treeFeatureConfigIn, blockPosSetIn);

    this.setUpVinePlaceholder(worldIn, randomIn, pos.add(+2, 1, -2), treeFeatureConfigIn, blockPosSetIn);
    this.setVinePlaceholder(worldIn, randomIn, pos.add(+2, 0, -2), treeFeatureConfigIn, blockPosSetIn);

    this.setUpVinePlaceholder(worldIn, randomIn, pos.add(+2, 1, -2), treeFeatureConfigIn, blockPosSetIn);
    this.setVinePlaceholder(worldIn, randomIn, pos.add(+2, 0, -2), treeFeatureConfigIn, blockPosSetIn);

    this.setUpVinePlaceholder(worldIn, randomIn, pos.add(-2, 1, +2), treeFeatureConfigIn, blockPosSetIn);
    this.setVinePlaceholder(worldIn, randomIn, pos.add(-2, 0, +2), treeFeatureConfigIn, blockPosSetIn);

    this.setUpVinePlaceholder(worldIn, randomIn, pos.add(-2, 1, -2), treeFeatureConfigIn, blockPosSetIn);
    this.setVinePlaceholder(worldIn, randomIn, pos.add(-2, 0, -2), treeFeatureConfigIn, blockPosSetIn);

    //for (int y = baseHeightIn; y >= trunkHeightIn; --y) {
    //  int range = Math.max(foliageHeightIn - 1 - (y - baseHeightIn) / 2, 0);
    //  this.func_227384_a_(worldIn, randomIn, treeFeatureConfigIn, baseHeightIn, blockPosIn, y, range, blockPosSetIn);
    //}
  }

  @Override
  protected void func_227384_a_(IWorldGenerationReader worldIn, Random randomIn, TreeFeatureConfig treeFeatureConfigIn, int baseHeightIn, BlockPos blockPosIn, int yIn, int range, Set<BlockPos> blockPosSet) {
    BlockPos.Mutable mutable = new BlockPos.Mutable();

    for (int x = -range; x <= range; x++) {
      for (int z = -range; z <= range; z++) {
        if (this.func_225572_a_(randomIn, baseHeightIn, x, yIn, z, range)) {
          mutable.setPos(x + blockPosIn.getX(), yIn + blockPosIn.getY(), z + blockPosIn.getZ());
          this.func_227385_a_(worldIn, randomIn, mutable, treeFeatureConfigIn, blockPosSet);
        }
      }
    }
  }

  @Override
  protected void func_227385_a_(IWorldGenerationReader worldIn, Random randomIn, BlockPos blockPosIn, TreeFeatureConfig treeFeatureConfigIn, Set<BlockPos> blockPosSetIn) {
    if (SlimeTreeFeature.isAirOrLeaves(worldIn, blockPosIn) || AbstractTreeFeature.isTallPlants(worldIn, blockPosIn) || AbstractTreeFeature.isWater(worldIn, blockPosIn)) {
      worldIn.setBlockState(blockPosIn, treeFeatureConfigIn.leavesProvider.func_225574_a_(randomIn, blockPosIn), 19);
      blockPosSetIn.add(blockPosIn.toImmutable());
    }
  }

  protected void setAir(IWorldGenerationReader worldIn, Random randomIn, BlockPos blockPosIn, TreeFeatureConfig treeFeatureConfigIn, Set<BlockPos> blockPosSetIn) {
    if (SlimeTreeFeature.isAirOrLeaves(worldIn, blockPosIn) || AbstractTreeFeature.isTallPlants(worldIn, blockPosIn) || AbstractTreeFeature.isWater(worldIn, blockPosIn)) {
      worldIn.setBlockState(blockPosIn, Blocks.AIR.getDefaultState(), 19);
      blockPosSetIn.add(blockPosIn.toImmutable());
    }
  }

  protected void setNoVinePlaceholder(IWorldGenerationReader worldIn, Random randomIn, BlockPos blockPosIn, TreeFeatureConfig treeFeatureConfigIn, Set<BlockPos> blockPosSetIn) {
    if (SlimeTreeFeature.isAirOrLeaves(worldIn, blockPosIn) || AbstractTreeFeature.isTallPlants(worldIn, blockPosIn) || AbstractTreeFeature.isWater(worldIn, blockPosIn)) {
      worldIn.setBlockState(blockPosIn, WorldBlocks.blue_slime.getDefaultState(), 19);
      blockPosSetIn.add(blockPosIn.toImmutable());
    }
  }

  protected void setVineAirPlaceholder(IWorldGenerationReader worldIn, Random randomIn, BlockPos blockPosIn, TreeFeatureConfig treeFeatureConfigIn, Set<BlockPos> blockPosSetIn) {
    if (SlimeTreeFeature.isAirOrLeaves(worldIn, blockPosIn) || AbstractTreeFeature.isTallPlants(worldIn, blockPosIn) || AbstractTreeFeature.isWater(worldIn, blockPosIn)) {
      worldIn.setBlockState(blockPosIn, WorldBlocks.purple_slime.getDefaultState(), 19);
      blockPosSetIn.add(blockPosIn.toImmutable());
    }
  }

  protected void setVinePlaceholder(IWorldGenerationReader worldIn, Random randomIn, BlockPos blockPosIn, TreeFeatureConfig treeFeatureConfigIn, Set<BlockPos> blockPosSetIn) {
    if (SlimeTreeFeature.isAirOrLeaves(worldIn, blockPosIn) || AbstractTreeFeature.isTallPlants(worldIn, blockPosIn) || AbstractTreeFeature.isWater(worldIn, blockPosIn)) {
      worldIn.setBlockState(blockPosIn, WorldBlocks.blood_slime.getDefaultState(), 19);
      blockPosSetIn.add(blockPosIn.toImmutable());
    }
  }

  protected void setUpVinePlaceholder(IWorldGenerationReader worldIn, Random randomIn, BlockPos blockPosIn, TreeFeatureConfig treeFeatureConfigIn, Set<BlockPos> blockPosSetIn) {
    if (SlimeTreeFeature.isAirOrLeaves(worldIn, blockPosIn) || AbstractTreeFeature.isTallPlants(worldIn, blockPosIn) || AbstractTreeFeature.isWater(worldIn, blockPosIn)) {
      worldIn.setBlockState(blockPosIn, WorldBlocks.magma_slime.getDefaultState(), 19);
      blockPosSetIn.add(blockPosIn.toImmutable());
    }
  }

  @Override
  public int func_225573_a_(Random randomIn, int trunkHeightIn, int baseHeightIn, TreeFeatureConfig treeFeatureConfigIn) {
    return this.field_227381_a_ + randomIn.nextInt(this.field_227382_b_ + 1);
  }

  @Override
  protected boolean func_225572_a_(Random randomIn, int baseHeightIn, int xIn, int yIn, int zIn, int rangeIn) {
    return Math.abs(xIn) + Math.abs(zIn) <= rangeIn;
  }

  @Override
  public int func_225570_a_(int trunkHeightIn, int baseHeightIn, int foliageHeightIn, int heightIn) {
    return heightIn == 0 ? 0 : 1;
  }
}
