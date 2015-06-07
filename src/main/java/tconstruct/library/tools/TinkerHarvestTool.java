package tconstruct.library.tools;

import net.minecraft.item.ItemStack;

import tconstruct.library.tinkering.Category;
import tconstruct.library.tinkering.PartMaterialWrapper;
import tconstruct.library.utils.TooltipBuilder;

public abstract class TinkerHarvestTool extends TinkersTool {

  public TinkerHarvestTool(PartMaterialWrapper... requiredComponents) {
    super(requiredComponents);

    addCategory(Category.HARVEST);
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
