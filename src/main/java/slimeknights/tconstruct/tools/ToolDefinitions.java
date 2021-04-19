package slimeknights.tconstruct.tools;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolDefinition;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ToolDefinitions {
  // rock
  public static final ToolDefinition PICKAXE = new ToolDefinition(
    ToolBaseStatDefinitions.PICKAXE,
    requirements(TinkerToolParts.pickaxeHead, TinkerToolParts.toolHandle, TinkerToolParts.toolBinding));
  public static final ToolDefinition SLEDGE_HAMMER = new ToolDefinition(
    ToolBaseStatDefinitions.SLEDGE_HAMMER,
    requirements(TinkerToolParts.hammerHead, TinkerToolParts.toughHandle, TinkerToolParts.largePlate, TinkerToolParts.largePlate));

  // dirt
  public static final ToolDefinition MATTOCK = new ToolDefinition(
    ToolBaseStatDefinitions.MATTOCK,
    requirements(TinkerToolParts.axeHead, TinkerToolParts.toolHandle, TinkerToolParts.kamaHead));
  public static final ToolDefinition EXCAVATOR = new ToolDefinition(
    ToolBaseStatDefinitions.EXCAVATOR,
    requirements(TinkerToolParts.largePlate, TinkerToolParts.toughHandle, TinkerToolParts.largePlate, TinkerToolParts.toughHandle));

  // wood
  public static final ToolDefinition AXE = new ToolDefinition(
    ToolBaseStatDefinitions.AXE,
    requirements(TinkerToolParts.axeHead, TinkerToolParts.toolHandle, TinkerToolParts.toolBinding));

  // scythes
  public static final ToolDefinition KAMA = new ToolDefinition(
    ToolBaseStatDefinitions.KAMA,
    requirements(TinkerToolParts.kamaHead, TinkerToolParts.toolHandle, TinkerToolParts.toolBinding));
  public static final ToolDefinition SCYTHE = new ToolDefinition(
    ToolBaseStatDefinitions.SCYTHE,
    requirements(TinkerToolParts.broadBlade, TinkerToolParts.toughHandle, TinkerToolParts.toolBinding, TinkerToolParts.toughHandle));

  public static final ToolDefinition BROADSWORD = new ToolDefinition(
    ToolBaseStatDefinitions.BROADSWORD,
    requirements(TinkerToolParts.swordBlade, TinkerToolParts.toolHandle, TinkerToolParts.toolHandle));
  public static final ToolDefinition CLEAVER = new ToolDefinition(
    ToolBaseStatDefinitions.CLEAVER,
    requirements(TinkerToolParts.broadBlade, TinkerToolParts.toughHandle, TinkerToolParts.toughHandle, TinkerToolParts.largePlate),
    () -> Collections.singletonList(new ModifierEntry(TinkerModifiers.beheading, 2)));

  /** Creates a requirements supplier from a list */
  private static Supplier<List<IToolPart>> requirements(Stream<Supplier<? extends IToolPart>> parts) {
    return () -> parts.map(Supplier::get).collect(Collectors.toList());
  }

  /** Creates a requirements supplier from 3 parts */
  @SuppressWarnings("SameParameterValue")
  private static Supplier<List<IToolPart>> requirements(Supplier<? extends IToolPart> part1, Supplier<? extends IToolPart> part2, Supplier<? extends IToolPart> part3) {
    return requirements(Stream.of(part1, part2, part3));
  }

  /** Creates a requirements supplier from 4 parts */
  @SuppressWarnings("SameParameterValue")
  private static Supplier<List<IToolPart>> requirements(Supplier<? extends IToolPart> part1, Supplier<? extends IToolPart> part2, Supplier<? extends IToolPart> part3, Supplier<? extends IToolPart> part4) {
    return requirements(Stream.of(part1, part2, part3, part4));
  }

  private ToolDefinitions() {
  }
}
