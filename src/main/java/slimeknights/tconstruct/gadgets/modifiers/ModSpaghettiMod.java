package slimeknights.tconstruct.gadgets.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;

public class ModSpaghettiMod extends Modifier {

  public ModSpaghettiMod(String suffix, int color) {
    super("spaghetti_" + suffix);

    addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this, color));
  }

  @Override
  protected boolean canApplyCustom(ItemStack stack) throws TinkerGuiException {
    return stack.getItem() == TinkerGadgets.momsSpaghetti;
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {

  }
}
