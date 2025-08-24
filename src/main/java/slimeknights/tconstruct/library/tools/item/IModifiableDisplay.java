package slimeknights.tconstruct.library.tools.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Interface for tools to display in books and other similar contexts
 */
public interface IModifiableDisplay extends IModifiable, ITinkerStationDisplay {
  /**
   * Gets a tool meant for rendering in a screen, can (and should) return the same stack on multiple calls
   *
   * @return the tool to use for rendering
   */
  ItemStack getRenderTool();

  /** Helper method to convert an item into its display tool, if it uses this interface */
  static ItemStack getDisplayStack(Item item) {
    return item instanceof IModifiableDisplay display ? display.getRenderTool() : new ItemStack(item);
  }

  /** Helper method to convert a stack into its display tool, if it uses this interface */
  static ItemStack getDisplayStack(ItemStack stack) {
    if (stack.getItem() instanceof IModifiableDisplay display) {
      ItemStack tool = display.getRenderTool();
      return stack.getCount() > 1 ? tool.copyWithCount(stack.getCount()) : tool;
    }
    return stack;
  }
}
