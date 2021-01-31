package slimeknights.tconstruct.tools;

import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

/**
 * Builder to combine material stats with modifier stats. Used after building via {@link ToolStatsBuilder}
 */
@NoArgsConstructor(staticName = "builder")
public class ToolStatsModifierBuilder {
  /** If true, a change was made */
  private boolean dirty = false;

  /** New harvest level, largest is kept */
  private int harvestLevel = 0;

  /** Value added to final durability */
  private int addDurability = 0;
  /** Value multiplied after adding */
  private float multiplyDurability = 1;

  /** Value added to final attack damage */
  private float addAttackDamage = 0;
  /** Value multiplied after adding */
  private float multiplyAttackDamage = 1;

  /** Value added to final attack speed */
  private float addAttackSpeed = 0;
  /** Value multiplied after adding */
  private float multiplyAttackSpeed = 1;

  /** Value added to final mining speed */
  private float addMiningSpeed = 0;
  /** Value multiplied after adding */
  private float multiplyMiningSpeed = 1;

  /** Sets the harvest level, if the value is larger than the current */
  public ToolStatsModifierBuilder setHarvestLevel(int level) {
    if (level > harvestLevel) {
      harvestLevel = level;
      dirty = true;
    }
    return this;
  }

  /** Adds durability to the stats */
  public ToolStatsModifierBuilder addDurability(int amount) {
    addDurability += amount;
    dirty = true;
    return this;
  }

  /** Multiplies the final durability by the factor */
  public ToolStatsModifierBuilder multiplyDurability(float factor) {
    multiplyDurability *= factor;
    dirty = true;
    return this;
  }

  /** Adds attack damage to the stats */
  public ToolStatsModifierBuilder addAttackDamage(float amount) {
    addAttackDamage += amount;
    dirty = true;
    return this;
  }

  /** Multiplies the final attack damage by the factor */
  public ToolStatsModifierBuilder multiplyAttackDamage(float factor) {
    multiplyAttackDamage *= factor;
    dirty = true;
    return this;
  }

  /** Adds attack speed to the stats */
  public ToolStatsModifierBuilder addAttackSpeed(float amount) {
    addAttackSpeed += amount;
    dirty = true;
    return this;
  }

  /** Multiplies the final attack speed by the factor */
  public ToolStatsModifierBuilder multiplyAttackSpeed(float factor) {
    multiplyAttackSpeed *= factor;
    dirty = true;
    return this;
  }

  /** Adds mining speed to the stats */
  public ToolStatsModifierBuilder addMiningSpeed(float amount) {
    addMiningSpeed += amount;
    dirty = true;
    return this;
  }

  /** Multiplies the final mining speed by the factor */
  public ToolStatsModifierBuilder multiplyMiningSpeed(float factor) {
    multiplyMiningSpeed *= factor;
    dirty = true;
    return this;
  }

  /**
   * Builds the stats
   * @param base  Base stats
   * @return  Base stats with stats from this builder
   */
  public StatsNBT build(StatsNBT base) {
    if (!dirty) {
      return base;
    }
    return new StatsNBT(
      (int)((base.getDurability() + addDurability) * multiplyDurability),
      Math.max(base.getHarvestLevel(), harvestLevel),
      (base.getAttackDamage() + addAttackDamage) * multiplyAttackDamage,
      (base.getMiningSpeed()  +  addMiningSpeed) * multiplyMiningSpeed,
      (base.getAttackSpeed()  +  addAttackSpeed) * multiplyAttackSpeed
    );
  }
}
