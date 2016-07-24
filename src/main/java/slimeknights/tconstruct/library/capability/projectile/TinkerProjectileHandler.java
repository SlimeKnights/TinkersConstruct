package slimeknights.tconstruct.library.capability.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TinkerProjectileHandler implements ITinkerProjectile, INBTSerializable<NBTTagCompound> {

  private ItemStack parent;

  public TinkerProjectileHandler() {
  }

  @Override
  public ItemStack getItemStack() {
    return parent;
  }

  @Override
  public void setItemStack(ItemStack stack) {
    parent = stack;
  }

  @Override
  public boolean pickup(Entity entity, boolean simulate) {
    ItemStack stack = getMatchingItemstackFromInventory(entity, true);
    if(stack != null) {
      if(!simulate) {
        if(ToolHelper.isBroken(stack)) {
          ToolHelper.unbreakTool(stack);
        }
        ToolHelper.healTool(stack, parent.stackSize, null);
      }
      return true;
    }

    return false;
  }

  public ItemStack getMatchingItemstackFromInventory(Entity entity, boolean damagedOnly) {
    if(parent == null || !entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
      return null;
    }

    // try main and off hand first, because priority (yes they're also covered in the loop below.)
    if(entity instanceof EntityLivingBase) {
      ItemStack in = ((EntityLivingBase) entity).getHeldItemMainhand();
      if(ToolCore.isEqualTinkersItem(in, parent) && (!damagedOnly || in.getItemDamage() > 0)) {
        return in;
      }

      in = ((EntityLivingBase) entity).getHeldItemOffhand();
      if(ToolCore.isEqualTinkersItem(in, parent) && (!damagedOnly || in.getItemDamage() > 0)) {
        return in;
      }
    }


    IItemHandler itemHandler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

    // find an itemstack that matches our input
    for(int i = 0; i < itemHandler.getSlots(); i++) {
      ItemStack in = itemHandler.getStackInSlot(i);
      if(ToolCore.isEqualTinkersItem(in, parent) && (!damagedOnly || in.getItemDamage() > 0)) {
        return in;
      }
    }

    return null;
  }

  @Override
  public NBTTagCompound serializeNBT() {
    NBTTagCompound tag = new NBTTagCompound();
    if(parent != null) {
      parent.writeToNBT(tag);
    }
    return tag;
  }

  @Override
  public void deserializeNBT(NBTTagCompound nbt) {
    parent = ItemStack.loadItemStackFromNBT(nbt);
  }
}
