package slimeknights.tconstruct.library.tools.definition;

import com.google.common.collect.ImmutableList;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData.Stats;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;

import java.util.List;
import java.util.function.Supplier;

/**
 * Builder for a tool definition data
 */
@NoArgsConstructor(staticName = "builder")
public class ToolDefinitionDataBuilder {
  private final ImmutableList.Builder<PartRequirement> parts = ImmutableList.builder();
  private final DefinitionToolStats.Builder bonuses = DefinitionToolStats.builder();
  private final DefinitionToolStats.Builder multipliers = DefinitionToolStats.builder();
  private final DefinitionModifierSlots.Builder slots = DefinitionModifierSlots.builder();
  private final ImmutableList.Builder<ModifierEntry> traits = ImmutableList.builder();

  /* Parts */

  /**
   * Adds a part to the builder
   */
  public ToolDefinitionDataBuilder part(IToolPart part, int weight) {
    parts.add(new PartRequirement(part, weight));
    return this;
  }

  /**
   * Adds a part to the builder
   */
  public ToolDefinitionDataBuilder part(Supplier<? extends IToolPart> part, int weight) {
    return part(part.get(), weight);
  }

  /**
   * Adds a part to the builder
   */
  public ToolDefinitionDataBuilder part(IToolPart part) {
    return part(part, 1);
  }

  /**
   * Adds a part to the builder
   */
  public ToolDefinitionDataBuilder part(Supplier<? extends IToolPart> part) {
    return part(part, 1);
  }


  /* Stats */

  /**
   * Adds a bonus to the builder
   */
  public ToolDefinitionDataBuilder stat(FloatToolStat stat, float value) {
    bonuses.addStat(stat, value);
    return this;
  }

  /**
   * Applies a global multiplier
   */
  public ToolDefinitionDataBuilder multiplier(FloatToolStat stat, float value) {
    multipliers.addStat(stat, value);
    return this;
  }

  /**
   * Sets the starting slots for the given type, unspecified defaults to 0
   */
  public ToolDefinitionDataBuilder startingSlots(SlotType slotType, int value) {
    slots.setSlots(slotType, value);
    return this;
  }

  /**
   * Sets the starting slots to default
   */
  public ToolDefinitionDataBuilder smallToolStartingSlots() {
    startingSlots(SlotType.UPGRADE, 3);
    startingSlots(SlotType.ABILITY, 1);
    return this;
  }

  /**
   * Sets the starting slots to default
   */
  public ToolDefinitionDataBuilder largeToolStartingSlots() {
    startingSlots(SlotType.UPGRADE, 2);
    startingSlots(SlotType.ABILITY, 1);
    return this;
  }


  /* Traits */

  /**
   * Adds a base trait to the tool
   */
  public ToolDefinitionDataBuilder trait(Modifier modifier, int level) {
    traits.add(new ModifierEntry(modifier, level));
    return this;
  }

  /**
   * Adds a base trait to the tool
   */
  public ToolDefinitionDataBuilder trait(Supplier<? extends Modifier> modifier, int level) {
    return trait(modifier.get(), level);
  }

  /**
   * Adds a base trait to the tool
   */
  public ToolDefinitionDataBuilder trait(Modifier modifier) {
    return trait(modifier, 1);
  }

  /**
   * Adds a base trait to the tool
   */
  public ToolDefinitionDataBuilder trait(Supplier<? extends Modifier> modifier) {
    return trait(modifier, 1);
  }

  /**
   * Builds the final definition JSON to serialize
   */
  public ToolDefinitionData build() {
    List<PartRequirement> parts = this.parts.build();
    DefinitionToolStats multipliers = this.multipliers.build();
    List<ModifierEntry> traits = this.traits.build();
    return new ToolDefinitionData(parts.isEmpty() ? null : parts,
                                  new Stats(bonuses.build(),
                                            multipliers.containedStats().isEmpty() ? null : multipliers),
                                  slots.build(),
                                  traits.isEmpty() ? null : traits);
  }
}
