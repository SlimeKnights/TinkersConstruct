package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.trees.Tree;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nonnull;

public class SlimeSaplingBlock extends SaplingBlock {

  public SlimeSaplingBlock(Tree treeIn, Properties properties) {
    super(treeIn, properties);
  }

  @Override
  protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
    Block block = state.getBlock();
    return WorldBlocks.slime_dirt.contains(block) || WorldBlocks.vanilla_slime_grass.contains(block) || WorldBlocks.green_slime_grass.contains(block) || WorldBlocks.blue_slime_grass.contains(block) || WorldBlocks.purple_slime_grass.contains(block) || WorldBlocks.magma_slime_grass.contains(block);
  }

  @Nonnull
  @Override
  public PlantType getPlantType(IBlockReader world, BlockPos pos) {
    return TinkerWorld.slimePlantType;
  }

  @Override
  @Deprecated
  public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
    return false;
  }
}
