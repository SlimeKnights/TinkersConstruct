package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialItemFixture;
import slimeknights.tconstruct.fixture.MaterialStatsFixture;
import slimeknights.tconstruct.fixture.ToolDefinitionFixture;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionDataBuilder;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.item.ToolItemTest;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.test.BlockHarvestLogic;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: update
class ToolHarvestLogicTest extends ToolItemTest {

  private final ModifiableItem pickaxeTool = new ModifiableItem(
    new Item.Properties(),
    ToolDefinitionFixture.getStandardToolDefinition());

  @Test
  void calcSpeed_dirt_notEffective() {
    ItemStack tool = buildTestTool(pickaxeTool);

    float speed = ToolHarvestLogic.getDestroySpeed(tool, Blocks.DIRT.defaultBlockState());

    assertThat(speed).isEqualTo(1f);
  }

  @Test
  void calcSpeed_cobble_effective() {
    ItemStack tool = buildTestTool(pickaxeTool);

    float speed = ToolHarvestLogic.getDestroySpeed(tool, Blocks.COBBLESTONE.defaultBlockState());

    assertThat(speed).isEqualTo(MaterialStatsFixture.MATERIAL_STATS_HEAD.getMiningSpeed());
  }

  @Test
  void calcSpeed_obsidian_notEnoughHarvestLevel() {
    ItemStack tool = buildTestTool(pickaxeTool);

    float speed = ToolHarvestLogic.getDestroySpeed(tool, Blocks.OBSIDIAN.defaultBlockState());

    assertThat(speed).isEqualTo(1f);
  }

  @Test
  void calcSpeed_broken_slowButNotZero() {
    ItemStack tool = buildTestTool(pickaxeTool);
    breakTool(tool);

    float speed = ToolHarvestLogic.getDestroySpeed(tool, Blocks.DIRT.defaultBlockState());

    assertThat(speed).isLessThan(1f);
    assertThat(speed).isGreaterThan(0f);
  }

  @Test
  void calcSpeed_effective_withMiningModifier() {
    float modifier = 2f;

    ToolDefinition definition = ToolDefinition.builder(new ResourceLocation("test", "mining_tool")).meleeHarvest().skipRegister().build();
    definition.setData(ToolDefinitionDataBuilder
                         .builder()
                         .harvestLogic(new BlockHarvestLogic(Blocks.COBBLESTONE))
                         .part(MaterialItemFixture.MATERIAL_ITEM_HEAD)
                         .part(MaterialItemFixture.MATERIAL_ITEM_HANDLE)
                         .part(MaterialItemFixture.MATERIAL_ITEM_EXTRA)
                         .multiplier(ToolStats.MINING_SPEED, modifier)
                         .build());

    IModifiable toolWithMiningModifier = new ModifiableItem(new Item.Properties(), definition);
    ItemStack tool = buildTestTool(toolWithMiningModifier);

    // boosted by correct block
    float speed = ToolHarvestLogic.getDestroySpeed(tool, Blocks.COBBLESTONE.defaultBlockState());
    assertThat(speed).isEqualTo(MaterialStatsFixture.MATERIAL_STATS_HEAD.getMiningSpeed() * modifier);

    // default speed
    speed = ToolHarvestLogic.getDestroySpeed(tool, Blocks.STONE.defaultBlockState());
    assertThat(speed).isEqualTo(1.0f);
  }
}
