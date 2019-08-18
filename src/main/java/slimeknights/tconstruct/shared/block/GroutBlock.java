package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerPulseIds;

import javax.annotation.Nullable;

public class GroutBlock extends Block {

  public GroutBlock() {
    super(Block.Properties.create(Material.SAND).hardnessAndResistance(3.0f).slipperiness(0.8F).sound(SoundType.SAND));
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_SMELTERY_PULSE_ID)) {
      super.fillItemGroup(group, items);
    }
  }

  @Nullable
  @Override
  //TODO: Replace when forge Re-Evaluates
  public net.minecraftforge.common.ToolType getHarvestTool(BlockState state) {
    return ToolType.SHOVEL;
  }

  @Override
  //TODO: Replace when forge Re-Evaluates
  public int getHarvestLevel(BlockState state) {
    return -1;
  }

}
