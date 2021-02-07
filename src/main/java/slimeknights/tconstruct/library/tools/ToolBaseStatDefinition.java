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
  /**
   * A fixed damage value where the calculations start to apply dimishing returns.
   * Basically if you'd hit more than that damage with this tool, the damage is gradually reduced depending on how much the cutoff is exceeded.
   * Helps keeping power creep in check.
   * The default is 15, in general this should be sufficient and only needs increasing if it's a stronger weapon.
   * A diamond sword with sharpness V has 15 damage
   */
  private final float damageCutoff;

  /** Knockback modifier. Basically this takes the vanilla knockback on hit and modifies it by this factor.
   * TODO: move to tool stats? This and reach */
  private final float knockbackModifier;

  /** Number of modifiers new tools start with */
  private final int defaultModifiers;

  /** Number of abilities new tools start with */
  private final int defaultAbilities;

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
    private float knockbackModifier = 1;
    /** See comment on {@link ToolBaseStatDefinition} */
    private int defaultModifiers = 3;
    /** See comment on {@link ToolBaseStatDefinition} */
    private int defaultAbilities = 1;

    /** Creates the tool stat definition */
    public ToolBaseStatDefinition build() {
      if (damageModifier < 0.001) {
        throw new TinkerAPIException("Trying to define a tool without damage modifier set. Damage modifier has to be defined per tool and should be greater than 0.001 and non-negative.");
      }
      // bake the multipliers into the stats factory
      IStatFactory factory = (durability, harvestLevel, attackDamage, miningSpeed, attackSpeed)
        -> new StatsNBT((int)(durability * durabilityModifier), harvestLevel, (attackDamage + damageBonus) * damageModifier,
                        miningSpeed * miningSpeedModifier, attackSpeed * this.attackSpeed);
      return new ToolBaseStatDefinition(damageCutoff, knockbackModifier, defaultModifiers, defaultAbilities, factory);
    }
  }
}
