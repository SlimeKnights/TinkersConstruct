package tconstruct.library.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModifier {

  String getIdentifier();

  /**
   * Called with a set of itemstacks and returns a match which contains the items that match
   * and how often the modifier can be applied with them
   */
  RecipeMatch.Match matches(ItemStack[] stacks);

  /**
   * Returns true if the modifier can be applied to the given itemstack
   *
   * @throws ModifyException Thrown if there is a specific reason why the modifier couldn't be applied.
   *                         The exception contains a localized string describing what's wrong.
   */
  boolean canApply(ItemStack stack) throws ModifyException;

  /** Apply the modifier to that itemstack. The complete procedure */
  void apply(ItemStack stack);

  /**
   * Apply the modifier to a root-nbt of an itemstack.
   * The complete procedure, usually called from the itemstack variant.
   */
  void apply(NBTTagCompound root);

  /**
   * In this function the modifier saves its own data into the given tag.
   * Take a look at the ModifierNBT class for easy handling.
   * Do not apply any actual effect of the modifier here, ONLY update the modifiers tag!
   *
   * @param modifierTag This tag shall be filled with data. It will be saved into the tool as the modifiers identifier.
   */
  void updateNBT(NBTTagCompound modifierTag);

  /**
   * This is the actual bread and butter of the modifier. This function applies the actual effect like adding a trait,
   * increasing miningspeed, etc. It is important that the application is DETERMINISTIC! The result has to solely depend
   * on the NBT of the modifierTag and has to give the same result every time. This is needed so the tool can be rebuilt and modifiers
   * reapplied.
   * Do NOT modify the tag itself. That's done in updateNBT. You'll get very unhappy otherwise.
   *
   * @param rootCompound The main compound of the item to be modified.
   * @param modifierTag  The same tag as for updateNBT.
   */
  void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag);

  /**
   * Returns the tooltip to display for the given tag of this specific modifier.
   * Color tags are not necessarry.
   */
  String getTooltip(NBTTagCompound modifierTag);

  String getLocalizedName();

  /** Used for specific modifiers that need a texture variant for each material */
  @SideOnly(Side.CLIENT)
  boolean hasTexturePerMaterial();

}
