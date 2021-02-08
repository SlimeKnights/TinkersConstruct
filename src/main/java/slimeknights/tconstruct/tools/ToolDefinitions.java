package slimeknights.tconstruct.tools;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToolDefinitions {
  /*
  ,
    (durability, harvestLevel, attackDamage, miningSpeed, attackSpeed) ->
      new StatsNBT(durability, harvestLevel, attackDamage, miningSpeed, attackSpeed)
   */

  public static final ToolDefinition PICKAXE = new ToolDefinition(
    ToolBaseStatDefinitions.PICKAXE,
    requirements(TinkerToolParts.pickaxeHead, TinkerToolParts.toolRod, TinkerToolParts.smallBinding));

  public static final ToolDefinition HAMMER = new ToolDefinition(
    ToolBaseStatDefinitions.HAMMER,
    requirements(TinkerToolParts.hammerHead, TinkerToolParts.toughToolRod, TinkerToolParts.largePlate, TinkerToolParts.largePlate));

  public static final ToolDefinition SHOVEL = new ToolDefinition(
    ToolBaseStatDefinitions.SHOVEL,
    requirements(TinkerToolParts.shovelHead, TinkerToolParts.toolRod, TinkerToolParts.smallBinding));

  public static final ToolDefinition EXCAVATOR = new ToolDefinition(
    ToolBaseStatDefinitions.EXCAVATOR,
    requirements(TinkerToolParts.excavatorHead, TinkerToolParts.toughToolRod, TinkerToolParts.largePlate, TinkerToolParts.toughBinding));

  public static final ToolDefinition AXE = new ToolDefinition(
    ToolBaseStatDefinitions.AXE,
    requirements(TinkerToolParts.axeHead, TinkerToolParts.toolRod, TinkerToolParts.smallBinding));

  public static final ToolDefinition KAMA = new ToolDefinition(
    ToolBaseStatDefinitions.KAMA,
    requirements(TinkerToolParts.kamaHead, TinkerToolParts.toolRod, TinkerToolParts.smallBinding));

  public static final ToolDefinition BROADSWORD = new ToolDefinition(
    ToolBaseStatDefinitions.BROADSWORD,
    requirements(TinkerToolParts.swordBlade, TinkerToolParts.toolRod, TinkerToolParts.toolRod));

  /** Creates a requirements supplier from a list */
  private static Supplier<List<IToolPart>> requirements(List<Supplier<? extends IToolPart>> parts) {
    return () -> parts.stream().map(Supplier::get).collect(Collectors.toList());
  }

  /** Creates a requirements supplier from 3 parts */
  @SuppressWarnings("SameParameterValue")
  private static Supplier<List<IToolPart>> requirements(Supplier<? extends IToolPart> part1, Supplier<? extends IToolPart> part2, Supplier<? extends IToolPart> part3) {
    return requirements(Arrays.asList(part1, part2, part3));
  }

  /** Creates a requirements supplier from 4 parts */
  @SuppressWarnings("SameParameterValue")
  private static Supplier<List<IToolPart>> requirements(Supplier<? extends IToolPart> part1, Supplier<? extends IToolPart> part2, Supplier<? extends IToolPart> part3, Supplier<? extends IToolPart> part4) {
    return requirements(Arrays.asList(part1, part2, part3, part4));
  }
}
