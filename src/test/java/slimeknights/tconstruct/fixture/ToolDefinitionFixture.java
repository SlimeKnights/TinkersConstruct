package slimeknights.tconstruct.fixture;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.util.Lazy;
import slimeknights.tconstruct.library.tools.definition.IToolStatProvider;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionDataBuilder;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.test.BlockHarvestLogic;
import slimeknights.tconstruct.tools.MeleeHarvestToolStatsBuilder;

public final class ToolDefinitionFixture {
  private static final ResourceLocation DEFINITION_ID = new ResourceLocation("test", "test_tool");
  private static final IToolStatProvider TEST_STATS_PROVIDER = new IToolStatProvider() {
    private final Lazy<ToolDefinitionData> DATA = Lazy.of(
      () -> ToolDefinitionDataBuilder.builder()
                                     .part(MaterialItemFixture.MATERIAL_ITEM_HEAD)
                                     .part(MaterialItemFixture.MATERIAL_ITEM_HANDLE)
                                     .part(MaterialItemFixture.MATERIAL_ITEM_EXTRA)
                                     .action(ToolActions.PICKAXE_DIG)
                                     .harvestLogic(new BlockHarvestLogic(Blocks.STONE))
                                     .build());
    @Override
    public StatsNBT buildStats(ToolDefinition definition, MaterialNBT materials) {
      return MeleeHarvestToolStatsBuilder.from(definition, materials).buildStats();
    }

    @Override
    public boolean isMultipart() {
      return true;
    }

    @Override
    public ToolDefinitionData getDefaultData() {
      return DATA.get();
    }
  };

  /** Standard tool definition for testing */
  private static final ToolDefinition STANDARD_TOOL_DEFINITION = ToolDefinition.builder(DEFINITION_ID).setStatsProvider(TEST_STATS_PROVIDER).skipRegister().build();
  public static ToolDefinition getStandardToolDefinition() {
    return STANDARD_TOOL_DEFINITION;
  }

  private ToolDefinitionFixture() {}
}
