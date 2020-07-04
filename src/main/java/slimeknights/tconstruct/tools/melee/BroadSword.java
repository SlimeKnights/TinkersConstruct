package slimeknights.tconstruct.tools.melee;

import slimeknights.tconstruct.library.tools.SwordCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.tools.ToolRegistry;

public class BroadSword extends SwordCore {

  public BroadSword(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);

    ToolRegistry.registerToolCrafting(this);
  }
}

