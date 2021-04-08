package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraftforge.common.PlantType;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;

import javax.annotation.Nonnull;

public class SlimeSaplingBlock extends SaplingBlock {

  private final FoliageType foliageType;
  public SlimeSaplingBlock(SaplingGenerator treeIn, FoliageType foliageType, Settings properties) {
    super(treeIn, properties);
    this.foliageType = foliageType;
  }

  @Override
  protected boolean canPlantOnTop(BlockState state, BlockView worldIn, BlockPos pos) {
    Block block = state.getBlock();
    return TinkerWorld.slimeDirt.contains(block) || TinkerWorld.vanillaSlimeGrass.contains(block) || TinkerWorld.earthSlimeGrass.contains(block) || TinkerWorld.skySlimeGrass.contains(block) || TinkerWorld.enderSlimeGrass.contains(block) || TinkerWorld.ichorSlimeGrass.contains(block);
  }

  @Nonnull
  @Override
  public PlantType getPlantType(BlockView world, BlockPos pos) {
    return TinkerWorld.SLIME_PLANT_TYPE;
  }

  @Override
  @Deprecated
  public boolean canReplace(BlockState state, ItemPlacementContext useContext) {
    return false;
  }

  @Override
  public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> items) {
    if (this.foliageType != FoliageType.ICHOR) {
      super.addStacksForDisplay(group, items);
    }
  }
}
