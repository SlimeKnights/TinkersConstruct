package slimeknights.tconstruct.library.tools.definition;

import com.google.gson.JsonSyntaxException;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

import java.util.List;

/**
 * Base interface for tool stat providing logic
 */
public interface IToolStatProvider {
  /** Builds the stats from the given definition and materials */
  StatsNBT buildStats(ToolDefinition definition, List<IMaterial> materials);

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

  /** Gets the default data for this definition, used for missing or erroring stats */
  default ToolDefinitionData getDefaultData() {
    return ToolDefinitionData.EMPTY;
  }
}
