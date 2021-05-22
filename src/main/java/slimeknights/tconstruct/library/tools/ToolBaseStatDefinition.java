package slimeknights.tconstruct.library.tools;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import slimeknights.tconstruct.library.TinkerAPIException;

/**
 * This class defines the innate properties of a tool.
 * Everything before materials are factored in.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public final class ToolBaseStatDefinition {
  /* General */

  /** Durability modifier */
  private final float durabilityModifier;

  /** All heads after the first are divided by this number on repairs, and the first stats are duplicated this many times on stat build */
  private final int primaryHeadWeight;

  /** Number of upgrades new tools start with */
  private final int defaultUpgrades;
  /** Number of abilities new tools start with */
  private final int defaultAbilities;
  /** Number of trait slots for the tool forge the tool starts with */
  private final int defaultTraits;

  /** Extra reach to use to boost a tool's range */
  private final float reachBonus;

  /* Harvest */

  /**
   * Multiplier applied to the actual mining speed of the tool
   * Internally a hammer and pick have the same speed, but a hammer is 2/3 slower
   */
  private final float miningSpeedModifier;


  /* Weapon */

  /** Value to add to the base damage before multiplying */
  private final float damageBonus;

  /** Multiplier for damage from materials. Should be defined per tool. */
  private final float damageModifier;

  /**
   * Allows you set the base attack speed, can be changed by modifiers. Equivalent to the vanilla attack speed.
   * 4 is equal to any standard item. Value has to be greater than zero.
   */
  private final float attackSpeed;

  /**
   * Applies the extra tool stats to the tool like a modifier
   * @param builder  Tool stats builder
   */
  public void buildStats(ModifierStatsBuilder builder) {
    // general
    builder.multiplyDurability(durabilityModifier);
    builder.addReach(reachBonus);
    // harvest
    builder.multiplyMiningSpeed(miningSpeedModifier);
    // weapon
    // builder.addAttackDamage(damageBonus);  damage bonus added in ToolStatsBuilder
    builder.multiplyAttackDamage(damageModifier);
    builder.multiplyAttackSpeed(attackSpeed);
  }

  /** Tool stat builder */
  @Setter @Accessors(chain = true)
  public static class Builder {
    // general
    private float durabilityModifier = 1f;
    private float reachBonus = 0f;
    private int primaryHeadWeight = 1;
    private int defaultUpgrades = 3;
    private int defaultAbilities = 1;
    private int defaultTraits = 0;
    // harvest
    private float miningSpeedModifier = 1f;
    // weapon
    private float damageBonus = 0f;
    private float damageModifier = 1f;
    private float attackSpeed = 1f;

    /** Creates the tool stat definition */
    public ToolBaseStatDefinition build() {
      if (damageModifier < 0.001) {
        throw new TinkerAPIException("Trying to define a tool without damage modifier set. Damage modifier has to be defined per tool and should be greater than 0.001");
      }
      return new ToolBaseStatDefinition(
        durabilityModifier, primaryHeadWeight,
        defaultUpgrades, defaultAbilities, defaultTraits,
        reachBonus, miningSpeedModifier,
        damageBonus, damageModifier, attackSpeed
      );
    }
  }
}
