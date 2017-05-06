package slimeknights.tconstruct.library.tools;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IAmmoUser {

  @Nonnull
  ItemStack findAmmo(@Nonnull ItemStack weapon, EntityLivingBase player);

  @Nonnull
  ItemStack getAmmoToRender(@Nonnull ItemStack weapon, EntityLivingBase player);
}
