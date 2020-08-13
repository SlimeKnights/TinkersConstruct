package slimeknights.tconstruct.tools;

import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;

public final class ToolBaseStatDefinitions {

  static final ToolBaseStatDefinition PICKAXE = new ToolBaseStatDefinition.Builder().setDamageModifier(1).build();
  static final ToolBaseStatDefinition HAMMER = new ToolBaseStatDefinition.Builder().setDamageModifier(1.2f).setAttackSpeed(0.8f).setMiningSpeedModifer(0.4f).build();

  static final ToolBaseStatDefinition SHOVEL = new ToolBaseStatDefinition.Builder().setDamageModifier(0.9f).setAttackSpeed(1f).build();
  static final ToolBaseStatDefinition EXCAVATOR = new ToolBaseStatDefinition.Builder().setDamageModifier(1.25f).setAttackSpeed(0.7f).setMiningSpeedModifer(0.28f).build();

  static final ToolBaseStatDefinition AXE = new ToolBaseStatDefinition.Builder().setDamageModifier(1.1f).setAttackSpeed(1.1f).setKnockbackModifier(1.3f).build();

  static final ToolBaseStatDefinition KAMA = new ToolBaseStatDefinition.Builder().setDamageModifier(1.0f).setAttackSpeed(1.3f).build();

  static final ToolBaseStatDefinition BROADSWORD = new ToolBaseStatDefinition.Builder().setDamageModifier(1).setAttackSpeed(1.6).setMiningSpeedModifer(0.5f).build();

  private ToolBaseStatDefinitions() {
  }
}
