package slimeknights.tconstruct.tools;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.tools.ToolDefinition;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToolDefinitions {
  // rock
  public static final ToolDefinition PICKAXE = ToolDefinition.builder(TinkerTools.pickaxe).meleeHarvest().build();
  public static final ToolDefinition SLEDGE_HAMMER = ToolDefinition.builder(TinkerTools.sledgeHammer).meleeHarvest().build();
  public static final ToolDefinition VEIN_HAMMER = ToolDefinition.builder(TinkerTools.veinHammer).meleeHarvest().build();

  // dirt
  public static final ToolDefinition MATTOCK = ToolDefinition.builder(TinkerTools.mattock).meleeHarvest().build();
  public static final ToolDefinition EXCAVATOR = ToolDefinition.builder(TinkerTools.excavator).meleeHarvest().build();

  // wood
  public static final ToolDefinition HAND_AXE = ToolDefinition.builder(TinkerTools.handAxe).meleeHarvest().build();
  public static final ToolDefinition BROAD_AXE = ToolDefinition.builder(TinkerTools.broadAxe).meleeHarvest().build();

  // scythes
  public static final ToolDefinition KAMA = ToolDefinition.builder(TinkerTools.kama).meleeHarvest().build();
  public static final ToolDefinition SCYTHE = ToolDefinition.builder(TinkerTools.scythe).meleeHarvest().build();
  // swords
  public static final ToolDefinition DAGGER = ToolDefinition.builder(TinkerTools.dagger).meleeHarvest().build();
  public static final ToolDefinition SWORD = ToolDefinition.builder(TinkerTools.sword).meleeHarvest().build();
  public static final ToolDefinition CLEAVER = ToolDefinition.builder(TinkerTools.cleaver).meleeHarvest().build();

  // special
  public static final ToolDefinition FLINT_AND_BRONZE = ToolDefinition.builder(TinkerTools.flintAndBronze).noParts().build();
}
