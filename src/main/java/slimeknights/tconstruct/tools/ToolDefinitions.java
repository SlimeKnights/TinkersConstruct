package slimeknights.tconstruct.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public final class ToolDefinitions {

  public static final ToolDefinition PICKAXE = new ToolDefinition(
    ToolBaseStatDefinitions.PICKAXE,
    ImmutableList.of(
      new PartMaterialRequirement(TinkerToolParts.pickaxeHead, HeadMaterialStats.ID),
      new PartMaterialRequirement(TinkerToolParts.toolRod, HandleMaterialStats.ID),
      new PartMaterialRequirement(TinkerToolParts.smallBinding, ExtraMaterialStats.ID)
    ),
    ImmutableSet.of(Category.HARVEST));

  public static final ToolDefinition HAMMER = new ToolDefinition(
    ToolBaseStatDefinitions.HAMMER,
    ImmutableList.of(
      new PartMaterialRequirement(TinkerToolParts.hammerHead, HeadMaterialStats.ID),
      new PartMaterialRequirement(TinkerToolParts.toughToolRod, HandleMaterialStats.ID),
      new PartMaterialRequirement(TinkerToolParts.largePlate, HeadMaterialStats.ID),
      new PartMaterialRequirement(TinkerToolParts.largePlate, HeadMaterialStats.ID)
    ),
    ImmutableSet.of(Category.HARVEST, Category.WEAPON, Category.AOE));

  public static final ToolDefinition SHOVEL = new ToolDefinition(
    ToolBaseStatDefinitions.SHOVEL,
    ImmutableList.of(
      new PartMaterialRequirement(TinkerToolParts.shovelHead, HeadMaterialStats.ID),
      new PartMaterialRequirement(TinkerToolParts.toolRod, HandleMaterialStats.ID),
      new PartMaterialRequirement(TinkerToolParts.smallBinding, ExtraMaterialStats.ID)
    ),
    ImmutableSet.of(Category.HARVEST));

  public static final ToolDefinition AXE = new ToolDefinition(
    ToolBaseStatDefinitions.AXE,
    ImmutableList.of(
      new PartMaterialRequirement(TinkerToolParts.axeHead, HeadMaterialStats.ID),
      new PartMaterialRequirement(TinkerToolParts.toolRod, HandleMaterialStats.ID),
      new PartMaterialRequirement(TinkerToolParts.smallBinding, ExtraMaterialStats.ID)
    ),
    ImmutableSet.of(Category.HARVEST, Category.WEAPON));

  public static final ToolDefinition KAMA = new ToolDefinition(
    ToolBaseStatDefinitions.KAMA,
    ImmutableList.of(
      new PartMaterialRequirement(TinkerToolParts.kamaHead, HeadMaterialStats.ID),
      new PartMaterialRequirement(TinkerToolParts.toolRod, HandleMaterialStats.ID),
      new PartMaterialRequirement(TinkerToolParts.smallBinding, ExtraMaterialStats.ID)
    ),
    ImmutableSet.of(Category.HARVEST, Category.WEAPON));


  public static final ToolDefinition BROADSWORD = new ToolDefinition(
    ToolBaseStatDefinitions.BROADSWORD,
    ImmutableList.of(
      new PartMaterialRequirement(TinkerToolParts.swordBlade, HeadMaterialStats.ID),
      new PartMaterialRequirement(TinkerToolParts.toolRod, HandleMaterialStats.ID),
      new PartMaterialRequirement(TinkerToolParts.wideGuard, ExtraMaterialStats.ID)
    ),
    ImmutableSet.of(Category.WEAPON));

  private ToolDefinitions() {
  }
}
