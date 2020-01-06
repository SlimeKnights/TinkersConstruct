package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * All classes implementing this interface represent a item that has tinkers data. Usually also used for modifiers to
 * access the data.
 */
public interface ITinkerable {

  /** The default tooltip for the item */
  void getTooltip(ItemStack stack, List<String> tooltips);
}
