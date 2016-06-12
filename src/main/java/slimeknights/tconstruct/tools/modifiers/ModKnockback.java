package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class ModKnockback extends ModifierTrait {

  public ModKnockback() {
    super("knockback", 0x9f9f9f, 99, 10); // the sky is the limit, wheeeee
  }

  @Override
  public float knockBack(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float knockback, float newKnockback, boolean isCritical) {
    return newKnockback + calcKnockback(TinkerUtil.getModifierTag(tool, identifier));
  }

  protected float calcKnockback(NBTTagCompound modifierTag) {
    ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);
    return (float) data.current * 0.1f;
  }
}
