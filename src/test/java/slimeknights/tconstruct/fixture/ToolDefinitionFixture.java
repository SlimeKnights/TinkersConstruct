package slimeknights.tconstruct.fixture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;
import slimeknights.tconstruct.library.tools.ToolDefinition;

public final class ToolDefinitionFixture {

  public static ToolDefinition getTestToolDefinition() {
    return new ToolDefinition(
      new ToolBaseStatDefinition.Builder().setDamageModifier(1f).build(),
      ImmutableList.of(PartMaterialTypeFixture.getTestPartMaterialType()),
      ImmutableSet.of(new Category("test"))
    );
  }

  private ToolDefinitionFixture() {
  }
}
