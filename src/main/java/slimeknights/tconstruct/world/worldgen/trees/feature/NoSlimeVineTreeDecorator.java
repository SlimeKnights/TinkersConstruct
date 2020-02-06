package slimeknights.tconstruct.world.worldgen.trees.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.World;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class NoSlimeVineTreeDecorator extends TreeDecorator {

  public NoSlimeVineTreeDecorator() {
    super(TinkerWorld.SLIME_NO_VINE_TREE_DECORATOR);
  }

  public <T> NoSlimeVineTreeDecorator(Dynamic<T> p_i225870_1_) {
    this();
  }

  @Override
  public void func_225576_a_(IWorld worldIn, Random randomIn, List<BlockPos> blockPosList, List<BlockPos> secondBlockPosList, Set<BlockPos> blockPosSet, MutableBoundingBox mutableboundingbox) {
    secondBlockPosList.forEach((blockPos) -> {
      if(this.isBlueSlime(worldIn, blockPos)) {

      }

      if (randomIn.nextInt(4) == 0) {
        BlockPos blockpos = blockPos.west();
        if (AbstractTreeFeature.isAir(worldIn, blockpos)) {
          this.func_227420_a_(worldIn, blockpos, VineBlock.EAST, blockPosSet, mutableboundingbox);
        }
      }

      if (randomIn.nextInt(4) == 0) {
        BlockPos blockpos1 = blockPos.east();
        if (AbstractTreeFeature.isAir(worldIn, blockpos1)) {
          this.func_227420_a_(worldIn, blockpos1, VineBlock.WEST, blockPosSet, mutableboundingbox);
        }
      }

      if (randomIn.nextInt(4) == 0) {
        BlockPos blockpos2 = blockPos.north();
        if (AbstractTreeFeature.isAir(worldIn, blockpos2)) {
          this.func_227420_a_(worldIn, blockpos2, VineBlock.SOUTH, blockPosSet, mutableboundingbox);
        }
      }

      if (randomIn.nextInt(4) == 0) {
        BlockPos blockpos3 = blockPos.south();
        if (AbstractTreeFeature.isAir(worldIn, blockpos3)) {
          this.func_227420_a_(worldIn, blockpos3, VineBlock.NORTH, blockPosSet, mutableboundingbox);
        }
      }
    });
  }

  private boolean isBlueSlime(IWorld worldIn, BlockPos blockPos) {
    return worldIn.hasBlockState(blockPos, (p_227224_0_) -> {
      return p_227224_0_.getBlock() == WorldBlocks.blue_slime;
    });
  }

  private void func_227420_a_(IWorldGenerationReader worldIn, BlockPos blockPos, BooleanProperty direction, Set<BlockPos> blockPosSet, MutableBoundingBox mutableboundingbox) {
    this.func_227424_a_(worldIn, blockPos, direction, blockPosSet, mutableboundingbox);
    int i = 4;

    for (BlockPos blockpos = blockPos.down(); AbstractTreeFeature.isAir(worldIn, blockpos) && i > 0; --i) {
      this.func_227424_a_(worldIn, blockpos, direction, blockPosSet, mutableboundingbox);
      blockpos = blockpos.down();
    }
  }

  @Override
  protected void func_227424_a_(IWorldWriter p_227424_1_, BlockPos p_227424_2_, BooleanProperty p_227424_3_, Set<BlockPos> p_227424_4_, MutableBoundingBox p_227424_5_) {
    this.func_227423_a_(p_227424_1_, p_227424_2_, Blocks.VINE.getDefaultState().with(p_227424_3_, Boolean.valueOf(true)), p_227424_4_, p_227424_5_);
  }

  @Override
  protected void func_227423_a_(IWorldWriter p_227423_1_, BlockPos p_227423_2_, BlockState p_227423_3_, Set<BlockPos> p_227423_4_, MutableBoundingBox p_227423_5_) {
    p_227423_1_.setBlockState(p_227423_2_, p_227423_3_, 19);
    p_227423_4_.add(p_227423_2_);
    p_227423_5_.expandTo(new MutableBoundingBox(p_227423_2_, p_227423_2_));
  }

  @Override
  public <T> T serialize(DynamicOps<T> p_218175_1_) {
    return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.TREE_DECORATOR_TYPE.getKey(this.field_227422_a_).toString()))))).getValue();
  }
}
