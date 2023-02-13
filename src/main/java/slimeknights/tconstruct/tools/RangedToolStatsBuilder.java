package slimeknights.tconstruct.tools;

import com.google.common.annotations.VisibleForTesting;
import lombok.AccessLevel;
import lombok.Getter;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.tools.stat.ToolStatsBuilder;
import slimeknights.tconstruct.tools.stats.BowstringMaterialStats;
import slimeknights.tconstruct.tools.stats.GripMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;

import java.util.List;

/**
 * Standard stat builder for ranged tools. Includes some melee attributes for melee bows
 */
@Getter(AccessLevel.PROTECTED)
public class RangedToolStatsBuilder extends ToolStatsBuilder {
  private final List<LimbMaterialStats> limbs;
  private final List<GripMaterialStats> grips;
  private final List<BowstringMaterialStats> strings;

  @VisibleForTesting
  public RangedToolStatsBuilder(ToolDefinitionData toolData, List<LimbMaterialStats> limbs, List<GripMaterialStats> grips, List<BowstringMaterialStats> strings) {
    super(toolData);
    this.limbs = limbs;
    this.grips = grips;
    this.strings = strings;
  }

  /** Creates a builder from the definition and materials */
  public static ToolStatsBuilder from(ToolDefinition toolDefinition, MaterialNBT materials) {
    ToolDefinitionData data = toolDefinition.getData();
    List<PartRequirement> requiredComponents = data.getParts();
    // if the NBT is invalid, at least we can return the default stats builder, as an exception here could kill itemstacks
    if (materials.size() != requiredComponents.size()) {
      return ToolStatsBuilder.noParts(toolDefinition);
    }
    return new RangedToolStatsBuilder(data,
                                      listOfCompatibleWith(LimbMaterialStats.ID, materials, requiredComponents),
                                      listOfCompatibleWith(GripMaterialStats.ID, materials, requiredComponents),
                                      listOfCompatibleWith(BowstringMaterialStats.ID, materials, requiredComponents)
    );
  }

  @Override
  protected void setStats(StatsNBT.Builder builder) {
    // add in specific stat types handled by our materials
    builder.set(ToolStats.DURABILITY, buildDurability());
    builder.set(ToolStats.DRAW_SPEED, buildDrawSpeed());
    builder.set(ToolStats.VELOCITY, buildVelocity());
    builder.set(ToolStats.ACCURACY, buildAccuracy());
    builder.set(ToolStats.ATTACK_DAMAGE, buildAttackDamage());
  }

  @Override
  protected boolean handles(IToolStat<?> stat) {
    return stat == ToolStats.DURABILITY || stat == ToolStats.ATTACK_DAMAGE
           || stat == ToolStats.DRAW_SPEED || stat == ToolStats.VELOCITY || stat == ToolStats.ACCURACY;
  }

  /** Builds durability for the tool */
  public float buildDurability() {
    double averageHeadDurability = getTotalValue(limbs, LimbMaterialStats::getDurability) + getStatOrDefault(ToolStats.DURABILITY, 0f);
    double averageHandleModifier = getAverageValue(grips, GripMaterialStats::getDurability, 1);
    // durability should never be below 1
    return Math.max(1, (int)(averageHeadDurability * averageHandleModifier));
  }

  /** Builds attack speed for the tool */
  public float buildDrawSpeed() {
    return (float)Math.max(0, getStatOrDefault(ToolStats.DRAW_SPEED, 1f) + getTotalValue(limbs, LimbMaterialStats::getDrawSpeed));
  }

  /** Builds velocity for the tool */
  public float buildVelocity() {
    return (float)Math.max(0, getStatOrDefault(ToolStats.VELOCITY, 1f) + getTotalValue(limbs, LimbMaterialStats::getVelocity));
  }

  /** Builds velocity for the tool */
  public float buildAccuracy() {
    return (float)Math.max(0, getStatOrDefault(ToolStats.ACCURACY, 0.75f) + getTotalValue(limbs, LimbMaterialStats::getAccuracy) + getTotalValue(grips, GripMaterialStats::getAccuracy));
  }

  /** Builds attack damage for the tool */
  public float buildAttackDamage() {
    return (float)Math.max(0.0d, getStatOrDefault(ToolStats.ATTACK_DAMAGE, 0f) + getAverageValue(grips, GripMaterialStats::getMeleeAttack));
  }
}
