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
      ImmutableList.of(PartMaterialTypeFixture.PART_MATERIAL_TYPE, PartMaterialTypeFixture.PART_MATERIAL_TYPE_2),
      ImmutableSet.of(TEST_CATEGORY)
    );
  }

  private ToolDefinitionFixture() {
  }
}
