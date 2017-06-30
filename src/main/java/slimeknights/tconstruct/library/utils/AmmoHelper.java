package slimeknights.tconstruct.library.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ranged.IAmmo;

public final class AmmoHelper {

  private AmmoHelper() {}

  @Nonnull
  public static ItemStack findAmmoFromInventory(List<Item> ammoItems, Entity entity) {
    if(ammoItems == null || entity == null || !entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
      return ItemStack.EMPTY;
    }

    // we specifically check the equipment inventory first because it contains the offhand
    IItemHandler itemHandler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
    ItemStack ammo = ItemStack.EMPTY;
    if(itemHandler != null) {
      ammo = validAmmoInRange(itemHandler, ammoItems, 0, itemHandler.getSlots());
    }

    // and then the remaining inventory
    if(ammo.isEmpty()) {
      itemHandler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
      if(itemHandler != null) {
        int hotbarSize = 0;
        // find an itemstack that matches our input. Hotbar first
        if(entity instanceof EntityPlayer) {
          hotbarSize = Math.min(InventoryPlayer.getHotbarSize(), itemHandler.getSlots());
          ammo = validAmmoInRange(itemHandler, ammoItems, 0, hotbarSize);
        }
        // then remaining inventory
        if(ammo.isEmpty()) {
          ammo = validAmmoInRange(itemHandler, ammoItems, hotbarSize, itemHandler.getSlots());
        }
      }
    }

    return ammo;
  }

  @Nonnull
  private static ItemStack validAmmoInRange(IItemHandler itemHandler, List<Item> ammoItems, int from, int to) {
    for(int i = from; i < to; i++) {
      ItemStack in = itemHandler.getStackInSlot(i);
      for(Item ammoItem : ammoItems) {
        // same item
        if(!in.isEmpty() && in.getItem() == ammoItem) {
          // no ammoitem or ammoitem with ammo
          if(!(ammoItem instanceof IAmmo) || ((IAmmo) ammoItem).getCurrentAmmo(in) > 0) {
            return in;
          }
        }
      }
    }
    return ItemStack.EMPTY;
  }

  @Nonnull
  public static ItemStack getMatchingItemstackFromInventory(ItemStack stack, Entity entity, boolean damagedOnly) {
    if(stack == null || !entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
      return ItemStack.EMPTY;
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
    assert itemHandler != null;
    for(int i = 0; i < itemHandler.getSlots(); i++) {
      ItemStack in = itemHandler.getStackInSlot(i);
      if(ToolCore.isEqualTinkersItem(in, stack) && (!damagedOnly || in.getItemDamage() > 0)) {
        return in;
      }
    }

    return ItemStack.EMPTY;
  }
}
