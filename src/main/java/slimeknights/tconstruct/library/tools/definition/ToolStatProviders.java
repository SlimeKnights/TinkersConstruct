package slimeknights.tconstruct.library.tools.definition;

import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStatsBuilder;
import slimeknights.tconstruct.tools.MeleeHarvestToolStatsBuilder;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.List;

public class ToolStatProviders {
  /** For tools that have no parts, crafted directly in the crafting table */
  public static final IToolStatProvider NO_PARTS = new IToolStatProvider() {
    @Override
    public StatsNBT buildStats(ToolDefinition definition, List<IMaterial> materials) {
      return ToolStatsBuilder.noParts(definition).buildStats();
    }

    @Override
    public boolean isMultipart() {
      return false;
    }

    @Override
    public void validate(ToolDefinitionData data) {
      if (!data.getParts().isEmpty()) {
        throw new IllegalStateException("Cannot have parts for a specialized tool");
      }
    }
  };

  /** Tools with 1 or more tool parts using melee stats */
  public static final IToolStatProvider MELEE_HARVEST = new IToolStatProvider() {
    @Override
    public StatsNBT buildStats(ToolDefinition definition, List<IMaterial> materials) {
      return MeleeHarvestToolStatsBuilder.from(definition, materials).buildStats();
    }

    @Override
    public boolean isMultipart() {
      return true;
    }

    @Override
    public void validate(ToolDefinitionData data) {
      List<PartRequirement> requirements = data.getParts();
      if (requirements.isEmpty()) {
        throw new IllegalStateException("Must have at least one tool part for a melee/harvest tool");
      }
      boolean foundHead = false;
      for (PartRequirement req : requirements) {
        MaterialStatsId statType = req.getStatType();
        if (statType.equals(HeadMaterialStats.ID)) {
          foundHead = true;
        } else if (!statType.equals(HandleMaterialStats.ID) && !statType.equals(ExtraMaterialStats.ID)) {
          throw new IllegalStateException("Invalid melee/harvest tool part type, only support head, handle, and extra part types");
        }
      }
      if (!foundHead) {
        throw new IllegalStateException("Melee/harvest tool must use at least one head part");
      }
    }
  };
}
