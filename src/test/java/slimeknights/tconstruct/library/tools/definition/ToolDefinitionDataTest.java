package slimeknights.tconstruct.library.tools.definition;

import com.google.common.collect.ImmutableMap;
import net.neoforged.neoforge.common.ItemAbilities;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.build.MultiplyStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.SetStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolActionsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolSlotsModule;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.test.TestHelper;
import slimeknights.tconstruct.test.TestHelper.ToolDefinitionStats;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ToolDefinitionDataTest extends BaseMcTest {
  /** Checks that the stats are all empty */
  protected static void checkStatsEmpty(ToolDefinitionData data) {
    ToolDefinitionStats stats = TestHelper.buildStats(data);
    assertThat(stats.base().getContainedStats()).isEmpty();
    assertThat(stats.multipliers().getContainedStats()).isEmpty();
  }

  /** Checks that the stats are all empty */
  protected static void checkToolDataNonPartsEmpty(ToolDefinitionData data) {
    checkStatsEmpty(data);
    assertThat(TestHelper.getTraits(data)).isEmpty();
  }

  /** Checks that the stats are all empty */
  protected static void checkToolDataEmpty(ToolDefinitionData data) {
    List<IToolPart> parts = data.getHook(ToolHooks.TOOL_PARTS).getParts(ToolDefinition.EMPTY);
    assertThat(parts).isNotNull();
    assertThat(parts).isEmpty();
    checkToolDataNonPartsEmpty(data);
    assertThat(data.getHooks().getAllModules()).isEmpty();
  }

  @Test
  void data_emptyContainsNoData() {
    checkToolDataEmpty(ToolDefinitionData.EMPTY);
  }

  @Test
  void data_getStatBonus() {
    ToolDefinitionData withBonuses = ToolDefinitionDataBuilder
      .builder()
      .module(new SetStatsModule(StatsNBT.builder().set(ToolStats.DURABILITY, 100).set(ToolStats.ATTACK_SPEED, 5.5f).build()))
      .build();

    // ensure stats are in the right place
    ToolDefinitionStats stats = TestHelper.buildStats(withBonuses);
    assertThat(stats.base().getContainedStats()).hasSize(2);
    assertThat(stats.multipliers().getContainedStats()).isEmpty();
    assertThat(stats.base().get(ToolStats.DURABILITY)).isEqualTo(100);
    assertThat(stats.base().get(ToolStats.ATTACK_SPEED)).isEqualTo(5.5f);
    // note mining speed was chosen as it has a non-zero default
    assertThat(stats.base().get(ToolStats.MINING_SPEED)).isEqualTo(ToolStats.MINING_SPEED.getDefaultValue());
  }

  @Test
  void data_getStatMultiplier() {
    ToolDefinitionData withMultipliers = ToolDefinitionDataBuilder
      .builder()
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.DURABILITY, 10)
        .set(ToolStats.ATTACK_SPEED, 2.5f).build()))
      .build();

    // ensure stats are in the right place
    ToolDefinitionStats stats = TestHelper.buildStats(withMultipliers);
    assertThat(stats.base().getContainedStats()).isEmpty();
    assertThat(stats.multipliers().getContainedStats()).hasSize(2);
    assertThat(stats.multipliers().get(ToolStats.DURABILITY)).isEqualTo(10);
    assertThat(stats.multipliers().get(ToolStats.ATTACK_SPEED)).isEqualTo(2.5f);
    // stat not present
    assertThat(stats.multipliers().get(ToolStats.MINING_SPEED)).isEqualTo(1);
  }

  @Test
  void data_buildStats_empty() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.MINING_SPEED.add(builder, 5);
    ToolStats.ATTACK_DAMAGE.add(builder, 3);
    ToolDefinitionData.EMPTY.getHook(ToolHooks.TOOL_STATS).addToolStats(mock(IToolContext.class), builder);

    StatsNBT stats = builder.build();
    assertThat(stats.getContainedStats()).hasSize(2);
    assertThat(stats.getContainedStats()).contains(ToolStats.MINING_SPEED);
    assertThat(stats.getContainedStats()).contains(ToolStats.ATTACK_DAMAGE);
    assertThat(stats.get(ToolStats.MINING_SPEED)).isEqualTo(6); // adds to the base value of 1
    assertThat(stats.get(ToolStats.ATTACK_DAMAGE)).isEqualTo(3); // base value 0
  }

  @Test
  void data_buildStats_withData() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.MINING_SPEED.add(builder, 5);
    ToolStats.DURABILITY.update(builder, 100f);

    ToolDefinitionData data = ToolDefinitionDataBuilder
      .builder()
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.MINING_SPEED, 5)
        .set(ToolStats.ATTACK_SPEED, 2).build()))
      .build();
    data.getHook(ToolHooks.TOOL_STATS).addToolStats(mock(IToolContext.class), builder);

    StatsNBT stats = builder.build();
    assertThat(stats.getContainedStats()).hasSize(3);
    assertThat(stats.getContainedStats()).contains(ToolStats.DURABILITY);
    assertThat(stats.getContainedStats()).contains(ToolStats.MINING_SPEED);
    assertThat(stats.getContainedStats()).contains(ToolStats.ATTACK_SPEED);
    assertThat(stats.get(ToolStats.MINING_SPEED)).isEqualTo(30); // (1+5)*6
    assertThat(stats.get(ToolStats.DURABILITY)).isEqualTo(100); // ignores base value
    assertThat(stats.get(ToolStats.ATTACK_SPEED)).isEqualTo(2); // (1)*2
  }

  @Test
  void data_buildSlots_withData() {
    ToolDataNBT modData = new ToolDataNBT();
    ToolDefinitionData data = ToolDefinitionDataBuilder
      .builder()
      .module(new ToolSlotsModule(ImmutableMap.of(SlotType.UPGRADE, 5, SlotType.ABILITY, 2)))
      .build();
    data.getHook(ToolHooks.VOLATILE_DATA).addVolatileData(mock(IToolContext.class), modData);
    assertThat(modData.getSlots(SlotType.UPGRADE)).isEqualTo(5);
    assertThat(modData.getSlots(SlotType.ABILITY)).isEqualTo(2);
    for (SlotType type : SlotType.getAllSlotTypes()) {
      if (type != SlotType.UPGRADE && type != SlotType.ABILITY) {
        assertThat(modData.getSlots(type)).overridingErrorMessage("Slot type %s has a value", type.getName()).isEqualTo(0);
      }
    }

    // packet buffers handled in packet test
  }

  @Test
  void actions_canPerform() {
    IToolStackView context = mock(IToolStackView.class);
    assertThat(ToolDefinitionData.EMPTY.getHook(ToolHooks.TOOL_ACTION).canPerformAction(context, ItemAbilities.SHOVEL_FLATTEN)).isFalse();
    assertThat(ToolDefinitionData.EMPTY.getHook(ToolHooks.TOOL_ACTION).canPerformAction(context, ItemAbilities.SWORD_DIG)).isFalse();
    ToolDefinitionData newData = ToolDefinitionDataBuilder.builder().module(ToolActionsModule.of(ItemAbilities.SHOVEL_FLATTEN)).build();
    assertThat(newData.getHook(ToolHooks.TOOL_ACTION).canPerformAction(context, ItemAbilities.SHOVEL_FLATTEN)).isTrue();
    assertThat(newData.getHook(ToolHooks.TOOL_ACTION).canPerformAction(context, ItemAbilities.SWORD_DIG)).isFalse();
  }
}
