package slimeknights.tconstruct.library.tools.definition;

import com.google.gson.JsonSyntaxException;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

import java.util.List;
import java.util.Set;

/**
 * Base interface for tool stat providing logic
 */
public interface IToolStatProvider {
  /** Builds the stats from the given definition and materials */
  StatsNBT buildStats(ToolDefinition definition, MaterialNBT materials);

  /** Determines if thi stat provider contains parts */
  boolean isMultipart();

  /**
   * Checks if the loaded tool definition data is valid
   * @throws JsonSyntaxException if the data is invalid
   */
  default void validate(ToolDefinitionData data) {
    int size = data.getParts().size();
    // multipart tools need at least 1 part, non-multipart must have no parts
    if (isMultipart()) {
      if (size == 0) {
        throw new IllegalStateException("Must have at least one tool part for a multipart tool");
      }
    } else {
      if (size != 0) {
        throw new IllegalStateException("Cannot have parts for a specialized tool");
      }
    }
  }

  /** Gets the default data for this definition, used for missing or erroring stats. If called multiple times, should return the same instance each time */
  default ToolDefinitionData getDefaultData() {
    return ToolDefinitionData.EMPTY;
  }

  /**
   * Validates the requirements
   * @param name          Name of the stat type
   * @param requiredStat  Stat type that must be included for the tool
   * @param otherStats    Other valid stat types
   * @param data          Data instance to validate
   */
  static void validate(String name, MaterialStatsId requiredStat, Set<MaterialStatsId> otherStats, ToolDefinitionData data) {
    List<PartRequirement> requirements = data.getParts();
    if (requirements.isEmpty()) {
      throw new IllegalStateException("Must have at least one tool part for a " + name + " tool");
    }
    boolean foundHead = false;
    for (PartRequirement req : requirements) {
      MaterialStatsId statType = req.getStatType();
      if (statType.equals(requiredStat)) {
        foundHead = true;
      } else if (!otherStats.contains(statType)) {
        throw new IllegalStateException("Invalid " + name + " tool part type " + statType + ", only support: " + requiredStat
                                        + otherStats.stream().map(MaterialStatsId::toString).reduce("", (s1, s2) -> s1 + ", " + s2));
      }
    }
    if (!foundHead) {
      throw new IllegalStateException(name + " tool must use at least one " + requiredStat + " part");
    }
  }
}
