package slimeknights.tconstruct.library.tools;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import slimeknights.tconstruct.library.TinkerAPIException;
import slimeknights.tconstruct.tools.ToolStatsBuilder.IStatFactory;

/**
 * This class defines the innate properties of a tool.
 * Everything before materials are factored in.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public final class ToolBaseStatDefinition {

  /**
   * Multiplier applied to the actual mining speed of the tool
   * Internally a hammer and pick have the same speed, but a hammer is 2/3 slower
   */
  private final float miningSpeedModifier;

  /**
   * Multiplier for damage from materials. Should be defined per tool.
   */
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
  private final double attackSpeed;

  /** Knockback modifier. Basically this takes the vanilla knockback on hit and modifies it by this factor. */
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
    private float miningSpeedModifer = 1;
    private float damageModifier = 0;
    private float damageCutoff = 15;
    private double attackSpeed = 4;
    private float knockbackModifier = 1;
    private int defaultModifiers = 3;
    private int defaultAbilities = 1;
    private IStatFactory statFactory = IStatFactory.DEFAULT;

    /** Creates the tool stat definition */
    public ToolBaseStatDefinition build() {
      if (damageModifier < 0.001) {
        throw new TinkerAPIException("Trying to define a tool without damage modifier set. Damage modifier has to be defined per tool and should be greater than 0.001 and non-negative.");
      }
      return new ToolBaseStatDefinition(miningSpeedModifer, damageModifier, damageCutoff, attackSpeed, knockbackModifier, defaultModifiers, defaultAbilities, statFactory);
    }
  }
}
