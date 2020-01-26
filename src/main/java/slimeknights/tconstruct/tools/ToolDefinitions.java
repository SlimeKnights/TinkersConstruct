package slimeknights.tconstruct.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import slimeknights.tconstruct.items.ToolItems;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolDefinition;

public final class ToolDefinitions {

  public static final ToolDefinition PICKAXE = new ToolDefinition(
    ToolBaseStatDefinitions.PICKAXE,
    ImmutableList.of(new PartMaterialType(ToolItems.test_part)),
    ImmutableSet.of(Category.HARVEST));

  private ToolDefinitions() {
  }
}
