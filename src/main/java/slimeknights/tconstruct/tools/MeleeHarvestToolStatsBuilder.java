package slimeknights.tconstruct.tools;

import com.google.common.annotations.VisibleForTesting;
import lombok.AccessLevel;
import lombok.Getter;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.tools.stat.ToolStatsBuilder;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.List;

/**
 * Standard stat builder for melee and harvest tools. Calculates the five main stat types, and handles the bonuses for other types
 */
@Getter(AccessLevel.PROTECTED)
public final class MeleeHarvestToolStatsBuilder extends ToolStatsBuilder {
  private final List<HeadMaterialStats> heads;
  private final List<HandleMaterialStats> handles;
  private final List<ExtraMaterialStats> extras;

  @VisibleForTesting
  public MeleeHarvestToolStatsBuilder(ToolDefinitionData toolData, List<HeadMaterialStats> heads, List<HandleMaterialStats> handles, List<ExtraMaterialStats> extras) {
    super(toolData);
    this.heads = heads;
    this.handles = handles;
    this.extras = extras;
  }

  /** Creates a builder from the definition and materials */
  public static ToolStatsBuilder from(ToolDefinition toolDefinition, List<IMaterial> materials) {
    ToolDefinitionData data = toolDefinition.getData();
    List<PartRequirement> requiredComponents = data.getParts();
    // if the NBT is invalid, at least we can return the default stats builder, as an exception here could kill itemstacks
    if (materials.size() != requiredComponents.size()) {
      return ToolStatsBuilder.noParts(toolDefinition);
    }
    return new MeleeHarvestToolStatsBuilder(data,
                                            listOfCompatibleWith(HeadMaterialStats.ID, materials, requiredComponents),
                                            listOfCompatibleWith(HandleMaterialStats.ID, materials, requiredComponents),
                                            listOfCompatibleWith(ExtraMaterialStats.ID, materials, requiredComponents)
    );
  }

  @Override
  protected void setStats(StatsNBT.Builder builder) {
    // add in specific stat types handled by our materials
    builder.set(ToolStats.DURABILITY, buildDurability());
    builder.set(ToolStats.HARVEST_LEVEL, buildHarvestLevel());
    builder.set(ToolStats.ATTACK_DAMAGE, buildAttackDamage());
    builder.set(ToolStats.ATTACK_SPEED, buildAttackSpeed());
    builder.set(ToolStats.MINING_SPEED, buildMiningSpeed());
  }

  @Override
  protected boolean handles(IToolStat<?> stat) {
    return stat == ToolStats.DURABILITY || stat == ToolStats.HARVEST_LEVEL
           || stat == ToolStats.ATTACK_DAMAGE || stat == ToolStats.ATTACK_SPEED || stat == ToolStats.MINING_SPEED;
  }

  /** Builds durability for the tool */
  public float buildDurability() {
    double averageHeadDurability = getAverageValue(heads, HeadMaterialStats::getDurability) + toolData.getBonus(ToolStats.DURABILITY);
    double averageHandleModifier = getAverageValue(handles, HandleMaterialStats::getDurability, 1);
    // durability should never be below 1
    return Math.max(1, (int)(averageHeadDurability * averageHandleModifier));
  }

  /** Builds mining speed for the tool */
  public float buildMiningSpeed() {
    double averageHeadSpeed = getAverageValue(heads, HeadMaterialStats::getMiningSpeed) + toolData.getBonus(ToolStats.MINING_SPEED);
    double averageHandleModifier = getAverageValue(handles, HandleMaterialStats::getMiningSpeed, 1);

    return (float)Math.max(0.1d, averageHeadSpeed * averageHandleModifier);
  }

  /** Builds attack speed for the tool */
  public float buildAttackSpeed() {
    float baseSpeed = toolData.getBaseStat(ToolStats.ATTACK_SPEED);
    double averageHandleModifier = getAverageValue(handles, HandleMaterialStats::getAttackSpeed, 1);
    return (float)Math.max(0, baseSpeed * averageHandleModifier);
  }

  /** Builds the harvest level for the tool */
  public int buildHarvestLevel() {
    return heads.stream()
      .mapToInt(HeadMaterialStats::getHarvestLevel)
      .max()
      .orElse(0);
  }

  /** Builds attack damage for the tool */
  public float buildAttackDamage() {
    double averageHeadAttack = getAverageValue(heads, HeadMaterialStats::getAttack) + toolData.getBonus(ToolStats.ATTACK_DAMAGE);
    double averageHandle = getAverageValue(handles, HandleMaterialStats::getAttackDamage, 1.0f);
    return (float)Math.max(0.0d, averageHeadAttack * averageHandle);
  }
}
