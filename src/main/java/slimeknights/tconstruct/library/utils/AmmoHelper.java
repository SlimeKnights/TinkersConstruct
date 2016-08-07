package slimeknights.tconstruct.library.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ranged.IAmmo;

public final class AmmoHelper {

  private AmmoHelper() {}

  public static ItemStack findAmmoFromInventory(List<Item> ammoItems, Entity entity) {
    if(ammoItems == null || entity == null || !entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
      return null;
    }

    IItemHandler itemHandler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    // find an itemstack that matches our input. Hotbar first
    ItemStack ammo = validAmmoInRange(itemHandler, ammoItems, 0, InventoryPlayer.getHotbarSize());
    // then remaining inventory
    if(ammo == null) {
      ammo = validAmmoInRange(itemHandler, ammoItems, InventoryPlayer.getHotbarSize(), itemHandler.getSlots());
    }

    return ammo;
  }

  private static ItemStack validAmmoInRange(IItemHandler itemHandler, List<Item> ammoItems, int from, int to) {
    for(int i = from; i < to; i++) {
      ItemStack in = itemHandler.getStackInSlot(i);
      for(Item ammoItem : ammoItems) {
        if(in != null && in.getItem() == ammoItem) {
          if(!(ammoItem instanceof IAmmo) || ((IAmmo) ammoItem).getCurrentAmmo(in) > 0) {
            return in;
          }
        }
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
