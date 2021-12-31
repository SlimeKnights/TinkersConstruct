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
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.aoe.IAreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.definition.harvest.IHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
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
  protected static final Stats EMPTY_STATS = new Stats(DefinitionToolStats.EMPTY, DefinitionToolStats.EMPTY);
  /** Empty tool data definition instance */
  public static final ToolDefinitionData EMPTY = new ToolDefinitionData(Collections.emptyList(), EMPTY_STATS, DefinitionModifierSlots.EMPTY, Collections.emptyList(), Collections.emptySet(), null);

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


  /* Stats */

  /** Gets a set of bonuses applied to this tool, for stat building */
  public Set<FloatToolStat> getAllBaseStats() {
    return getStats().getBase().containedStats();
  }

  /** Gets the value of a stat in this tool, or the default value if missing */
  public float getBaseStat(FloatToolStat toolStat) {
    return getStats().getBase().getStat(toolStat, toolStat.getDefaultValue());
  }

  /** Gets the value of a stat in this tool, or 0 if missing */
  public float getBonus(FloatToolStat toolStat) {
    return getStats().getBase().getStat(toolStat, 0f);
  }

  /**
   * Gets the multiplier for this stat to use for modifiers
   *
   * In most cases, its better to use {@link slimeknights.tconstruct.library.tools.nbt.IModifierToolStack#getModifier(FloatToolStat)} as that takes the modifier multiplier into account
   */
  public float getMultiplier(FloatToolStat toolStat) {
    return getStats().getMultipliers().getStat(toolStat, 1f);
  }


  /* Tool building */

  /**
   * Applies the extra tool stats to the tool like a modifier
   * @param builder  Tool stats builder
   */
  public void buildStatMultipliers(ModifierStatsBuilder builder) {
    if (stats != null) {
      DefinitionToolStats multipliers = stats.getMultipliers();
      for (FloatToolStat stat : multipliers.containedStats()) {
        stat.multiplyAll(builder, multipliers.getStat(stat, 1f));
      }
    }
  }

  /**
   * Adds the starting slots to the given mod data
   * @param persistentModData  Mod data
   */
  public void buildSlots(ModDataNBT persistentModData) {
    if (slots != null) {
      for (SlotType type : slots.containedTypes()) {
        persistentModData.setSlots(type, slots.getSlots(type));
      }
    }
  }


  /* Harvest */

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


  /* Packet buffers */

  /** Writes a tool definition stat object to a packet buffer */
  public void write(FriendlyByteBuf buffer) {
    List<PartRequirement> parts = getParts();
    buffer.writeVarInt(parts.size());
    for (PartRequirement part : parts) {
      part.write(buffer);
    }
    Stats stats = getStats();
    stats.getBase().write(buffer);
    stats.getMultipliers().write(buffer);
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
  }

  /** Reads a tool definition stat object from a packet buffer */
  public static ToolDefinitionData read(FriendlyByteBuf buffer) {
    int size = buffer.readVarInt();
    ImmutableList.Builder<PartRequirement> parts = ImmutableList.builder();
    for (int i = 0; i < size; i++) {
      parts.add(PartRequirement.read(buffer));
    }
    DefinitionToolStats bonuses = DefinitionToolStats.read(buffer);
    DefinitionToolStats multipliers = DefinitionToolStats.read(buffer);
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
    return new ToolDefinitionData(parts.build(), new Stats(bonuses, multipliers), slots, traits.build(), actions.build(), new Harvest(harvestLogic, aoe));
  }

  /** Internal stats object */
  @RequiredArgsConstructor
  public static class Stats {
    @Nullable
    private final DefinitionToolStats base;
    @Nullable
    private final DefinitionToolStats multipliers;

    /** Bonus to add to each stat on top of the base value */
    public DefinitionToolStats getBase() {
      return requireNonNullElse(base, DefinitionToolStats.EMPTY);
    }

    /** Multipliers to apply after modifiers */
    public DefinitionToolStats getMultipliers() {
      return requireNonNullElse(multipliers, DefinitionToolStats.EMPTY);
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
