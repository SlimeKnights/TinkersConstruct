package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public interface ITinkerStationDisplay {

  /**
   * The "title" displayed in the GUI
   */
  ITextComponent getLocalizedName();

  /**
   * Returns an List of ITextComponent, where each Text Component represents an information about the tool. Used to display
   * Information about the item in the GUI
   */
  List<ITextComponent> getInformation(ItemStack stack);
}
