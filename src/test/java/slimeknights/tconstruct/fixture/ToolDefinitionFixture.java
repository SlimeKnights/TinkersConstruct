package slimeknights.tconstruct.fixture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public final class ToolDefinitionFixture {

  public static final Category TEST_CATEGORY = new Category("test");

  public static ToolDefinition getTestToolDefinition() {
    return new ToolDefinition(
      new ToolBaseStatDefinition.Builder().setDamageModifier(1f).build(),
      ImmutableList.of(PartMaterialTypeFixture.PART_MATERIAL_TYPE, PartMaterialTypeFixture.PART_MATERIAL_TYPE_2),
      ImmutableSet.of(TEST_CATEGORY)
    );
  }

  public static ToolDefinition getStandardToolDefinition() {
    return new ToolDefinition(
      new ToolBaseStatDefinition.Builder().setDamageModifier(1f).build(),
      ImmutableList.of(
        new PartMaterialType(MaterialItemFixture.MATERIAL_ITEM_HEAD, HeadMaterialStats.ID),
        new PartMaterialType(MaterialItemFixture.MATERIAL_ITEM_HANDLE, HandleMaterialStats.ID),
        new PartMaterialType(MaterialItemFixture.MATERIAL_ITEM_EXTRA, ExtraMaterialStats.ID)
      ),
      ImmutableSet.of(TEST_CATEGORY, Category.HARVEST)
    );
  }

  private ToolDefinitionFixture() {
  }
}
