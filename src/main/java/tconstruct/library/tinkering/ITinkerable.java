package tconstruct.library.tinkering;

import net.minecraft.item.ItemStack;

/**
 * All classes implementing this interface
 */
public interface ITinkerable {

  /**
   * Returns the tag key of the tag with the tinker data.
   */
  public String getTagName();

  /**
   * Returns a String that returns the category of the item. For example "tool", "armor" or
   * "accessory". Used to determine compatibility with modifiers etc.
   */
  public String getItemType();

  /**
   * Returns an String of arrays, where each String represents an information about the tool. Used
   * to display Information about the item in a tooltip or the GUI
   */
  public String[] getInformation(ItemStack stack);
}
