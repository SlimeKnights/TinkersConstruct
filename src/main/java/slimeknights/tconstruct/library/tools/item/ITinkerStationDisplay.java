package slimeknights.tconstruct.library.tools.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.utils.Util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Interface to implement for tools that also display in the tinker station
 */
public interface ITinkerStationDisplay {
  /** Tooltip telling the player to hold shift for more info */
  ITextComponent TOOLTIP_HOLD_SHIFT = TConstruct.makeTranslation("tooltip", "hold_shift", TConstruct.makeTranslation("key", "shift").mergeStyle(TextFormatting.YELLOW, TextFormatting.ITALIC));
  /** Tooltip telling the player to hold control for part info */
  ITextComponent TOOLTIP_HOLD_CTRL = TConstruct.makeTranslation("tooltip", "hold_ctrl", TConstruct.makeTranslation("key", "ctrl").mergeStyle(TextFormatting.AQUA, TextFormatting.ITALIC));

  /**
   * The "title" displayed in the GUI
   */
  ITextComponent getLocalizedName();

  /**
   * Returns an List of ITextComponent, where each Text Component represents an information about the tool. Used to display
   * Information about the item in the GUI
   */
  List<ITextComponent> getInformation(ItemStack stack);

  /**
   * Combines the given display name with the material names to form the new given name
   *
   * @param itemName the standard display name
   * @param materials the list of materials
   * @return the combined item name
   */
  static ITextComponent getCombinedItemName(ITextComponent itemName, Collection<IMaterial> materials) {
    if (materials.isEmpty() || materials.stream().allMatch(IMaterial.UNKNOWN::equals)) {
      return itemName;
    }

    if (materials.size() == 1) {
      IMaterial material = materials.iterator().next();

      if (Util.canTranslate(material.getTranslationKey() + ".format")) {
        return new TranslationTextComponent(material.getTranslationKey() + ".format", itemName);
      }

      return new TranslationTextComponent(materials.iterator().next().getTranslationKey()).appendSibling(new StringTextComponent(" ")).appendSibling(itemName);
    }

    // multiple materials. we'll have to combine
    StringTextComponent name = new StringTextComponent("");

    Iterator<IMaterial> iter = materials.iterator();

    IMaterial material = iter.next();
    name.appendSibling(new TranslationTextComponent(material.getTranslationKey()));

    while (iter.hasNext()) {
      material = iter.next();
      name.appendString("-").appendSibling(new TranslationTextComponent(material.getTranslationKey()));
    }

    name.appendString(" ").appendSibling(itemName);

    return name;
  }
}
