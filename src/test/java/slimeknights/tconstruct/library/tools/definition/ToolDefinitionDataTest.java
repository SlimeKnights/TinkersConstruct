package slimeknights.tconstruct.library.tools.definition;

import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

class ToolDefinitionDataTest extends BaseMcTest {
  /** Checks that the stats are all empty */
  protected static void checkStatsEmpty(ToolDefinitionData.Stats stats) {
    DefinitionToolStats base = stats.getBase();
    assertThat(base).isNotNull();
    assertThat(base.containedStats()).isEmpty();
    DefinitionToolStats multipliers = stats.getMultipliers();
    assertThat(multipliers).isNotNull();
    assertThat(multipliers.containedStats()).isEmpty();
  }

  /** Checks that the stats are all empty */
  protected static void checkToolDataNonPartsEmpty(ToolDefinitionData data) {
    assertThat(data.getStats()).isNotNull();
    checkStatsEmpty(data.getStats());
    assertThat(data.getSlots()).isNotNull();
    assertThat(data.getSlots().containedTypes()).isEmpty();
    assertThat(data.getTraits()).isNotNull();
    assertThat(data.getTraits()).isEmpty();
  }

  /** Checks that the stats are all empty */
  protected static void checkToolDataEmpty(ToolDefinitionData data) {
    assertThat(data.getParts()).isNotNull();
    assertThat(data.getParts()).isEmpty();
    checkToolDataNonPartsEmpty(data);
  }

  @Test
  void stats_emptyContainsNoData() {
    checkStatsEmpty(ToolDefinitionData.EMPTY_STATS);
  }

  @Test
  void stats_nullDefaults() {
    checkStatsEmpty(new ToolDefinitionData.Stats(null, null));
  }

  @Test
  void data_emptyContainsNoData() {
    checkToolDataEmpty(ToolDefinitionData.EMPTY);
  }

  @Test
  void data_nullContainsNoData() {
    checkToolDataEmpty(new ToolDefinitionData(null, null, null, null));
  }

  @Test
  void data_getStartingSlots() {
    assertThat(ToolDefinitionData.EMPTY.getStartingSlots(SlotType.UPGRADE)).isEqualTo(0);
    assertThat(ToolDefinitionData.EMPTY.getStartingSlots(SlotType.ABILITY)).isEqualTo(0);
    ToolDefinitionData data = ToolDefinitionDataBuilder.builder().startingSlots(SlotType.ABILITY, 5).build();
    assertThat(data.getStartingSlots(SlotType.UPGRADE)).isEqualTo(0);
    assertThat(data.getStartingSlots(SlotType.ABILITY)).isEqualTo(5);
  }

  @Test
  void data_getStatBonus() {
    assertThat(ToolDefinitionData.EMPTY.getAllBaseStats()).isEmpty();
    ToolDefinitionData withBonuses = ToolDefinitionDataBuilder
      .builder()
      .stat(ToolStats.DURABILITY, 100)
      .stat(ToolStats.ATTACK_SPEED, 5.5f)
      .build();

    // ensure stats are in the right place
    assertThat(withBonuses.getAllBaseStats()).hasSize(2);
    assertThat(withBonuses.getStats().getMultipliers().containedStats()).isEmpty();
    assertThat(withBonuses.getBaseStat(ToolStats.DURABILITY)).isEqualTo(100);
    assertThat(withBonuses.getBonus(ToolStats.DURABILITY)).isEqualTo(100);
    assertThat(withBonuses.getBaseStat(ToolStats.ATTACK_SPEED)).isEqualTo(5.5f);
    assertThat(withBonuses.getBonus(ToolStats.ATTACK_SPEED)).isEqualTo(5.5f);
    // note mining speed was chosen as it has a non-zero default
    assertThat(withBonuses.getBaseStat(ToolStats.MINING_SPEED)).isEqualTo(ToolStats.MINING_SPEED.getDefaultValue());
    assertThat(withBonuses.getBonus(ToolStats.MINING_SPEED)).isEqualTo(0);
  }

  @Test
  void data_getStatMultiplier() {
    ToolDefinitionData withMultipliers = ToolDefinitionDataBuilder
      .builder()
      .multiplier(ToolStats.DURABILITY, 10)
      .multiplier(ToolStats.ATTACK_SPEED, 2.5f)
      .build();

    // ensure stats are in the right place
    assertThat(withMultipliers.getAllBaseStats()).isEmpty();
    assertThat(withMultipliers.getStats().getMultipliers().containedStats()).hasSize(2);
    assertThat(withMultipliers.getMultiplier(ToolStats.DURABILITY)).isEqualTo(10);
    assertThat(withMultipliers.getMultiplier(ToolStats.ATTACK_SPEED)).isEqualTo(2.5f);
    assertThat(withMultipliers.getMultiplier(ToolStats.MINING_SPEED)).isEqualTo(1);
  }

  @Test
  void data_buildStats_empty() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.MINING_SPEED.add(builder, 5);
    ToolStats.ATTACK_DAMAGE.add(builder, 3);
    ToolDefinitionData.EMPTY.buildStatMultipliers(builder);

    StatsNBT stats = builder.build(StatsNBT.EMPTY);
    assertThat(stats.getContainedStats()).hasSize(2);
    assertThat(stats.getContainedStats()).contains(ToolStats.MINING_SPEED);
    assertThat(stats.getContainedStats()).contains(ToolStats.ATTACK_DAMAGE);
    assertThat(stats.getFloat(ToolStats.MINING_SPEED)).isEqualTo(6);
    assertThat(stats.getFloat(ToolStats.ATTACK_DAMAGE)).isEqualTo(3);
  }

  @Test
  void data_buildStats_withData() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.MINING_SPEED.add(builder, 5);
    ToolStats.DURABILITY.add(builder, 100);

    ToolDefinitionData data = ToolDefinitionDataBuilder
      .builder()
      .multiplier(ToolStats.MINING_SPEED, 5)
      .multiplier(ToolStats.ATTACK_SPEED, 2)
      .build();
    data.buildStatMultipliers(builder);

    StatsNBT stats = builder.build(StatsNBT.EMPTY);
    assertThat(stats.getContainedStats()).hasSize(3);
    assertThat(stats.getContainedStats()).contains(ToolStats.DURABILITY);
    assertThat(stats.getContainedStats()).contains(ToolStats.MINING_SPEED);
    assertThat(stats.getContainedStats()).contains(ToolStats.ATTACK_SPEED);
    assertThat(stats.getFloat(ToolStats.MINING_SPEED)).isEqualTo(30);
    assertThat(stats.getFloat(ToolStats.DURABILITY)).isEqualTo(101);
    assertThat(stats.getFloat(ToolStats.ATTACK_SPEED)).isEqualTo(2);
  }

  @Test
  void data_buildSlots_empty() {
    ModDataNBT modData = new ModDataNBT();
    ToolDefinitionData.EMPTY.buildSlots(modData);
    for (SlotType type : SlotType.getAllSlotTypes()) {
      assertThat(modData.getSlots(type)).overridingErrorMessage("Slot type %s has a value", type.getName()).isEqualTo(0);
    }
  }

  @Test
  void data_buildSlots_withData() {
    ModDataNBT modData = new ModDataNBT();
    ToolDefinitionData data = ToolDefinitionDataBuilder
      .builder()
      .startingSlots(SlotType.UPGRADE, 5)
      .startingSlots(SlotType.ABILITY, 2)
      .build();
    data.buildSlots(modData);
    assertThat(modData.getSlots(SlotType.UPGRADE)).isEqualTo(5);
    assertThat(modData.getSlots(SlotType.ABILITY)).isEqualTo(2);
    for (SlotType type : SlotType.getAllSlotTypes()) {
      if (type != SlotType.UPGRADE && type != SlotType.ABILITY) {
        assertThat(modData.getSlots(type)).overridingErrorMessage("Slot type %s has a value", type.getName()).isEqualTo(0);
      }
    }

    // packet buffers handled in packet test
  }
}
