package slimeknights.tconstruct.library.tools;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IAmmoUser {

  ItemStack findAmmo(@Nonnull ItemStack weapon, EntityLivingBase player);

  ItemStack getAmmoToRender(@Nonnull ItemStack weapon, EntityLivingBase player);
}
