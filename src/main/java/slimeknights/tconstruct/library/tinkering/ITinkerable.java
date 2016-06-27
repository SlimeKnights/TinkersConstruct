package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * All classes implementing this interface represent a item that has tinkers data. Usually also used for modifiers to
 * access the data.
 */
public interface ITinkerable {

  /**
   * Returns an List of Strings, where each String represents an information about the tool. Used to display
   * Information about the item in the GUI
   */
  List<String> getInformation(ItemStack stack);

  /** The default tooltip for the item */
  void getTooltip(ItemStack stack, List<String> tooltips);

  /** Detailed info about the tool. Displayed when Shift is held */
  void getTooltipDetailed(ItemStack stack, List<String> tooltips);

  /** What the tool is made out of. Displayed whet Ctrl is held */
  void getTooltipComponents(ItemStack stack, List<String> tooltips);
}
