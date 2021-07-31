package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class defines the innate properties of a tool.
 * Everything before materials are factored in.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public final class ToolBaseStatDefinition {
  /* General */

  /** All heads after the first are divided by this number on repairs, and the first stats are duplicated this many times on stat build */
  @Getter
  private final int primaryHeadWeight;

  /** Map of all starting slot amounts */
  private final Map<SlotType,Integer> startingSlots;

  /** Bonuses to include as part of tool stat building */
  private final Map<FloatToolStat, Float> bonuses;

  /** Multipliers to include during modifier building */
  private final Map<FloatToolStat, Float> modifiers;

  /**
   * Gets the default number of slots for the given type
   * @param type  Type
   * @return  Number of starting slots on new tools
   */
  public int getStartingSlots(SlotType type) {
    return startingSlots.getOrDefault(type, 0);
  }

  /** Gets a set of bonuses applied to this tool, for stat building */
  public Set<FloatToolStat> getAllBonuses() {
    return bonuses.keySet();
  }

  /**
   * Gets the stat bonus for this tool, the tools stat builder is responsible for using these
   */
  public float getBonus(FloatToolStat stat) {
    return bonuses.getOrDefault(stat, 0f);
  }

  /**
   * Gets the stat multiplier for this tool, used by modifiers and during modifier application.
   *
   * In most cases, its better to use {@link slimeknights.tconstruct.library.tools.nbt.IModifierToolStack#getModifier(FloatToolStat)} as that takes the modifier multiplier into account
   */
  public float getModifier(FloatToolStat stat) {
    return modifiers.getOrDefault(stat, 1f);
  }

  /**
   * Applies the extra tool stats to the tool like a modifier
   * @param builder  Tool stats builder
   */
  public void buildStats(ModifierStatsBuilder builder) {
    modifiers.forEach((stat, value) -> stat.multiplyAll(builder, value));
  }

  /**
   * Adds the starting slots to the given mod data
   * @param persistentModData  Mod data
   */
  public void buildSlots(ModDataNBT persistentModData) {
    for (Entry<SlotType,Integer> entry : startingSlots.entrySet()) {
      persistentModData.setSlots(entry.getKey(), entry.getValue());
    }
  }


  /* Deprecated */

  /** @deprecated Use {@link #getStartingSlots(SlotType)} */
  @Deprecated
  public int getDefaultUpgrades() {
    return getStartingSlots(SlotType.UPGRADE);
  }

  /** @deprecated Use {@link #getStartingSlots(SlotType)} */
  @Deprecated
  public int getDefaultAbilities() {
    return getStartingSlots(SlotType.ABILITY);
  }

  /** @deprecated Use {@link #getStartingSlots(SlotType)} */
  @Deprecated
  public int getDefaultTraits() {
    return getStartingSlots(SlotType.TRAIT);
  }

  /** Tool stat builder */
  public static class Builder {
    private boolean setUpgrades = false;
    private boolean setAbilities = false;
    // general
    @Setter @Accessors(chain = true)
    private int primaryHeadWeight = 1;
    private final ImmutableMap.Builder<SlotType,Integer> startingSlots = ImmutableMap.builder();
    // stats
    private final ImmutableMap.Builder<FloatToolStat,Float> bonuses = ImmutableMap.builder();
    private final ImmutableMap.Builder<FloatToolStat,Float> modifiers = ImmutableMap.builder();

    /**
     * Sets the starting slot count for the given slot
     * @param type   Slot type
     * @param value  Value
     * @return  Builder
     */
    public Builder startingSlots(SlotType type, int value) {
      startingSlots.put(type, value);
      if (type == SlotType.UPGRADE) {
        setUpgrades = true;
      } else if (type == SlotType.ABILITY) {
        setAbilities = true;
      }
      return this;
    }

    /**
     * Adds a bonus to the builder, applied during tool stat creation
     * @param stat   Stat to apply
     * @param bonus  Bonus amount
     * @return  Builder
     */
    public Builder bonus(FloatToolStat stat, float bonus) {
      bonuses.put(stat, bonus);
      return this;
    }

    /**
     * Sets the stat to a particular value, replacing the default value.
     * Internally, sets the bonus to the passed value minus the default value, as the default will be added down the line
     * @param stat   Stat to apply
     * @param value  Value to set
     * @return  Builder
     */
    public Builder set(FloatToolStat stat, float value) {
      bonuses.put(stat, value - stat.getDefaultValue());
      return this;
    }

    /**
     * Adds a multiplier to the tool, applied during modifier stats
     * @param stat   Stat to apply
     * @param bonus  Multiplier
     * @return  Builder
     */
    public Builder modifier(FloatToolStat stat, float bonus) {
      modifiers.put(stat, bonus);
      return this;
    }


    /* Deprecated */

    /** @deprecated Use {@link #startingSlots(SlotType, int)} */
    @Deprecated
    public Builder setDefaultUpgrades(int value) {
      startingSlots(SlotType.UPGRADE, value);
      return this;
    }

    /** @deprecated Use {@link #startingSlots(SlotType, int)} */
    @Deprecated
    public Builder setDefaultAbilities(int value) {
      startingSlots(SlotType.ABILITY, value);
      return this;
    }

    /** @deprecated Use {@link #startingSlots(SlotType, int)} */
    @Deprecated
    public Builder setDefaultTraits(int value) {
      startingSlots(SlotType.TRAIT, value);
      return this;
    }

    /** Creates the tool stat definition */
    public ToolBaseStatDefinition build() {
      if (!setUpgrades) {
        startingSlots.put(SlotType.UPGRADE, 3);
      }
      if (!setAbilities) {
        startingSlots.put(SlotType.ABILITY, 1);
      }
      return new ToolBaseStatDefinition(primaryHeadWeight, startingSlots.build(), bonuses.build(), modifiers.build());
    }
  }
}
