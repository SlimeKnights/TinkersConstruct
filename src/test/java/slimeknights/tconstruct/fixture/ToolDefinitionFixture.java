package slimeknights.tconstruct.fixture;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionDataBuilder;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolActionsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.PartStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveModule;
import slimeknights.tconstruct.test.TestHelper;

public final class ToolDefinitionFixture {
  private static final ResourceLocation DEFINITION_ID = TestHelper.id("test_tool");

  /** Standard tool definition for testing */
  private static final ToolDefinition STANDARD_TOOL_DEFINITION = new ToolDefinition(DEFINITION_ID);
  static {
    STANDARD_TOOL_DEFINITION.setData(
      ToolDefinitionDataBuilder.builder()
                               .module(PartStatsModule.parts()
                                                      .part(MaterialItemFixture.MATERIAL_ITEM_HEAD)
                                                      .part(MaterialItemFixture.MATERIAL_ITEM_HANDLE)
                                                      .part(MaterialItemFixture.MATERIAL_ITEM_EXTRA).build())
                               .module(ToolActionsModule.of(ToolActions.PICKAXE_DIG))
                               .module(new IsEffectiveModule(BlockPredicate.set(Blocks.STONE), true))
                               .smallToolStartingSlots()
                               .build());
  }
  public static ToolDefinition getStandardToolDefinition() {
    return STANDARD_TOOL_DEFINITION;
  }

  private ToolDefinitionFixture() {}
}
