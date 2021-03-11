package slimeknights.tconstruct.tools.harvest;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;

public class ExcavatorTool extends HarvestTool {
  public ExcavatorTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public AOEToolHarvestLogic getToolHarvestLogic() {
    return AOEToolHarvestLogic.LARGE_TOOL;
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    return getToolHarvestLogic().transformBlocks(context, ToolType.SHOVEL, SoundEvents.ITEM_SHOVEL_FLATTEN, true);
  }

  /*
  @Override
  public float getRepairModifierForPart(int index) {
    return index == 1 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.75f;
  }*/
}
