package slimeknights.tconstruct.library.tools.definition;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.PacketBuffer;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static slimeknights.mantle.util.LogicHelper.defaultIfNull;

/**
 * This class contains all data pack configurable data for a tool, before materials are factored in.
 * Contains info about how to craft a tool and how it behaves.
 */
@RequiredArgsConstructor
public class ToolDefinitionData {
  @VisibleForTesting
  protected static final Stats EMPTY_STATS = new Stats(DefinitionToolStats.EMPTY, DefinitionToolStats.EMPTY);
  /** Empty tool data definition instance */
  public static final ToolDefinitionData EMPTY = new ToolDefinitionData(Collections.emptyList(), EMPTY_STATS, DefinitionModifierSlots.EMPTY, Collections.emptyList());

  @Nullable
  private final List<PartRequirement> parts;
  @Nullable
  private final Stats stats;
  @Nullable
  private final DefinitionModifierSlots slots;
  @Nullable
  private final List<ModifierEntry> traits;


  /* Getters */

  /** Gets a list of all parts in the tool */
  public List<PartRequirement> getParts() {
    return defaultIfNull(parts, Collections.emptyList());
  }

  /** Gets the stat sub object on the tool */
  protected Stats getStats() {
    return defaultIfNull(stats, EMPTY_STATS);
  }

  /** Gets the starting slots on the tool */
  protected DefinitionModifierSlots getSlots() {
    return defaultIfNull(slots, DefinitionModifierSlots.EMPTY);
  }

  /** Gets a list of all traits of the tool */
  public List<ModifierEntry> getTraits() {
    return defaultIfNull(traits, Collections.emptyList());
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


  /* Packet buffers */

  /** Writes a tool definition stat object to a packet buffer */
  public void write(PacketBuffer buffer) {
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
  }

  /** Reads a tool definition stat object from a packet buffer */
  public static ToolDefinitionData read(PacketBuffer buffer) {
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
    return new ToolDefinitionData(parts.build(), new Stats(bonuses, multipliers), slots, traits.build());
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
      return defaultIfNull(base, DefinitionToolStats.EMPTY);
    }

    /** Multipliers to apply after modifiers */
    public DefinitionToolStats getMultipliers() {
      return defaultIfNull(multipliers, DefinitionToolStats.EMPTY);
    }
  }
}
