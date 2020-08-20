package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.utils.TooltipType;

import java.util.List;

/**
 * All classes implementing this interface represent a item that has tinkers data. Usually also used for modifiers to
 * access the data.
 */
public interface ITinkerable {

  /**
   * The tooltip for the item
   *
   * Displays different information based on the tooltip type
   * If the SHIFT key is held, the detailed information is displayed
   * If CONTROL key is held, the materials the tool is made out of is displayed
   *
   * @param stack the given itemstack
   * @param tooltips the list of tooltips to add to
   * @param tooltipType the tooltip type to display
   */
  void getTooltip(ItemStack stack, List<ITextComponent> tooltips, TooltipType tooltipType);
}
