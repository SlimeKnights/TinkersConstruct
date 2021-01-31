package slimeknights.tconstruct.tools;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.tools.ToolStatsBuilder.IStatFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToolBaseStatDefinitions {
  // pickaxes
  static final ToolBaseStatDefinition PICKAXE = new ToolBaseStatDefinition.Builder().setDamageModifier(1).setAttackSpeed(1.2f).build();

  static final ToolBaseStatDefinition HAMMER = new ToolBaseStatDefinition.Builder()
    .setDamageModifier(1.2f).setAttackSpeed(0.8f).setMiningSpeedModifer(0.4f).setStatFactory(durabilityMultiplier(2.5f)).build();

  // shovels
  static final ToolBaseStatDefinition SHOVEL = new ToolBaseStatDefinition.Builder().setDamageModifier(0.9f).setAttackSpeed(1f).build();

  static final ToolBaseStatDefinition EXCAVATOR = new ToolBaseStatDefinition.Builder()
    .setDamageModifier(1.25f).setAttackSpeed(0.7f).setMiningSpeedModifer(0.28f).setStatFactory(durabilityMultiplier(1.75f)).build();

  // axes
  static final ToolBaseStatDefinition AXE = new ToolBaseStatDefinition.Builder()
    .setDamageModifier(1.1f).setAttackSpeed(1.1f).setKnockbackModifier(1.3f)
    .setStatFactory((durability, harvestLevel, attackDamage, miningSpeed, attackSpeed) ->
                      new StatsNBT(durability, harvestLevel, (int) (attackDamage + 0.5f), miningSpeed, attackSpeed)).build();

  // scythes
  static final ToolBaseStatDefinition KAMA = new ToolBaseStatDefinition.Builder().setDamageModifier(1.0f).setAttackSpeed(1.3f).build();

  // swords
  static final ToolBaseStatDefinition BROADSWORD = new ToolBaseStatDefinition.Builder()
    .setDamageModifier(1).setAttackSpeed(1.6).setMiningSpeedModifer(0.5f)
    .setStatFactory((durability, harvestLevel, attackDamage, miningSpeed, attackSpeed) ->
                      new StatsNBT((int)(durability * 1.1f), harvestLevel, (int)(attackDamage + 1), miningSpeed, attackSpeed)).build();


  /** Creates a stat factory that multiplies the durability by the given value */
  private static IStatFactory durabilityMultiplier(final float multiplier) {
    return (durability, harvestLevel, attackDamage, miningSpeed, attackSpeed) ->
      new StatsNBT((int)(durability * multiplier), harvestLevel, attackDamage, miningSpeed, attackSpeed);
  }
}
