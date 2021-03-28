package slimeknights.tconstruct.tools;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToolBaseStatDefinitions {
  // pickaxes
  static final ToolBaseStatDefinition PICKAXE = new ToolBaseStatDefinition.Builder().setDamageBonus(1f).setAttackSpeed(1.2f).build();
  static final ToolBaseStatDefinition SLEDGE_HAMMER = new ToolBaseStatDefinition.Builder()
    .setDamageBonus(1f).setDamageModifier(1.5f)
    .setAttackSpeed(0.8f).setMiningSpeedModifier(0.4f)
    .setDurabilityModifier(4f).setPrimaryHeadWeight(2)
    .setDefaultUpgrades(2).build();

  // shovels
  static final ToolBaseStatDefinition MATTOCK = new ToolBaseStatDefinition.Builder().setDamageBonus(1f).setAttackSpeed(1f).build();
  static final ToolBaseStatDefinition EXCAVATOR = new ToolBaseStatDefinition.Builder()
    .setDamageBonus(1.5f).setDamageModifier(1.25f)
    .setAttackSpeed(0.7f).setMiningSpeedModifier(0.3f)
    .setDurabilityModifier(3.75f)
    .setKnockbackBonus(1f).setDefaultUpgrades(2)
    .build();

  // axes
  static final ToolBaseStatDefinition AXE = new ToolBaseStatDefinition.Builder().setDamageBonus(5.0f).setAttackSpeed(1.1f).build();

  // scythes
  static final ToolBaseStatDefinition KAMA = new ToolBaseStatDefinition.Builder().setDamageBonus(1.25f).setAttackSpeed(1.3f).build();

  // swords
  static final ToolBaseStatDefinition BROADSWORD = new ToolBaseStatDefinition.Builder()
    .setDamageBonus(3f).setAttackSpeed(1.6f).setMiningSpeedModifier(0.5f).setDurabilityModifier(1.1f).build();
}
