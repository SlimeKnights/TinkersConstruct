package slimeknights.tconstruct.library.modifiers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Contains the common things modifiers and traits share.
 * Basically basic information that relates to user display
 */
public interface IToolMod {

  IFormattableTextComponent getLocalizedName();

  /**
   * A short description to tell the user what the trait does
   */
  IFormattableTextComponent getLocalizedDescription();

  /**
   * Extra info to display in the tool station. Each entry adds a line.
   */
  List<IFormattableTextComponent> getExtraInfo(ItemStack tool, CompoundNBT modifierTag);

  /**
   * Return true to hide the trait from the user.
   * Useful for internal stuff.
   */
  boolean isHidden();

  boolean canApplyTogether(IToolMod iToolMod);

  boolean canApplyTogether(Enchantment enchantment);
}
