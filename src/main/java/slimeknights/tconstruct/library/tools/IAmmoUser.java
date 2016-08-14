package slimeknights.tconstruct.library.tools;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IAmmoUser {

  ItemStack findAmmo(ItemStack weapon, EntityLivingBase player);

  ItemStack getAmmoToRender(ItemStack weapon, EntityLivingBase player);
}
