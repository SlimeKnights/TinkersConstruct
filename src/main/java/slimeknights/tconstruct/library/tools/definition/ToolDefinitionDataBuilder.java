package slimeknights.tconstruct.library.tools.definition;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ToolAction;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData.Harvest;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData.Stats;
import slimeknights.tconstruct.library.tools.definition.aoe.IAreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.definition.harvest.IHarvestLogic;
import slimeknights.tconstruct.library.tools.definition.harvest.TagHarvestLogic;
import slimeknights.tconstruct.library.tools.definition.module.IToolModule;
import slimeknights.tconstruct.library.tools.definition.weapon.IWeaponAttack;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Builder for a tool definition data
 */
@NoArgsConstructor(staticName = "builder")
@Accessors(fluent = true)
public class ToolDefinitionDataBuilder {
  private final ImmutableList.Builder<PartRequirement> parts = ImmutableList.builder();
  private final StatsNBT.Builder bonuses = StatsNBT.builder();
  private final MultiplierNBT.Builder multipliers = MultiplierNBT.builder();
  private final DefinitionModifierSlots.Builder slots = DefinitionModifierSlots.builder();
  private final ImmutableList.Builder<ModifierEntry> traits = ImmutableList.builder();
  private final ImmutableSet.Builder<ToolAction> actions = ImmutableSet.builder();
  private final ModifierHookMap.Builder hookBuilder = new ModifierHookMap.Builder();

  /** Tool's harvest logic */
  @Nonnull @Setter
  private IHarvestLogic harvestLogic = IHarvestLogic.DEFAULT;
  /** Tool's AOE logic */
  @Nonnull @Setter
  private IAreaOfEffectIterator aoe = IAreaOfEffectIterator.DEFAULT;
  /** Tool's attack logic */
  @Nonnull @Setter
  private IWeaponAttack attack = IWeaponAttack.DEFAULT;

  /* Parts */

  /**
   * Adds a part to the builder
   */
  public ToolDefinitionDataBuilder part(IToolPart part, int weight) {
    parts.add(PartRequirement.ofPart(part, weight));
    return this;
  }

  /**
   * Adds a stat requirement to the builder, for tools that don't have normal tool building recipes
   */
  public ToolDefinitionDataBuilder part(MaterialStatsId stat, int weight) {
    parts.add(PartRequirement.ofStat(stat, weight));
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
  public <T> ToolDefinitionDataBuilder stat(IToolStat<T> stat, T value) {
    bonuses.set(stat, value);
    return this;
  }

  /**
   * Adds a bonus to the builder, overload for floats as they come up pretty often, helps with boxing
   */
  public ToolDefinitionDataBuilder stat(IToolStat<Float> stat, float value) {
    bonuses.set(stat, value);
    return this;
  }

  /**
   * Applies a global multiplier
   */
  public ToolDefinitionDataBuilder multiplier(INumericToolStat<?> stat, float value) {
    multipliers.set(stat, value);
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
  public ToolDefinitionDataBuilder trait(ModifierId modifier, int level) {
    traits.add(new ModifierEntry(modifier, level));
    return this;
  }

  /**
   * Adds a base trait to the tool
   */
  public ToolDefinitionDataBuilder trait(LazyModifier modifier, int level) {
    return trait(modifier.getId(), level);
  }

  /**
   * Adds a base trait to the tool
   */
  public ToolDefinitionDataBuilder trait(ModifierId modifier) {
    return trait(modifier, 1);
  }

  /**
   * Adds a base trait to the tool
   */
  public ToolDefinitionDataBuilder trait(LazyModifier modifier) {
    return trait(modifier, 1);
  }

  /**
   * Adds a tool action to the tool definition, only has an affect on tools with interaction behaviors
   * @param action  Action
   */
  public ToolDefinitionDataBuilder action(ToolAction action) {
    this.actions.add(action);
    return this;
  }


  /* Harvest */

  /** Makes the tool effective on the given blocks */
  public ToolDefinitionDataBuilder effective(TagKey<Block> tag) {
    return harvestLogic(new TagHarvestLogic(tag));
  }


  /* Modules */

  /** Adds a module to the definition */
  public <T extends IToolModule> ToolDefinitionDataBuilder module(ModifierHook<? super T> hook, T module) {
    hookBuilder.addHook(module, hook);
    return this;
  }


  /**
   * Builds the final definition JSON to serialize
   */
  public ToolDefinitionData build() {
    List<PartRequirement> parts = this.parts.build();
    MultiplierNBT multipliers = this.multipliers.build();
    List<ModifierEntry> traits = this.traits.build();
    Set<ToolAction> actions = this.actions.build();
    // null harvest if empty
    Harvest harvest = null;
    boolean isDefaultHarvest = harvestLogic == IHarvestLogic.DEFAULT;
    boolean isDefaultAOE = aoe == IAreaOfEffectIterator.DEFAULT;
    if (!isDefaultAOE || !isDefaultHarvest) {
      // null properties if defaults
      harvest = new Harvest(isDefaultHarvest ? null : harvestLogic, isDefaultAOE ? null : aoe);
    }
    ModifierHookMap hooks = hookBuilder.build();
    if (hooks.getAllModules().isEmpty()) {
      hooks = null;
    }
    return new ToolDefinitionData(parts.isEmpty() ? null : parts,
                                  // null multipliers, traits, and actions if empty
                                  new Stats(bonuses.build(), multipliers == MultiplierNBT.EMPTY ? null : multipliers),
                                  slots.build(),
                                  traits.isEmpty() ? null : traits,
                                  actions.isEmpty() ? null : actions,
                                  harvest,
                                  attack == IWeaponAttack.DEFAULT ? null : attack,
                                  hooks);
  }
}
