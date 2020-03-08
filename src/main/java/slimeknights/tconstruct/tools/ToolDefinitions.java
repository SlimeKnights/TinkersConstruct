package slimeknights.tconstruct.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import slimeknights.tconstruct.items.ToolItems;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public final class ToolDefinitions {

  public static final ToolDefinition PICKAXE = new ToolDefinition(
    ToolBaseStatDefinitions.PICKAXE,
    ImmutableList.of(
      new PartMaterialType(ToolItems.test_part, HeadMaterialStats.ID),
      new PartMaterialType(ToolItems.test_part, HandleMaterialStats.ID),
      new PartMaterialType(ToolItems.test_part, ExtraMaterialStats.ID)
    ),
    ImmutableSet.of(Category.HARVEST));

  private ToolDefinitions() {
  }
}
