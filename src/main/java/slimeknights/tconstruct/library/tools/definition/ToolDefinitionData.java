package slimeknights.tconstruct.library.tools.definition;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.ToolAction;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.aoe.IAreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.definition.harvest.IHarvestLogic;
import slimeknights.tconstruct.library.tools.definition.module.IToolModule;
import slimeknights.tconstruct.library.tools.definition.weapon.IWeaponAttack;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNullElse;

/**
 * This class contains all data pack configurable data for a tool, before materials are factored in.
 * Contains info about how to craft a tool and how it behaves.
 */
@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ToolDefinitionData {
  @VisibleForTesting
  protected static final Stats EMPTY_STATS = new Stats(StatsNBT.EMPTY, MultiplierNBT.EMPTY);
  /** Empty tool data definition instance */
  public static final ToolDefinitionData EMPTY = new ToolDefinitionData(Collections.emptyList(), EMPTY_STATS, DefinitionModifierSlots.EMPTY, Collections.emptyList(), Collections.emptySet(), null, null, ModifierHookMap.EMPTY);

  @Nullable
  private final List<PartRequirement> parts;
  @Nullable
  private final Stats stats;
  @Nullable
  private final DefinitionModifierSlots slots;
  @Nullable
  private final List<ModifierEntry> traits;
  @Nullable @VisibleForTesting
  protected final Set<ToolAction> actions;
  @Nullable
  private final Harvest harvest;
  @Nullable
  private final IWeaponAttack attack;
  @Nullable
  private final ModifierHookMap modules;


  /* Getters */

  /** Gets a list of all parts in the tool */
  public List<PartRequirement> getParts() {
    return requireNonNullElse(parts, Collections.emptyList());
  }

  /** Gets the stat sub object on the tool */
  protected Stats getStats() {
    return requireNonNullElse(stats, EMPTY_STATS);
  }

  /** Gets the starting slots on the tool */
  protected DefinitionModifierSlots getSlots() {
    return requireNonNullElse(slots, DefinitionModifierSlots.EMPTY);
  }

  /** Gets a list of all traits of the tool */
  public List<ModifierEntry> getTraits() {
    return requireNonNullElse(traits, Collections.emptyList());
  }

  /** Checks if the tool can natively perform the given tool action */
  public boolean canPerformAction(ToolAction action) {
    return this.actions != null && this.actions.contains(action);
  }

  /**
   * Gets the default number of slots for the given type
   * @param type  Type
   * @return  Number of starting slots on new tools
   */
  public int getStartingSlots(SlotType type) {
    return getSlots().getSlots(type);
  }

  /** Gets the map of internal module hooks */
  public ModifierHookMap getModules() {
    return requireNonNullElse(modules, ModifierHookMap.EMPTY);
  }

  /** Gets the given module from the tool */
  public <T> T getModule(ModifierHook<T> hook) {
    return getModules().getOrDefault(hook);
  }


  /* Stats */

  /** Gets a set of bonuses applied to this tool, for stat building */
  public Set<IToolStat<?>> getAllBaseStats() {
    return getStats().getBase().getContainedStats();
  }

  /** Determines if the given stat is defined in this definition, for stat building */
  public boolean hasBaseStat(IToolStat<?> stat) {
    return getStats().getBase().hasStat(stat);
  }

  /** Gets the value of a stat in this tool, or the default value if missing */
  public <T> T getBaseStat(IToolStat<T> toolStat) {
    return getStats().getBase().get(toolStat);
  }

  /**
   * Gets the multiplier for this stat to use for modifiers
   *
   * In most cases, its better to use {@link IToolStackView#getMultiplier(INumericToolStat)} as that takes the modifier multiplier into account
   */
  public float getMultiplier(INumericToolStat<?> toolStat) {
    return getStats().getMultipliers().get(toolStat);
  }


  /* Tool building */

  /**
   * Applies the extra tool stats to the tool like a modifier
   * @param builder  Tool stats builder
   */
  public void buildStatMultipliers(ModifierStatsBuilder builder) {
    if (stats != null) {
      MultiplierNBT multipliers = stats.getMultipliers();
      for (INumericToolStat<?> stat : multipliers.getContainedStats()) {
        stat.multiplyAll(builder, multipliers.get(stat));
      }
    }
  }

  /**
   * Adds the base slots to the given data. Called on tool rebuild, should not be called elsewhere.
   * @param volatileModData  Volatile mod data instance
   */
  public void buildSlots(ModDataNBT volatileModData) {
    if (slots != null) {
      for (SlotType type : slots.containedTypes()) {
        volatileModData.setSlots(type, slots.getSlots(type));
      }
    }
  }

  /**
   * Subtracts all the given slots from the data
   * @param persistentModData  Mod data
   * @deprecated will be removed in 1.19
   */
  @Deprecated
  public void migrateLegacySlots(ModDataNBT persistentModData) {
    if (slots != null) {
      for (SlotType type : slots.containedTypes()) {
        persistentModData.addSlots(type, -slots.getSlots(type));
      }
    }
  }


  /* Harvest */

  // TODO: migrate harvest into modules

  /** Gets the tools's harvest logic */
  public IHarvestLogic getHarvestLogic() {
    if (harvest != null && harvest.logic != null) {
      return harvest.logic;
    }
    return IHarvestLogic.DEFAULT;
  }

  /** Gets the AOE logic for this tool */
  public IAreaOfEffectIterator getAOE() {
    if (harvest != null && harvest.aoe != null) {
      return harvest.aoe;
    }
    return IAreaOfEffectIterator.DEFAULT;
  }


  /* Attack */

  // TODO: migrate attack into modules

  /** Gets the tool's attack logic */
  public IWeaponAttack getAttack() {
    return requireNonNullElse(attack, IWeaponAttack.DEFAULT);
  }


  /* Packet buffers */

  /** Writes a tool definition stat object to a packet buffer */
  public void write(FriendlyByteBuf buffer) {
    List<PartRequirement> parts = getParts();
    buffer.writeVarInt(parts.size());
    for (PartRequirement part : parts) {
      part.write(buffer);
    }
    Stats stats = getStats();
    stats.getBase().toNetwork(buffer);
    stats.getMultipliers().toNetwork(buffer);
    getSlots().write(buffer);
    List<ModifierEntry> traits = getTraits();
    buffer.writeVarInt(traits.size());
    for (ModifierEntry entry : traits) {
      entry.write(buffer);
    }
    if (actions == null) {
      buffer.writeVarInt(0);
    } else {
      buffer.writeVarInt(actions.size());
      for (ToolAction action : actions) {
        buffer.writeUtf(action.name());
      }
    }
    IHarvestLogic.LOADER.toNetwork(getHarvestLogic(), buffer);
    IAreaOfEffectIterator.LOADER.toNetwork(getAOE(), buffer);
    IWeaponAttack.LOADER.toNetwork(getAttack(), buffer);
    IToolModule.write(getModules(), buffer);
  }

  /** Reads a tool definition stat object from a packet buffer */
  public static ToolDefinitionData read(FriendlyByteBuf buffer) {
    int size = buffer.readVarInt();
    ImmutableList.Builder<PartRequirement> parts = ImmutableList.builder();
    for (int i = 0; i < size; i++) {
      parts.add(PartRequirement.read(buffer));
    }
    StatsNBT bonuses = StatsNBT.fromNetwork(buffer);
    MultiplierNBT multipliers = MultiplierNBT.fromNetwork(buffer);
    DefinitionModifierSlots slots = DefinitionModifierSlots.read(buffer);
    size = buffer.readVarInt();
    ImmutableList.Builder<ModifierEntry> traits = ImmutableList.builder();
    for (int i = 0; i < size; i++) {
      traits.add(ModifierEntry.read(buffer));
    }
    size = buffer.readVarInt();
    ImmutableSet.Builder<ToolAction> actions = ImmutableSet.builder();
    for (int i = 0; i < size; i++) {
      actions.add(ToolAction.get(buffer.readUtf()));
    }
    IHarvestLogic harvestLogic = IHarvestLogic.LOADER.fromNetwork(buffer);
    IAreaOfEffectIterator aoe = IAreaOfEffectIterator.LOADER.fromNetwork(buffer);
    IWeaponAttack attack = IWeaponAttack.LOADER.fromNetwork(buffer);
    ModifierHookMap modules = IToolModule.read(buffer);
    return new ToolDefinitionData(parts.build(), new Stats(bonuses, multipliers), slots, traits.build(), actions.build(), new Harvest(harvestLogic, aoe), attack, modules);
  }

  /** Internal stats object */
  @RequiredArgsConstructor
  public static class Stats {
    @Nullable
    private final StatsNBT base;
    @Nullable
    private final MultiplierNBT multipliers;

    /** Bonus to add to each stat on top of the base value */
    public StatsNBT getBase() {
      return requireNonNullElse(base, StatsNBT.EMPTY);
    }

    /** Multipliers to apply after modifiers */
    public MultiplierNBT getMultipliers() {
      return requireNonNullElse(multipliers, MultiplierNBT.EMPTY);
    }
  }

  /** Defines harvest properties */
  @Data
  protected static class Harvest {
    @Nullable
    private final IHarvestLogic logic;
    @Nullable
    private final IAreaOfEffectIterator aoe;
  }
}
