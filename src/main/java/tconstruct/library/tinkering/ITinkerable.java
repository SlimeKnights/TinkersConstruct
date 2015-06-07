package tconstruct.library.tinkering;

import net.minecraft.item.ItemStack;

/**
 * All classes implementing this interface represent a item that has tinkers data.
 * Usually also used for modifiers to access the data.
 */
public interface ITinkerable {

  /**
   * Returns an String of arrays, where each String represents an information about the tool. Used to display
   * Information about the item in a tooltip or the GUI
   */
  String[] getInformation(ItemStack stack);
}
