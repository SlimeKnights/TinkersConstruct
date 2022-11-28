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
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;

import java.util.List;

/**
 * Standard stat builder for ranged tools. Includes some melee attributes for melee bows
 */
@Getter(AccessLevel.PROTECTED)
public class RangedToolStatsBuilder extends ToolStatsBuilder {
  private final List<LimbMaterialStats> limbs;
  private final List<BowstringMaterialStats> strings;

  @VisibleForTesting
  public RangedToolStatsBuilder(ToolDefinitionData toolData, List<LimbMaterialStats> limbs, List<BowstringMaterialStats> strings) {
    super(toolData);
    this.limbs = limbs;
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
                                      listOfCompatibleWith(BowstringMaterialStats.ID, materials, requiredComponents)
    );
  }

  @Override
  protected void setStats(StatsNBT.Builder builder) {
    // add in specific stat types handled by our materials
    builder.set(ToolStats.DURABILITY, buildDurability());
    builder.set(ToolStats.DRAW_SPEED, buildDrawSpeed());
    builder.set(ToolStats.VELOCITY, buildVelocity());
    builder.set(ToolStats.ATTACK_DAMAGE, buildAttackDamage());
  }

  @Override
  protected boolean handles(IToolStat<?> stat) {
    return stat == ToolStats.DURABILITY || stat == ToolStats.DRAW_SPEED
           || stat == ToolStats.VELOCITY || stat == ToolStats.ATTACK_DAMAGE;
  }

  /** Builds durability for the tool */
  public float buildDurability() {
    double averageLimbDurability = getAverageValue(limbs, LimbMaterialStats::getDurability) + getStatOrDefault(ToolStats.DURABILITY, 0f);
    // durability should never be below 1
    return Math.max(1, (int)(averageLimbDurability));
  }

  /** Builds attack speed for the tool */
  public float buildDrawSpeed() {
    double averageHandleModifier = getAverageValue(limbs, LimbMaterialStats::getDrawSpeed, 1);
    return (float)Math.max(0, getStatOrDefault(ToolStats.DRAW_SPEED, 1f) * averageHandleModifier);
  }

  /** Builds velocity for the tool */
  public float buildVelocity() {
    double averageHandleModifier = getAverageValue(limbs, LimbMaterialStats::getVelocity, 1);
    return (float)Math.max(0, getStatOrDefault(ToolStats.VELOCITY, 1f) * averageHandleModifier);
  }

  /** Builds attack damage for the tool */
  public float buildAttackDamage() {
    double averageLimbAttack = getAverageValue(limbs, LimbMaterialStats::getMeleeAttack) + getStatOrDefault(ToolStats.ATTACK_DAMAGE, 0f);
    return (float)Math.max(0.0d, averageLimbAttack);
  }
}
