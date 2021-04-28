package slimeknights.tconstruct.tools.melee;

import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class CleaverTool extends BroadSwordTool {
  public CleaverTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  protected double getSweepRange(ToolStack tool) {
    return tool.getModifierLevel(TinkerModifiers.expanded.get()) + 3;
  }
}
