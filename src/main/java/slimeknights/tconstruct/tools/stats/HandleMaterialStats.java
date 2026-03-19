package slimeknights.tconstruct.tools.stats;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.ArrayList;
import java.util.List;

import static slimeknights.tconstruct.library.materials.stats.IMaterialStats.makeTooltip;
import static slimeknights.tconstruct.library.materials.stats.IMaterialStats.makeTooltipKey;

/** Stats for melee harvest handles */
public record HandleMaterialStats(float durability, float miningSpeed, float meleeSpeed, float attackDamage) implements IMaterialStats.ScaledTooltip {
  public static final MaterialStatsId ID = new MaterialStatsId(TConstruct.getResource("handle"));
  public static final MaterialStatType<HandleMaterialStats> TYPE = new MaterialStatType<>(ID, new HandleMaterialStats(0f, 0f, 0f, 0f), RecordLoadable.create(
    FloatLoadable.ANY.defaultField("durability", 0f, true, HandleMaterialStats::durability),
    FloatLoadable.ANY.defaultField("mining_speed", 0f, true, HandleMaterialStats::miningSpeed),
    FloatLoadable.ANY.defaultField("melee_speed", 0f, true, HandleMaterialStats::meleeSpeed),
    FloatLoadable.ANY.defaultField("melee_damage", 0f, true, HandleMaterialStats::attackDamage),
    HandleMaterialStats::new));

  // tooltip prefixes
  private static final String DURABILITY_PREFIX = makeTooltipKey(TConstruct.getResource("durability"));
  private static final String ATTACK_DAMAGE_PREFIX = makeTooltipKey(TConstruct.getResource("attack_damage"));
  private static final String ATTACK_SPEED_PREFIX = makeTooltipKey(TConstruct.getResource("attack_speed"));
  private static final String MINING_SPEED_PREFIX = makeTooltipKey(TConstruct.getResource("mining_speed"));
  // tooltip descriptions
  private static final List<Component> DESCRIPTION = List.of(
    makeTooltip(TConstruct.getResource("handle.durability.description")),
    makeTooltip(TConstruct.getResource("handle.attack_damage.description")),
    makeTooltip(TConstruct.getResource("handle.attack_speed.description")),
    makeTooltip(TConstruct.getResource("handle.mining_speed.description")));

  // multipliers

  @Override
  public MaterialStatType<HandleMaterialStats> getType() {
    return TYPE;
  }

  @Override
  public List<Component> getLocalizedInfo(float scale) {
    List<Component> list = new ArrayList<>();
    list.add(formatDurability(this.durability * scale));
    list.add(formatAttackDamage(this.attackDamage));
    list.add(formatAttackSpeed(this.meleeSpeed));
    list.add(formatMiningSpeed(this.miningSpeed));
    return list;
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

  @Override
  public void apply(ModifierStatsBuilder builder, float scale) {
    ToolStats.DURABILITY.percent(builder, durability * scale);
    ToolStats.ATTACK_DAMAGE.percent(builder, attackDamage * scale);
    ToolStats.ATTACK_SPEED.percent(builder, meleeSpeed * scale);
    ToolStats.MINING_SPEED.percent(builder, miningSpeed * scale);
  }

  /** Applies formatting for durability */
  public static Component formatDurability(float quality) {
    return IToolStat.formatColoredPercentBoost(DURABILITY_PREFIX, quality);
  }

  /** Applies formatting for attack speed */
  public static Component formatAttackDamage(float quality) {
    return IToolStat.formatColoredPercentBoost(ATTACK_DAMAGE_PREFIX, quality);
  }

  /** Applies formatting for attack speed */
  public static Component formatAttackSpeed(float quality) {
    return IToolStat.formatColoredPercentBoost(ATTACK_SPEED_PREFIX, quality);
  }

  /** Applies formatting for mining speed */
  public static Component formatMiningSpeed(float quality) {
    return IToolStat.formatColoredPercentBoost(MINING_SPEED_PREFIX, quality);
  }


  /* Builder */

  /**
   * Creates a new builder instance using percent boost values (meaning 0.25 is 125%)
   */
  public static Builder percents() {
    return new Builder(false);
  }

  /**
   * Creates a new builder instance using multiplier values (meaning 0.25 is 25%).
   * Note using multiplier mode limits you to 1% increments, not that there is any use of less than 1%.
   */
  public static Builder multipliers() {
    return new Builder(true);
  }

  @Accessors(fluent = true)
  @Setter
  public static class Builder {
    private final boolean multiplier;
    private float durability;
    private float miningSpeed;
    private float attackSpeed;
    private float attackDamage;

    private Builder(boolean multiplier) {
      this.multiplier = multiplier;
      float defaultValue = multiplier ? 1 : 0;
      this.durability = defaultValue;
      this.miningSpeed = defaultValue;
      this.attackSpeed = defaultValue;
      this.attackDamage = defaultValue;
    }

    /** Converts a multiplier to a percent in a way that minimizes round off error */
    private static float percent(float multiplier) {
      // round to the nearest 100 to get rid of floating point weirdness
      return (Math.round(multiplier * 100) - 100) / 100f;
    }

    /** Builds the final stats */
    public HandleMaterialStats build() {
      if (multiplier) {
        return new HandleMaterialStats(percent(durability), percent(miningSpeed), percent(attackSpeed), percent(attackDamage));
      }
      return new HandleMaterialStats(durability, miningSpeed, attackSpeed, attackDamage);
    }
  }
}
