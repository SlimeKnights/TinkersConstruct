package slimeknights.tconstruct.tools;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;
import slimeknights.tconstruct.library.tools.ToolCore;

import java.util.Set;

public class ToolRegistry {

  private static final Set<ToolCore> tools = Sets.newLinkedHashSet();
  private static final Set<IToolPart> toolParts = Sets.newLinkedHashSet();

  /**
   * Register a tool, making it known to tinkers' systems.
   * All toolparts used to craft the tool will be registered as well.
   */
  public static void registerTool(ToolCore tool) {
    tools.add(tool);

    for (PartMaterialRequirement pmt : tool.getToolDefinition().getRequiredComponents()) {
      if (pmt.getPossiblePart() instanceof IToolPart) {
        toolParts.add((IToolPart) pmt.getPossiblePart());
      }
    }
  }

  public static Set<ToolCore> getTools() {
    return ImmutableSet.copyOf(tools);
  }

  public static Set<IToolPart> getToolParts() {
    return ImmutableSet.copyOf(toolParts);
  }
}
