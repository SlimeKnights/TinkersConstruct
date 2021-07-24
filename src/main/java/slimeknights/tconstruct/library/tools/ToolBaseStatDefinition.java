package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.Map;
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

  /** Number of upgrades new tools start with */
  @Getter
  private final int defaultUpgrades;
  /** Number of abilities new tools start with */
  @Getter
  private final int defaultAbilities;
  /** Number of trait slots for the tool forge the tool starts with */
  @Getter
  private final int defaultTraits;

  /** Bonuses to include as part of tool stat building */
  private final Map<FloatToolStat, Float> bonuses;

  /** Multipliers to include during modifier building */
  private final Map<FloatToolStat, Float> modifiers;

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

  /** Tool stat builder */
  @Setter @Accessors(chain = true)
  public static class Builder {
    // general
    private int primaryHeadWeight = 1;
    private int defaultUpgrades = 3;
    private int defaultAbilities = 1;
    private int defaultTraits = 0;
    // stats
    private final ImmutableMap.Builder<FloatToolStat,Float> bonuses = ImmutableMap.builder();
    private final ImmutableMap.Builder<FloatToolStat,Float> modifiers = ImmutableMap.builder();

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

    /** Creates the tool stat definition */
    public ToolBaseStatDefinition build() {
      return new ToolBaseStatDefinition(primaryHeadWeight, defaultUpgrades, defaultAbilities, defaultTraits, bonuses.build(), modifiers.build());
    }
  }
}
