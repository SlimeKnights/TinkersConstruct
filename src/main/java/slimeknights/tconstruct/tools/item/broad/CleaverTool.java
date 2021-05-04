package slimeknights.tconstruct.tools.item.broad;

import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.item.small.BroadSwordTool;

public class CleaverTool extends BroadSwordTool {
  public CleaverTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  protected double getSweepRange(ToolStack tool) {
    return tool.getModifierLevel(TinkerModifiers.expanded.get()) + 3;
  }
}
