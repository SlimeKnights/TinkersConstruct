package slimeknights.tconstruct.library.capability.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

import javax.annotation.Nullable;

import slimeknights.tconstruct.library.traits.IProjectileTrait;
import slimeknights.tconstruct.library.traits.ITrait;

public interface ITinkerProjectile extends INBTSerializable<NBTTagCompound> {

  ItemStack getItemStack();

  void setItemStack(ItemStack stack);

  @Nullable
  ItemStack getLaunchingStack();

  void setLaunchingStack(ItemStack launchingStack);

  List<IProjectileTrait> getProjectileTraits();

  boolean pickup(EntityLivingBase entity, boolean simulate);
}
