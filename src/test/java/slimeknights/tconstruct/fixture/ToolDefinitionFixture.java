package slimeknights.tconstruct.fixture;

import com.google.common.collect.ImmutableList;
import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;
import slimeknights.tconstruct.library.tools.ToolDefinition;

public final class ToolDefinitionFixture {
  public static ToolBaseStatDefinition DEFAULT_BASE_STATS = ToolDefinition.EMPTY.getBaseStatDefinition();

  public static ToolDefinition getTestToolDefinition() {
    return new ToolDefinition(DEFAULT_BASE_STATS,
      () -> ImmutableList.of(MaterialItemFixture.MATERIAL_ITEM, MaterialItemFixture.MATERIAL_ITEM_2)
    );
  }

  public static ToolDefinition getStandardToolDefinition() {
    return new ToolDefinition(DEFAULT_BASE_STATS,
      () -> ImmutableList.of(MaterialItemFixture.MATERIAL_ITEM_HEAD, MaterialItemFixture.MATERIAL_ITEM_HANDLE, MaterialItemFixture.MATERIAL_ITEM_EXTRA)
    );
  }

  private ToolDefinitionFixture() {
  }
}
