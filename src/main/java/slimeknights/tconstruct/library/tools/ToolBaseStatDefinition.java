package slimeknights.tconstruct.library.tools;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import slimeknights.tconstruct.library.TinkerAPIException;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.tools.ToolStatsBuilder.IStatFactory;

/**
 * This class defines the innate properties of a tool.
 * Everything before materials are factored in.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public final class ToolBaseStatDefinition {
  /** Durability modifier, stored separately to allow accessing separately
   * TODO: can we extract this from the factory instead? Maybe a method to get a single stat instead of all */
  private final float durabilityModifier;

  /**
   * A fixed damage value where the calculations start to apply dimishing returns.
   * Basically if you'd hit more than that damage with this tool, the damage is gradually reduced depending on how much the cutoff is exceeded.
   * Helps keeping power creep in check.
   * The default is 15, in general this should be sufficient and only needs increasing if it's a stronger weapon.
   * A diamond sword with sharpness V has 15 damage
   */
  private final float damageCutoff;

  /** Knockback bonus to apply when fully charged. 0.5 is the same as 1 level of vanilla knockback, or the bonus from sprinting
   * TODO: move to tool stats? This and reach */
  private final float knockbackBonus;

  /** Number of upgrades new tools start with */
  private final int defaultUpgrades;
  /** Number of abilities new tools start with */
  private final int defaultAbilities;
  /** Number of trait slots for the tool forge the tool starts with */
  private final int defaultTraits;

  /** All heads after the first are divided by this number on repairs, and the first stats are duplicated this many times on stat build */
  private final int primaryHeadWeight;

  /** Factory to create the tool stats from the material data, used for minor fine adjustments */
  private final IStatFactory statFactory;

  /** Tool stat builder */
  @Setter @Accessors(chain = true)
  public static class Builder {
    /** Multiplier applied to the durability of the tool. Used primarily for large tools */
    private float durabilityModifier = 1f;

    /**
     * Multiplier applied to the actual mining speed of the tool
     * Internally a hammer and pick have the same speed, but a hammer is 2/3 slower
     */
    private float miningSpeedModifier = 1f;

    /** Value to add to the base damage before multiplying */
    private float damageBonus = 0f;

    /** Multiplier for damage from materials. Should be defined per tool. */
    private float damageModifier = 1f;

    /** See comment on {@link ToolBaseStatDefinition} */
    private float damageCutoff = 15f;

    /**
     * Allows you set the base attack speed, can be changed by modifiers. Equivalent to the vanilla attack speed.
     * 4 is equal to any standard item. Value has to be greater than zero.
     */
    private float attackSpeed = 1f;

    /** See comment on {@link ToolBaseStatDefinition} */
    private int primaryHeadWeight = 1;

    /** See comment on {@link ToolBaseStatDefinition} */
    private float knockbackBonus = 0;
    /** See comment on {@link ToolBaseStatDefinition} */
    private int defaultUpgrades = 3;
    /** See comment on {@link ToolBaseStatDefinition} */
    private int defaultAbilities = 1;
    /** See comment on {@link ToolBaseStatDefinition} */
    private int defaultTraits = 0;

    /** Creates the tool stat definition */
    public ToolBaseStatDefinition build() {
      if (damageModifier < 0.001) {
        throw new TinkerAPIException("Trying to define a tool without damage modifier set. Damage modifier has to be defined per tool and should be greater than 0.001 and non-negative.");
      }
      // bake the multipliers into the stats factory
      // TODO: cleanup: should use a builder
      IStatFactory factory = (durability, harvestLevel, attackDamage, miningSpeed, attackSpeed)
        -> new StatsNBT((int)(durability * durabilityModifier), harvestLevel, (attackDamage + damageBonus) * damageModifier,
                        miningSpeed * miningSpeedModifier, attackSpeed * this.attackSpeed);
      return new ToolBaseStatDefinition(durabilityModifier, damageCutoff, knockbackBonus, defaultUpgrades, defaultAbilities, defaultTraits, primaryHeadWeight, factory);
    }
  }
}
