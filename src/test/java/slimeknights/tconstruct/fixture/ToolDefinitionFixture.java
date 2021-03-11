package slimeknights.tconstruct.fixture;

import com.google.common.collect.ImmutableList;
import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;
import slimeknights.tconstruct.library.tools.ToolDefinition;

public final class ToolDefinitionFixture {
  public static ToolDefinition getTestToolDefinition() {
    return new ToolDefinition(
      new ToolBaseStatDefinition.Builder().setDamageModifier(1f).build(),
      () -> ImmutableList.of(MaterialItemFixture.MATERIAL_ITEM, MaterialItemFixture.MATERIAL_ITEM_2)
    );
  }

  public static ToolDefinition getStandardToolDefinition() {
    return new ToolDefinition(
      new ToolBaseStatDefinition.Builder().setDamageModifier(1f).build(),
      () -> ImmutableList.of(MaterialItemFixture.MATERIAL_ITEM_HEAD, MaterialItemFixture.MATERIAL_ITEM_HANDLE, MaterialItemFixture.MATERIAL_ITEM_EXTRA)
    );
  }

  private ToolDefinitionFixture() {
  }
}
