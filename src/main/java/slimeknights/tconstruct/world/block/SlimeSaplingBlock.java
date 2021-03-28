package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.trees.Tree;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;

import javax.annotation.Nonnull;

public class SlimeSaplingBlock extends SaplingBlock {

  private final FoliageType foliageType;
  public SlimeSaplingBlock(Tree treeIn, FoliageType foliageType, Properties properties) {
    super(treeIn, properties);
    this.foliageType = foliageType;
  }

  @Override
  protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
    Block block = state.getBlock();
    return TinkerWorld.slimeDirt.contains(block) || TinkerWorld.vanillaSlimeGrass.contains(block) || TinkerWorld.earthSlimeGrass.contains(block) || TinkerWorld.skySlimeGrass.contains(block) || TinkerWorld.enderSlimeGrass.contains(block) || TinkerWorld.ichorSlimeGrass.contains(block);
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

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.foliageType != FoliageType.ICHOR) {
      super.fillItemGroup(group, items);
    }
  }
}
