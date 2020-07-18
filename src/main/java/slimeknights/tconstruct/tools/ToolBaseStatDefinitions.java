package slimeknights.tconstruct.tools;

import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;

public final class ToolBaseStatDefinitions {

  static final ToolBaseStatDefinition PICKAXE = new ToolBaseStatDefinition.Builder().setDamageModifier(1).build();
  static final ToolBaseStatDefinition HAMMER = new ToolBaseStatDefinition.Builder().setDamageModifier(1.2f).setAttackSpeed(0.8f).setMiningSpeedModifer(0.4f).build();

  static final ToolBaseStatDefinition SHOVEL = new ToolBaseStatDefinition.Builder().setDamageModifier(0.9f).setAttackSpeed(1f).build();

  static final ToolBaseStatDefinition BROADSWORD = new ToolBaseStatDefinition.Builder().setDamageModifier(1).setAttackSpeed(1.6).setMiningSpeedModifer(0.5f).build();

  private ToolBaseStatDefinitions() {
  }
}
