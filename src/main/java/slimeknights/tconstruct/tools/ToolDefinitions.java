package slimeknights.tconstruct.tools;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToolDefinitions {
  // rock
  public static final ToolDefinition PICKAXE = ToolDefinition.create(TinkerTools.pickaxe);
  public static final ToolDefinition SLEDGE_HAMMER = ToolDefinition.create(TinkerTools.sledgeHammer);
  public static final ToolDefinition VEIN_HAMMER = ToolDefinition.create(TinkerTools.veinHammer);

  // dirt
  public static final ToolDefinition MATTOCK = ToolDefinition.create(TinkerTools.mattock);
  public static final ToolDefinition PICKADZE = ToolDefinition.create(TinkerTools.pickadze);
  public static final ToolDefinition EXCAVATOR = ToolDefinition.create(TinkerTools.excavator);

  // wood
  public static final ToolDefinition HAND_AXE = ToolDefinition.create(TinkerTools.handAxe);
  public static final ToolDefinition BROAD_AXE = ToolDefinition.create(TinkerTools.broadAxe);

  // scythes
  public static final ToolDefinition KAMA = ToolDefinition.create(TinkerTools.kama);
  public static final ToolDefinition SCYTHE = ToolDefinition.create(TinkerTools.scythe);
  // swords
  public static final ToolDefinition DAGGER = ToolDefinition.create(TinkerTools.dagger);
  public static final ToolDefinition SWORD = ToolDefinition.create(TinkerTools.sword);
  public static final ToolDefinition CLEAVER = ToolDefinition.create(TinkerTools.cleaver);

  // bows
  public static final ToolDefinition CROSSBOW = ToolDefinition.create(TinkerTools.crossbow);
  public static final ToolDefinition LONGBOW = ToolDefinition.create(TinkerTools.longbow);

  // special
  public static final ToolDefinition FLINT_AND_BRICK = ToolDefinition.create(TinkerTools.flintAndBrick);
  public static final ToolDefinition SKY_STAFF = ToolDefinition.create(TinkerTools.skyStaff);
  public static final ToolDefinition EARTH_STAFF = ToolDefinition.create(TinkerTools.earthStaff);
  public static final ToolDefinition ICHOR_STAFF = ToolDefinition.create(TinkerTools.ichorStaff);
  public static final ToolDefinition ENDER_STAFF = ToolDefinition.create(TinkerTools.enderStaff);

  // ancient
  public static final ToolDefinition MELTING_PAN = ToolDefinition.create(TinkerTools.meltingPan);
  public static final ToolDefinition WAR_PICK = ToolDefinition.create(TinkerTools.warPick);
  public static final ToolDefinition BATTLESIGN = ToolDefinition.create(TinkerTools.battlesign);
}
