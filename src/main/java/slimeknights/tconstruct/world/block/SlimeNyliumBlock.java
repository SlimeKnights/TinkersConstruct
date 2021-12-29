package slimeknights.tconstruct.world.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.server.level.ServerLevel;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.block.SlimeType;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * Slimy variant of nylium, mostly changes the way it bonemeals
 */
public class SlimeNyliumBlock extends Block implements BonemealableBlock {
  private final SlimeType foliageType;
  public SlimeNyliumBlock(Properties properties, SlimeType foliageType) {
    super(properties);
    this.foliageType = foliageType;
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (this.foliageType != SlimeType.ICHOR) {
      super.fillItemCategory(group, items);
    }
  }

  private static boolean isDarkEnough(BlockState state, LevelReader reader, BlockPos pos) {
    BlockPos blockpos = pos.above();
    BlockState blockstate = reader.getBlockState(blockpos);
    int i = LayerLightEngine.getLightBlockInto(reader, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(reader, blockpos));
    return i < reader.getMaxLightLevel();
  }

  @Override
  public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
    if (!isDarkEnough(state, worldIn, pos)) {
      worldIn.setBlockAndUpdate(pos, SlimeGrassBlock.getDirtState(state));
    }
  }

  @Override
  public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient) {
    return worldIn.getBlockState(pos.above()).isAir();
  }

  @Override
  public boolean isBonemealSuccess(Level worldIn, Random rand, BlockPos pos, BlockState state) {
    return true;
  }

  @Override
  public void performBonemeal(ServerLevel world, Random rand, BlockPos pos, BlockState state) {
    SlimeGrassBlock.growGrass(world, rand, pos, TinkerTags.Blocks.SLIMY_NYLIUM, foliageType, true, true);
  }
}
