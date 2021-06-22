package slimeknights.tconstruct.tools.item.broad;

import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.item.small.SweepingSwordTool;

public class CleaverTool extends SweepingSwordTool {
  public CleaverTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  protected double getSweepRange(IModifierToolStack tool) {
    return tool.getModifierLevel(TinkerModifiers.expanded.get()) + 3;
  }
}
