package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IToolStationDisplay {

  /**
   * The "title" displayed in the GUI
   * @deprecated Use getLocalizedName for consistency
   */
  @Deprecated
  String getLocalizedToolName();

  /**
   * The "title" displayed in the GUI
   */
  default String getLocalizedName() {
    return getLocalizedToolName();
  }

  /**
   * Returns an List of Strings, where each String represents an information about the tool. Used to display
   * Information about the item in the GUI
   */
  List<String> getInformation(ItemStack stack);
}
