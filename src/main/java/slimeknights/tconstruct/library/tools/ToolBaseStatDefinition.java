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
   * A fixed damage value where the calculations start to apply dimishing returns.
   * Basically if you'd hit more than that damage with this tool, the damage is gradually reduced depending on how much the cutoff is exceeded.
   * Helps keeping power creep in check.
   * The default is 15, in general this should be sufficient and only needs increasing if it's a stronger weapon.
   * A diamond sword with sharpness V has 15 damage
   */
  private final float damageCutoff;

  /**
   * Allows you set the base attack speed, can be changed by modifiers. Equivalent to the vanilla attack speed.
   * 4 is equal to any standard item. Value has to be greater than zero.
   */
  private final float attackSpeed;

  /** Knockback bonus to apply when fully charged. 0.5 is the same as 1 level of vanilla knockback, or the bonus from sprinting */
  private final float knockbackBonus;

  /**
   * Applies the extra tool stats to the tool like a modifier
   * @param builder  Tool stats builder
   */
  public void buildStats(ModifierStatsBuilder builder) {
    // general
    builder.multiplyDurability(durabilityModifier);
    // harvest
    builder.multiplyMiningSpeed(miningSpeedModifier);
    // weapon
    builder.addAttackDamage(damageBonus);
    builder.multiplyAttackDamage(damageModifier);
    builder.multiplyAttackSpeed(attackSpeed);
  }

  /** Tool stat builder */
  @Setter @Accessors(chain = true)
  public static class Builder {
    // general
    private float durabilityModifier = 1f;
    private int primaryHeadWeight = 1;
    private int defaultUpgrades = 3;
    private int defaultAbilities = 1;
    private int defaultTraits = 0;
    // harvest
    private float miningSpeedModifier = 1f;
    // weapon
    private float damageBonus = 0f;
    private float damageModifier = 1f;
    private float damageCutoff = 15f;
    private float attackSpeed = 1f;
    private float knockbackBonus = 0;

    /** Creates the tool stat definition */
    public ToolBaseStatDefinition build() {
      if (damageModifier < 0.001) {
        throw new TinkerAPIException("Trying to define a tool without damage modifier set. Damage modifier has to be defined per tool and should be greater than 0.001");
      }
      return new ToolBaseStatDefinition(
        durabilityModifier, primaryHeadWeight, defaultUpgrades, defaultAbilities, defaultTraits,
        miningSpeedModifier,
        damageBonus, damageModifier, damageCutoff, attackSpeed, knockbackBonus
      );
    }
  }
}
