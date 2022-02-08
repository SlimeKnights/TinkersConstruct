package slimeknights.tconstruct.tools.stats;

import com.google.common.annotations.VisibleForTesting;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.tools.stat.ToolStatsBuilder;

import java.util.List;

/**
 * Stat builder for slimeskull helmets
 */
public class SkullToolStatsBuilder extends ToolStatsBuilder {
  private final List<SkullStats> skulls;

  @VisibleForTesting
  protected SkullToolStatsBuilder(ToolDefinitionData toolData, List<SkullStats> skulls) {
    super(toolData);
    this.skulls = skulls;
  }

  /** Creates a builder from the definition and materials */
  public static ToolStatsBuilder from(ToolDefinition toolDefinition, MaterialNBT materials) {
    ToolDefinitionData data = toolDefinition.getData();
    List<PartRequirement> requiredComponents = data.getParts();
    // if the NBT is invalid, at least we can return the default stats builder, as an exception here could kill itemstacks
    if (materials.size() != requiredComponents.size()) {
      return ToolStatsBuilder.noParts(toolDefinition);
    }
    return new SkullToolStatsBuilder(data, listOfCompatibleWith(SkullStats.ID, materials, requiredComponents));
  }

  @Override
  protected void setStats(StatsNBT.Builder builder) {
    // add in specific stat types handled by our materials
    builder.set(ToolStats.DURABILITY, buildDurability());
    builder.set(ToolStats.ARMOR, buildArmor());
  }

  @Override
  protected boolean handles(IToolStat<?> stat) {
    return stat == ToolStats.DURABILITY || stat == ToolStats.ARMOR;
  }

  /** Builds durability for the tool */
  public float buildDurability() {
    return Math.max(1, (float)getAverageValue(skulls, SkullStats::getDurability) + getStatOrDefault(ToolStats.DURABILITY, 0f));
  }

  /** Builds armor for the tool */
  public float buildArmor() {
    return Math.max(0, (float)getAverageValue(skulls, SkullStats::getArmor) + getStatOrDefault(ToolStats.ARMOR, 0f));
  }
}
