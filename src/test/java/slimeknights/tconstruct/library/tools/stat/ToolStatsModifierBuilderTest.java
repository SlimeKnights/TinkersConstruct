package slimeknights.tconstruct.library.tools.stat;

import net.minecraft.world.item.Tiers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

class ToolStatsModifierBuilderTest extends BaseMcTest {
  private final StatsNBT testStatsNBT = StatsNBT.builder()
                                                .set(ToolStats.DURABILITY, 100f)
                                                .set(ToolStats.HARVEST_TIER, Tiers.STONE)
                                                .set(ToolStats.ATTACK_DAMAGE, 2f)
                                                .set(ToolStats.MINING_SPEED, 3f)
                                                .set(ToolStats.ATTACK_SPEED, 5f)
                                                .build();

  @BeforeAll
  static void setup() {
    setupTierSorting();
  }

  @Test
  void empty() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt).isEqualTo(testStatsNBT);
  }


  /* Tier tool stats */

  @Test
  void tierToolStat_defaultStat() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.ATTACK_DAMAGE.add(builder, 1);
    StatsNBT nbt = builder.build(StatsNBT.EMPTY);
    assertThat(nbt.get(ToolStats.HARVEST_TIER)).isEqualTo(ToolStats.HARVEST_TIER.getDefaultValue());
  }

  @Test
  void tierToolStat_noChangeCopy() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.ATTACK_DAMAGE.add(builder, 1);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.get(ToolStats.HARVEST_TIER)).isEqualTo(Tiers.STONE);
  }


  @Test
  void tierToolStat_replace() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.HARVEST_TIER.update(builder, Tiers.NETHERITE);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.get(ToolStats.HARVEST_TIER)).isEqualTo(Tiers.NETHERITE);
  }

  @Test
  void tierToolStat_missing() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.HARVEST_TIER.update(builder, Tiers.DIAMOND);
    StatsNBT nbt = builder.build(StatsNBT.EMPTY);
    assertThat(nbt.get(ToolStats.HARVEST_TIER)).isEqualTo(Tiers.DIAMOND);
  }

  @Test
  void tierToolStat_largest() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.HARVEST_TIER.update(builder, Tiers.IRON);
    ToolStats.HARVEST_TIER.update(builder, Tiers.DIAMOND);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.get(ToolStats.HARVEST_TIER)).isEqualTo(Tiers.DIAMOND);

    builder = ModifierStatsBuilder.builder();
    ToolStats.HARVEST_TIER.update(builder, Tiers.NETHERITE);
    ToolStats.HARVEST_TIER.update(builder, Tiers.DIAMOND);
    nbt = builder.build(testStatsNBT);
    assertThat(nbt.get(ToolStats.HARVEST_TIER)).isEqualTo(Tiers.NETHERITE);
  }

  @Test
  void tierToolStat_preserveStats() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.HARVEST_TIER.update(builder, Tiers.GOLD);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.get(ToolStats.HARVEST_TIER)).isEqualTo(Tiers.STONE);
  }


  /* Float stat value */

  @Test
  void floatToolStat_defaultStat() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.ATTACK_DAMAGE.add(builder, 1);
    StatsNBT nbt = builder.build(StatsNBT.EMPTY);
    assertThat(nbt.get(ToolStats.DURABILITY)).isEqualTo(ToolStats.DURABILITY.getDefaultValue());
  }

  @Test
  void floatToolStat_add() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.add(builder, 10);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getInt(ToolStats.DURABILITY)).isEqualTo(110);
  }

  @Test
  void floatToolStat_addMissing() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.add(builder, 10);
    StatsNBT nbt = builder.build(StatsNBT.EMPTY);
    assertThat(nbt.getInt(ToolStats.DURABILITY)).isEqualTo(11);
  }

  @Test
  void floatToolStat_addMultiple() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.add(builder, 10);
    ToolStats.DURABILITY.add(builder, 15);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getInt(ToolStats.DURABILITY)).isEqualTo(125);
  }

  @Test
  void floatToolStat_multiply() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.multiply(builder, 2f);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getInt(ToolStats.DURABILITY)).isEqualTo(200);
  }

  @Test
  void floatToolStat_multiplyMissing() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.multiply(builder, 2f);
    StatsNBT nbt = builder.build(StatsNBT.EMPTY);
    assertThat(nbt.getInt(ToolStats.DURABILITY)).isEqualTo(2);
  }

  @Test
  void floatToolStat_multiplyMultiple() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.multiply(builder, 2f);
    ToolStats.DURABILITY.multiply(builder, 1.5f);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getInt(ToolStats.DURABILITY)).isEqualTo(300);
  }

  @Test
  void durability_order() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.add(builder, 10);
    ToolStats.DURABILITY.multiply(builder, 2f);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getInt(ToolStats.DURABILITY)).isEqualTo(220);

    builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.multiply(builder, 2f);
    ToolStats.DURABILITY.add(builder, 10);
    nbt = builder.build(testStatsNBT);
    assertThat(nbt.getInt(ToolStats.DURABILITY)).isEqualTo(220);
  }

  @Test
  void floatToolStat_min() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.DURABILITY.add(builder, Short.MIN_VALUE);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.get(ToolStats.DURABILITY)).isEqualTo(ToolStats.DURABILITY.getMinValue());
  }

  @Test
  void floatToolStat_max() {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    ToolStats.ATTACK_DAMAGE.add(builder, 4096);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.get(ToolStats.ATTACK_DAMAGE)).isEqualTo(ToolStats.ATTACK_DAMAGE.getMaxValue());
  }
}
