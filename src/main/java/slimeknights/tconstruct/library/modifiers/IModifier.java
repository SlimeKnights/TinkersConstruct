package slimeknights.tconstruct.library.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IModifier extends IToolMod {

  /**
   * Apply the modifier to that itemstack. The complete procedure
   */
  void apply(ItemStack stack);

  /**
   * Apply the modifier to a root-nbt of an itemstack.
   * The complete procedure, usually called from the itemstack variant.
   */
  void apply(CompoundNBT root);

  /**
   * In this function the modifier saves its own data into the given tag.
   * Take a look at the ModifierNBT class for easy handling.
   * Do not apply any actual effect of the modifier here, ONLY update the modifiers tag!
   *
   * @param modifierTag This tag shall be filled with data. It will be saved into the tool as the modifiers identifier.
   */
  void updateNBT(CompoundNBT modifierTag);

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
  void applyEffect(CompoundNBT rootCompound, CompoundNBT modifierTag);

  /**
   * Returns the tooltip to display for the given tag of this specific modifier.
   * If detailed is true also include building info like how much X already is in it. Used in the toolstation display.
   * Color tags are not necessary.
   */
  ITextComponent getTooltip(CompoundNBT modifierTag, boolean detailed);

  /**
   * Used for specific modifiers that need a texture variant for each material
   */
  default boolean hasTexturePerMaterial() {
    return false;
  }

  boolean equalModifier(CompoundNBT modifierTag1, CompoundNBT modifierTag2);
}
