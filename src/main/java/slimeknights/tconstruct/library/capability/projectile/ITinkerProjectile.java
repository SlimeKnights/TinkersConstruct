package slimeknights.tconstruct.library.capability.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface ITinkerProjectile extends INBTSerializable<NBTTagCompound> {

  ItemStack getItemStack();

  void setItemStack(ItemStack stack);

  boolean pickup(Entity entity, boolean simulate);
}
