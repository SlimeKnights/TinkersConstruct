package slimeknights.tconstruct.tools.harvest;

import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;

public class ExcavatorTool extends ShovelTool {
  public ExcavatorTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public AOEToolHarvestLogic getToolHarvestLogic() {
    return AOEToolHarvestLogic.LARGE_TOOL;
  }

  /*@Override
  public int[] getRepairParts() {
    return new int[] { 1, 2 };
  }

  @Override
  public float getRepairModifierForPart(int index) {
    return index == 1 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.75f;
  }*/
}
