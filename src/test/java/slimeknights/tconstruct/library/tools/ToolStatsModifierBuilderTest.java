package slimeknights.tconstruct.library.tools;

import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.tools.ToolStatsModifierBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class ToolStatsModifierBuilderTest {
  private final StatsNBT testStatsNBT = new StatsNBT(100, 2, 2f, 3f, 5f);

  @Test
  void empty() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt).isEqualTo(testStatsNBT);
  }


  /* Mining level */

  @Test
  void harvestLevel_replace() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.setHarvestLevel(10);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getHarvestLevel()).isEqualTo(10);
  }

  @Test
  void harvestLevel_largest() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.setHarvestLevel(6);
    builder.setHarvestLevel(10);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getHarvestLevel()).isEqualTo(10);

    builder = ToolStatsModifierBuilder.builder();
    builder.setHarvestLevel(10);
    builder.setHarvestLevel(6);
    nbt = builder.build(testStatsNBT);
    assertThat(nbt.getHarvestLevel()).isEqualTo(10);
  }

  @Test
  void harvestLevel_preserveStats() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.setHarvestLevel(1);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getHarvestLevel()).isEqualTo(2);
  }


  /* Durability */

  @Test
  void durability_add() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.addDurability(10);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getDurability()).isEqualTo(110);
  }

  @Test
  void durability_addMultiple() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.addDurability(10);
    builder.addDurability(15);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getDurability()).isEqualTo(125);
  }

  @Test
  void durability_multiply() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.multiplyDurability(2f);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getDurability()).isEqualTo(200);
  }

  @Test
  void durability_multiplyMultiple() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.multiplyDurability(2f);
    builder.multiplyDurability(1.5f);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getDurability()).isEqualTo(300);
  }

  @Test
  void durability_order() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.addDurability(10);
    builder.multiplyDurability(2);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getDurability()).isEqualTo(220);
  }


  /* Attack Damage */

  @Test
  void attackDamage_add() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.addAttackDamage(10);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getAttackDamage()).isEqualTo(12f);
  }

  @Test
  void attackDamage_addMultiple() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.addAttackDamage(10);
    builder.addAttackDamage(15);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getAttackDamage()).isEqualTo(27f);
  }

  @Test
  void attackDamage_multiply() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.multiplyAttackDamage(2f);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getAttackDamage()).isEqualTo(4f);
  }

  @Test
  void attackDamage_multiplyMultiple() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.multiplyAttackDamage(2f);
    builder.multiplyAttackDamage(1.5f);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getAttackDamage()).isEqualTo(6f);
  }

  @Test
  void attackDamage_order() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.addAttackDamage(10);
    builder.multiplyAttackDamage(2f);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getAttackDamage()).isEqualTo(24f);
  }


  /* Mining Speed */

  @Test
  void miningSpeed_add() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.addMiningSpeed(10);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getMiningSpeed()).isEqualTo(13f);
  }

  @Test
  void miningSpeed_addMultiple() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.addMiningSpeed(10);
    builder.addMiningSpeed(15);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getMiningSpeed()).isEqualTo(28f);
  }

  @Test
  void miningSpeed_multiply() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.multiplyMiningSpeed(2f);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getMiningSpeed()).isEqualTo(6f);
  }

  @Test
  void miningSpeed_multiplyMultiple() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.multiplyMiningSpeed(2f);
    builder.multiplyMiningSpeed(1.5f);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getMiningSpeed()).isEqualTo(9f);
  }

  @Test
  void miningSpeed_order() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.addMiningSpeed(10);
    builder.multiplyMiningSpeed(2f);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getMiningSpeed()).isEqualTo(26f);
  }


  /* Attack Speed */

  @Test
  void attackSpeed_add() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.addAttackSpeed(10);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getAttackSpeed()).isEqualTo(15f);
  }

  @Test
  void attackSpeed_addMultiple() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.addAttackSpeed(10);
    builder.addAttackSpeed(15);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getAttackSpeed()).isEqualTo(30f);
  }

  @Test
  void attackSpeed_multiply() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.multiplyAttackSpeed(2f);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getAttackSpeed()).isEqualTo(10f);
  }

  @Test
  void attackSpeed_multiplyMultiple() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.multiplyAttackSpeed(2f);
    builder.multiplyAttackSpeed(1.5f);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getAttackSpeed()).isEqualTo(15f);
  }

  @Test
  void attackSpeed_order() {
    ToolStatsModifierBuilder builder = ToolStatsModifierBuilder.builder();
    builder.addAttackSpeed(10);
    builder.multiplyAttackSpeed(2f);
    StatsNBT nbt = builder.build(testStatsNBT);
    assertThat(nbt.getAttackSpeed()).isEqualTo(30f);
  }
}
