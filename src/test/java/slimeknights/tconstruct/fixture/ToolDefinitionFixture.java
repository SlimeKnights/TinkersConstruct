package slimeknights.tconstruct.fixture;

import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.IToolStatProvider;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionDataBuilder;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.tools.MeleeHarvestToolStatsBuilder;

import java.util.List;

public final class ToolDefinitionFixture {
  private static final ResourceLocation DEFINITION_ID = new ResourceLocation("test", "test_tool");
  private static final IToolStatProvider TEST_STATS_PROVIDER = new IToolStatProvider() {
    @Override
    public StatsNBT buildStats(ToolDefinition definition, List<IMaterial> materials) {
      return MeleeHarvestToolStatsBuilder.from(definition, materials).buildStats();
    }

    @Override
    public boolean isMultipart() {
      return true;
    }

    @Override
    public ToolDefinitionData getDefaultData() {
      return ToolDefinitionDataBuilder.builder()
                                      .part(MaterialItemFixture.MATERIAL_ITEM_HEAD)
                                      .part(MaterialItemFixture.MATERIAL_ITEM_HANDLE)
                                      .part(MaterialItemFixture.MATERIAL_ITEM_EXTRA)
                                      .build();
    }
  };

  /** Standard tool definition for testing */
  private static final ToolDefinition STANDARD_TOOL_DEFINITION = ToolDefinition.builder(DEFINITION_ID).setStatsProvider(TEST_STATS_PROVIDER).skipRegister().build();
  public static ToolDefinition getStandardToolDefinition() {
    return STANDARD_TOOL_DEFINITION;
  }

  private ToolDefinitionFixture() {}
}
