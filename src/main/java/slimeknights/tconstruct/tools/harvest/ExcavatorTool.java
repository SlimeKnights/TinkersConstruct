package slimeknights.tconstruct.tools.harvest;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;

public class ExcavatorTool extends HarvestTool {
  public ExcavatorTool(Settings properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public AOEToolHarvestLogic getToolHarvestLogic() {
    return AOEToolHarvestLogic.LARGE_TOOL;
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    return getToolHarvestLogic().transformBlocks(context, FabricToolTags.SHOVELS, SoundEvents.ITEM_SHOVEL_FLATTEN, true);
  }

  /*
  @Override
  public float getRepairModifierForPart(int index) {
    return index == 1 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.75f;
  }*/
}
