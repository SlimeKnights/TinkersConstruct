package slimeknights.tconstruct.library.tools;

import com.sun.istack.internal.NotNull;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IAmmoUser {

  ItemStack findAmmo(@Nonnull ItemStack weapon, EntityLivingBase player);

  ItemStack getAmmoToRender(@NotNull ItemStack weapon, EntityLivingBase player);
}
