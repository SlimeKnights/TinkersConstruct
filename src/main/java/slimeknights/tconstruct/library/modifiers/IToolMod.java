package slimeknights.tconstruct.library.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Contains the common things modifiers and traits share.
 * Basically basic information that relates to user display
 */
public interface IToolMod {

  String getIdentifier();

  String getLocalizedName();

  /** A short description to tell the user what the trait does */
  String getLocalizedDesc();

  /** Extra info to display in the tool station. Each entry adds a line. */
  List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag);

  /**
   * Return true to hide the trait from the user.
   * Useful for internal stuff.
   */
  boolean isHidden();
}
