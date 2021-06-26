package slimeknights.tconstruct.tools;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolDefinition;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToolDefinitions {
  // rock
  public static final ToolDefinition PICKAXE = new ToolDefinition(
    ToolBaseStatDefinitions.PICKAXE,
    requirements(TinkerToolParts.pickaxeHead, TinkerToolParts.toolHandle, TinkerToolParts.toolBinding),
    () -> ImmutableList.of(new ModifierEntry(TinkerModifiers.piercing.get(), 2)));
  public static final ToolDefinition SLEDGE_HAMMER = new ToolDefinition(
    ToolBaseStatDefinitions.SLEDGE_HAMMER,
    requirements(TinkerToolParts.hammerHead, TinkerToolParts.toughHandle, TinkerToolParts.largePlate, TinkerToolParts.largePlate),
    () -> ImmutableList.of(new ModifierEntry(TinkerModifiers.smite.get(), 2),
                           new ModifierEntry(TinkerModifiers.twoHanded.get(), 1)));
  public static final ToolDefinition VEIN_HAMMER = new ToolDefinition(
    ToolBaseStatDefinitions.VEIN_HAMMER,
    requirements(TinkerToolParts.hammerHead, TinkerToolParts.toughHandle, TinkerToolParts.pickaxeHead, TinkerToolParts.largePlate),
    () -> ImmutableList.of(new ModifierEntry(TinkerModifiers.twoHanded.get(), 1)));

  // dirt
  public static final ToolDefinition MATTOCK = new ToolDefinition(
    ToolBaseStatDefinitions.MATTOCK,
    requirements(TinkerToolParts.smallAxeHead, TinkerToolParts.toolHandle, TinkerToolParts.pickaxeHead),
    () -> ImmutableList.of(new ModifierEntry(TinkerModifiers.knockback.get(), 1),
                           new ModifierEntry(TinkerModifiers.shovelTransformHidden.get(), 1)));
  public static final ToolDefinition EXCAVATOR = new ToolDefinition(
    ToolBaseStatDefinitions.EXCAVATOR,
    requirements(TinkerToolParts.largePlate, TinkerToolParts.toughHandle, TinkerToolParts.largePlate, TinkerToolParts.toughHandle),
    () -> ImmutableList.of(new ModifierEntry(TinkerModifiers.knockback.get(), 2),
                           new ModifierEntry(TinkerModifiers.shovelTransformHidden.get(), 1),
                           new ModifierEntry(TinkerModifiers.twoHanded.get(), 1)));

  // wood
  public static final ToolDefinition HAND_AXE = new ToolDefinition(
    ToolBaseStatDefinitions.HAND_AXE,
    requirements(TinkerToolParts.smallAxeHead, TinkerToolParts.toolHandle, TinkerToolParts.toolBinding),
    () -> Collections.singletonList(new ModifierEntry(TinkerModifiers.axeTransformHidden.get(), 1)));
  public static final ToolDefinition BROAD_AXE = new ToolDefinition(
    ToolBaseStatDefinitions.BROAD_AXE,
    requirements(TinkerToolParts.broadAxeHead, TinkerToolParts.toughHandle, TinkerToolParts.pickaxeHead, TinkerToolParts.largePlate),
    () -> ImmutableList.of(new ModifierEntry(TinkerModifiers.axeTransformHidden.get(), 1),
                           new ModifierEntry(TinkerModifiers.twoHanded.get(), 1)));

  // scythes
  public static final ToolDefinition KAMA = new ToolDefinition(
    ToolBaseStatDefinitions.KAMA,
    requirements(TinkerToolParts.smallBlade, TinkerToolParts.toolHandle, TinkerToolParts.toolBinding),
    () -> ImmutableList.of(new ModifierEntry(TinkerModifiers.hoeTransformHidden.get(), 1),
                           new ModifierEntry(TinkerModifiers.shears.get(), 1),
                           new ModifierEntry(TinkerModifiers.harvest.get(), 1)));
  public static final ToolDefinition SCYTHE = new ToolDefinition(
    ToolBaseStatDefinitions.SCYTHE,
    requirements(TinkerToolParts.broadBlade, TinkerToolParts.toughHandle, TinkerToolParts.toolBinding, TinkerToolParts.toughHandle),
    () -> ImmutableList.of(new ModifierEntry(TinkerModifiers.hoeTransformHidden.get(), 1),
                           new ModifierEntry(TinkerModifiers.aoeSilkyShears.get(), 1),
                           new ModifierEntry(TinkerModifiers.harvest.get(), 1),
                           new ModifierEntry(TinkerModifiers.twoHanded.get(), 1)));

  // swords
  public static final ToolDefinition DAGGER = new ToolDefinition(
    ToolBaseStatDefinitions.DAGGER,
    requirements(Stream.of(TinkerToolParts.smallBlade, TinkerToolParts.toolHandle)),
    () -> ImmutableList.of(new ModifierEntry(TinkerModifiers.offhandAttack.get(), 1),
                           new ModifierEntry(TinkerModifiers.padded.get(), 1),
                           new ModifierEntry(TinkerModifiers.silkyShears.get(), 1)));
  public static final ToolDefinition SWORD = new ToolDefinition(
    ToolBaseStatDefinitions.SWORD,
    requirements(TinkerToolParts.smallBlade, TinkerToolParts.toolHandle, TinkerToolParts.toolHandle),
    () -> Collections.singletonList(new ModifierEntry(TinkerModifiers.silkyShears.get(), 1)));
  public static final ToolDefinition CLEAVER = new ToolDefinition(
    ToolBaseStatDefinitions.CLEAVER,
    requirements(TinkerToolParts.broadBlade, TinkerToolParts.toughHandle, TinkerToolParts.toughHandle, TinkerToolParts.largePlate),
    () -> ImmutableList.of(new ModifierEntry(TinkerModifiers.severing.get(), 2),
                           new ModifierEntry(TinkerModifiers.aoeSilkyShears.get(), 1),
                           new ModifierEntry(TinkerModifiers.twoHanded.get(), 1)));

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
}
