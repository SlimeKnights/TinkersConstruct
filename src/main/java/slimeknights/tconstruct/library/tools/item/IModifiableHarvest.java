package slimeknights.tconstruct.library.tools.item;

import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;

/**
 * Interface for an item that handles tool harvesting
 */
public interface IModifiableHarvest {
  /**
   * Gets the class handling the tool harvest logic for this block
   * @return  Tool harvest logic class
   */
  default ToolHarvestLogic getToolHarvestLogic() {
    return ToolHarvestLogic.DEFAULT;
  }
}
