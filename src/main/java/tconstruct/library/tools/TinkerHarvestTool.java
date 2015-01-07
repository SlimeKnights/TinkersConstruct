package tconstruct.library.tools;

import net.minecraft.item.ItemStack;

import tconstruct.library.utils.TooltipBuilder;

public abstract class TinkerHarvestTool extends TinkersTool {
  @Override
  public String getItemType() {
    return "harvest";
  }

  @Override
  public String[] getInformation(ItemStack stack) {
    TooltipBuilder info = new TooltipBuilder(stack);

    info.addDurability();
    info.addHarvestLevel();
    info.addMiningSpeed();

    return info.getTooltip();
  }
}
