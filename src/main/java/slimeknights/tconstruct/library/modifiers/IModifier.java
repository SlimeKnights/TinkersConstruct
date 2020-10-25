package slimeknights.tconstruct.library.modifiers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.tconstruct.library.modifiers.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.List;

public interface IModifier extends IForgeRegistryEntry<IModifier> {

  /*
   * The localized name of the tool modifier
   */
  ITextComponent getLocalizedName();

  /**
   * A short description to tell the user what the modifier does
   */
  ITextComponent getLocalizedDescription();

  /**
   * Extra info to display in the tool station. Each entry adds a line.
   */
  List<ITextComponent> getExtraInfo(ItemStack tool, ModifierNBT modifierNBT);

  /**
   * Return true to hide the trait from the user.
   * Useful for internal stuff.
   */
  boolean isHidden();

  boolean canApplyTogether(IModifier iToolMod);

  boolean canApplyTogether(Enchantment enchantment);

  /**
   * Apply the modifier to that itemstack. The complete procedure
   */
  ItemStack apply(ItemStack stack);

  /**
   * In this function the modifier saves its own data into the given tag.
   * Take a look at the ModifierNBT class for easy handling.
   * Do not apply any actual effect of the modifier here, ONLY update the modifiers tag!
   *
   * @param modifierNBT The current modifier nbt
   */
  void updateNBT(ModifierNBT modifierNBT);

  /**
   * This is the actual bread and butter of the modifier. This function applies the actual effect like adding a trait,
   * increasing miningspeed, etc. It is important that the application is DETERMINISTIC! The result has to solely depend
   * on the NBT of the modifierTag and has to give the same result every time. This is needed so the tool can be rebuilt and modifiers
   * reapplied.
   * Do NOT modify the tag itself. That's done in updateNBT. You'll get very unhappy otherwise.
   *
   * @param statsBuilder The stat builder containing the current stats of the tool being modified
   * @param modifierNBT  The nbt of the modifier, NOT EDITABLE
   */
  void applyStats(ModifiedToolStatsBuilder statsBuilder, ModifierNBT modifierNBT);

  /**
   * Returns the tooltip to display for the given tag of this specific modifier.
   * If detailed is true also include building info like how much X already is in it. Used in the toolstation display.
   * Color tags are not necessary.
   */
  ITextComponent getTooltip(ModifierNBT modifierNBT, boolean detailed);

  /**
   * Used for specific modifiers that need a texture variant for each material
   */
  default boolean hasTexturePerMaterial() {
    return false;
  }

  boolean equalModifier(ModifierNBT modifierNBT1, ModifierNBT modifierNBT2);

  /**
   *
   * @return
   */
  default int getColorIndex() {
    return 0;
  }

  @Override
  @Nullable
  ModifierId getRegistryName();

  @Override
  default Class<IModifier> getRegistryType() {
    return IModifier.class;
  }
}
