package slimeknights.tconstruct.tools;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToolBaseStatDefinitions {
  // pickaxes
  static final ToolBaseStatDefinition PICKAXE = new ToolBaseStatDefinition.Builder()
    .bonus(ToolStats.ATTACK_DAMAGE, 0f) // gains +1 damage from tool piercing, hence being lower than vanilla
    .modifier(ToolStats.ATTACK_SPEED, 1.2f)
    .build();
  static final ToolBaseStatDefinition SLEDGE_HAMMER = new ToolBaseStatDefinition.Builder()
    .bonus(ToolStats.ATTACK_DAMAGE, 3f) // gains +5 undead damage from smite modifier
    .modifier(ToolStats.ATTACK_DAMAGE, 1.35f)
    .modifier(ToolStats.ATTACK_SPEED, 0.75f)
    .modifier(ToolStats.MINING_SPEED, 0.4f)
    .modifier(ToolStats.DURABILITY, 4f)
    .setPrimaryHeadWeight(2).setDefaultUpgrades(2).build();
  static final ToolBaseStatDefinition VEIN_HAMMER = new ToolBaseStatDefinition.Builder()
    .bonus(ToolStats.ATTACK_DAMAGE, 3f)
    .modifier(ToolStats.ATTACK_DAMAGE, 1.25f)
    .modifier(ToolStats.ATTACK_SPEED, 1.1f)
    .modifier(ToolStats.MINING_SPEED, 0.3f)
    .modifier(ToolStats.DURABILITY, 5.0f)
    .setPrimaryHeadWeight(2).setDefaultUpgrades(2).build();

  // shovels
  static final ToolBaseStatDefinition MATTOCK = new ToolBaseStatDefinition.Builder()
    .bonus(ToolStats.ATTACK_DAMAGE, 1.5f)
    .modifier(ToolStats.ATTACK_SPEED, 1f)
    .build();
  static final ToolBaseStatDefinition EXCAVATOR = new ToolBaseStatDefinition.Builder()
    .bonus(ToolStats.ATTACK_DAMAGE, 1.5f)
    .modifier(ToolStats.ATTACK_DAMAGE, 1.2f)
    .modifier(ToolStats.ATTACK_SPEED, 1.0f)
    .modifier(ToolStats.MINING_SPEED, 0.3f)
    .modifier(ToolStats.DURABILITY, 3.75f)
    .setDefaultUpgrades(2).build();

  // axes
  static final ToolBaseStatDefinition HAND_AXE = new ToolBaseStatDefinition.Builder()
    .bonus(ToolStats.ATTACK_DAMAGE, 6.0f)
    .modifier(ToolStats.ATTACK_SPEED, 0.9f)
    .build();
  static final ToolBaseStatDefinition BROAD_AXE = new ToolBaseStatDefinition.Builder()
    .bonus(ToolStats.ATTACK_DAMAGE, 5f)
    .modifier(ToolStats.ATTACK_DAMAGE, 1.5f)
    .modifier(ToolStats.ATTACK_SPEED, 0.6f)
    .modifier(ToolStats.MINING_SPEED, 0.3f)
    .modifier(ToolStats.DURABILITY, 4.25f)
    .setPrimaryHeadWeight(2).setDefaultUpgrades(2).build();

  // scythes
  static final ToolBaseStatDefinition KAMA = new ToolBaseStatDefinition.Builder()
    .bonus(ToolStats.ATTACK_DAMAGE, 1f)
    .modifier(ToolStats.ATTACK_DAMAGE, 0.75f)
    .modifier(ToolStats.ATTACK_SPEED, 1.9f)
    .build();
  static final ToolBaseStatDefinition SCYTHE = new ToolBaseStatDefinition.Builder()
    .bonus(ToolStats.ATTACK_DAMAGE, 1f)
    .modifier(ToolStats.ATTACK_SPEED, 0.8f)
    .modifier(ToolStats.MINING_SPEED, 0.45f)
    .modifier(ToolStats.DURABILITY, 2.5f)
    .setDefaultUpgrades(2).build();

  // swords
  static final ToolBaseStatDefinition DAGGER = new ToolBaseStatDefinition.Builder()
    .bonus(ToolStats.ATTACK_DAMAGE, 2f)
    .modifier(ToolStats.ATTACK_DAMAGE, 0.5f)
    .modifier(ToolStats.ATTACK_SPEED, 2.0f)
    .modifier(ToolStats.MINING_SPEED, 0.75f)
    .modifier(ToolStats.DURABILITY, 0.75f).build();
  static final ToolBaseStatDefinition SWORD = new ToolBaseStatDefinition.Builder()
    .bonus(ToolStats.ATTACK_DAMAGE, 3f)
    .modifier(ToolStats.ATTACK_SPEED, 1.6f)
    .modifier(ToolStats.MINING_SPEED, 0.5f)
    .modifier(ToolStats.DURABILITY, 1.1f)
    .setDefaultUpgrades(2).setDefaultAbilities(2).build();
  static final ToolBaseStatDefinition CLEAVER = new ToolBaseStatDefinition.Builder()
    .bonus(ToolStats.ATTACK_DAMAGE, 3.5f)
    .modifier(ToolStats.ATTACK_DAMAGE, 1.5f)
    .modifier(ToolStats.ATTACK_SPEED, 0.9f)
    .modifier(ToolStats.MINING_SPEED, 0.25f)
    .bonus(ToolStats.REACH, 1f)
    .modifier(ToolStats.DURABILITY, 3.5f)
    .setPrimaryHeadWeight(2).setDefaultUpgrades(2).build();
}
