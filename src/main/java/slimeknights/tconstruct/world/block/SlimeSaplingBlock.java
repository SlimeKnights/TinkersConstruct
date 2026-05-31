package slimeknights.tconstruct.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;

import slimeknights.tconstruct.world.TinkerWorld;

public class SlimeSaplingBlock extends SaplingBlock {
  private final FoliageType foliageType;
  public SlimeSaplingBlock(TreeGrower treeIn, FoliageType foliageType, Properties properties) {
    super(treeIn, properties);
    this.foliageType = foliageType;
  }

  @Override
  protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
    Block block = state.getBlock();
    return TinkerWorld.slimeDirt.contains(block) || TinkerWorld.vanillaSlimeGrass.contains(block) || TinkerWorld.earthSlimeGrass.contains(block) || TinkerWorld.skySlimeGrass.contains(block) || TinkerWorld.enderSlimeGrass.contains(block) || TinkerWorld.ichorSlimeGrass.contains(block);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
    return false;
  }
}
