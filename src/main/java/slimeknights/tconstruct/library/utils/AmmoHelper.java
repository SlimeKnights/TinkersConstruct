package slimeknights.tconstruct.library.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import slimeknights.tconstruct.library.tools.ToolCore;

public final class AmmoHelper {

  private AmmoHelper() {}

  public static ItemStack findAmmoFromInventory(Item ammoItem, Entity entity) {
    if(ammoItem == null || !entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
      return null;
    }

    IItemHandler itemHandler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    // find an itemstack that matches our input
    for(int i = 0; i < itemHandler.getSlots(); i++) {
      ItemStack in = itemHandler.getStackInSlot(i);
      if(in != null && in.getItem() == ammoItem) {
        return in;
      }
    }

    return null;
  }

  public static ItemStack getMatchingItemstackFromInventory(ItemStack stack, Entity entity, boolean damagedOnly) {
    if(stack == null || !entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
      return null;
    }

    // try main and off hand first, because priority (yes they're also covered in the loop below.)
    if(entity instanceof EntityLivingBase) {
      ItemStack in = ((EntityLivingBase) entity).getHeldItemMainhand();
      if(ToolCore.isEqualTinkersItem(in, stack) && (!damagedOnly || in.getItemDamage() > 0)) {
        return in;
      }

      in = ((EntityLivingBase) entity).getHeldItemOffhand();
      if(ToolCore.isEqualTinkersItem(in, stack) && (!damagedOnly || in.getItemDamage() > 0)) {
        return in;
      }
    }


    IItemHandler itemHandler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

    // find an itemstack that matches our input
    for(int i = 0; i < itemHandler.getSlots(); i++) {
      ItemStack in = itemHandler.getStackInSlot(i);
      if(ToolCore.isEqualTinkersItem(in, stack) && (!damagedOnly || in.getItemDamage() > 0)) {
        return in;
      }
    }

    return null;
  }
}
