package slimeknights.tconstruct.fixture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;
import slimeknights.tconstruct.library.tools.ToolDefinition;

public final class ToolDefinitionFixture {

  public static final Category TEST_CATEGORY = new Category("test");

  public static ToolDefinition getTestToolDefinition() {
    return new ToolDefinition(
      new ToolBaseStatDefinition.Builder().setDamageModifier(1f).build(),
      () -> ImmutableList.of(MaterialItemFixture.MATERIAL_ITEM, MaterialItemFixture.MATERIAL_ITEM_2),
      ImmutableSet.of(TEST_CATEGORY)
    );
  }

  public static ToolDefinition getStandardToolDefinition() {
    return new ToolDefinition(
      new ToolBaseStatDefinition.Builder().setDamageModifier(1f).build(),
      () -> ImmutableList.of(MaterialItemFixture.MATERIAL_ITEM_HEAD, MaterialItemFixture.MATERIAL_ITEM_HANDLE, MaterialItemFixture.MATERIAL_ITEM_EXTRA),
      ImmutableSet.of(TEST_CATEGORY, Category.HARVEST)
    );
  }

  private ToolDefinitionFixture() {
  }
}
