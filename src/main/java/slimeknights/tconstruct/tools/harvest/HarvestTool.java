package slimeknights.tconstruct.tools.harvest;

import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;
import slimeknights.tconstruct.library.tools.item.ToolCore;

/**
 * Simple class that swaps the harvest logic for the AOE logic
 */
public class HarvestTool extends ToolCore {
  public HarvestTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public AOEToolHarvestLogic getToolHarvestLogic() {
    return AOEToolHarvestLogic.SMALL_TOOL;
  }
}
