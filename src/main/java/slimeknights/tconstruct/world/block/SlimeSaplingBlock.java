package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.trees.Tree;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nonnull;

public class SlimeSaplingBlock extends SaplingBlock {

  public SlimeSaplingBlock(Tree treeIn, Properties properties) {
    super(treeIn, properties);
  }

  @Override
  protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
    Block block = state.getBlock();
    return TinkerWorld.slimeDirt.contains(block) || TinkerWorld.vanillaSlimeGrass.contains(block) || TinkerWorld.greenSlimeGrass.contains(block) || TinkerWorld.blueSlimeGrass.contains(block) || TinkerWorld.purpleSlimeGrass.contains(block) || TinkerWorld.magmaSlimeGrass.contains(block);
  }

  @Nonnull
  @Override
  public PlantType getPlantType(IBlockReader world, BlockPos pos) {
    return TinkerWorld.SLIME_PLANT_TYPE;
  }

  @Override
  @Deprecated
  public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
    return false;
  }
}
