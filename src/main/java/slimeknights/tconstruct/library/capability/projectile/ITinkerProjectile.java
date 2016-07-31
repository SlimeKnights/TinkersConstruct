package slimeknights.tconstruct.library.capability.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface ITinkerProjectile extends INBTSerializable<NBTTagCompound> {

  ItemStack getItemStack();

  void setItemStack(ItemStack stack);

  boolean pickup(EntityLivingBase entity, boolean simulate);
}
