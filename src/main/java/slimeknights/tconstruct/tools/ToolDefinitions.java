package slimeknights.tconstruct.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import slimeknights.tconstruct.items.ToolParts;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public final class ToolDefinitions {

  public static final ToolDefinition PICKAXE = new ToolDefinition(
    ToolBaseStatDefinitions.PICKAXE,
    ImmutableList.of(
      new PartMaterialRequirement(ToolParts.pickaxe_head, HeadMaterialStats.ID),
      new PartMaterialRequirement(ToolParts.tool_rod, HandleMaterialStats.ID),
      new PartMaterialRequirement(ToolParts.small_binding, ExtraMaterialStats.ID)
    ),
    ImmutableSet.of(Category.HARVEST));

  private ToolDefinitions() {
  }
}
