package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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

import java.util.Random;

import net.minecraft.block.AbstractBlock.Properties;

/**
 * Slimy variant of nylium, mostly changes the way it bonemeals
 */
public class SlimeNyliumBlock extends Block implements IGrowable {
  private final SlimeType foliageType;
  public SlimeNyliumBlock(Properties properties, SlimeType foliageType) {
    super(properties);
    this.foliageType = foliageType;
  }

  @Override
  public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.foliageType != SlimeType.ICHOR) {
      super.fillItemCategory(group, items);
    }
  }

  private static boolean isDarkEnough(BlockState state, IWorldReader reader, BlockPos pos) {
    BlockPos blockpos = pos.above();
    BlockState blockstate = reader.getBlockState(blockpos);
    int i = LightEngine.getLightBlockInto(reader, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(reader, blockpos));
    return i < reader.getMaxLightLevel();
  }

  @Override
  public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
    if (!isDarkEnough(state, worldIn, pos)) {
      worldIn.setBlockAndUpdate(pos, SlimeGrassBlock.getDirtState(state));
    }
  }

  @Override
  public boolean isValidBonemealTarget(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
    return worldIn.getBlockState(pos.above()).isAir();
  }

  @Override
  public boolean isBonemealSuccess(World worldIn, Random rand, BlockPos pos, BlockState state) {
    return true;
  }

  @Override
  public void performBonemeal(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
    SlimeGrassBlock.growGrass(world, rand, pos, TinkerTags.Blocks.SLIMY_NYLIUM, foliageType, true, true);
  }
}
