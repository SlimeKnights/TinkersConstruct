package slimeknights.tconstruct.library.tools;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

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
    // up == main inventory, does mean we don't have to bother with the inventory itself
    if(parent == null || !entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
      return false;
    }

    IItemHandler itemHandler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

    // find an itemstack that matches our input
    for(int i = 0; i < itemHandler.getSlots(); i++) {
      ItemStack in = itemHandler.getStackInSlot(i);
      if(ToolCore.isEqualTinkersItem(in, parent)) {
        if(!simulate) {
          if(ToolHelper.isBroken(in)) {
            ToolHelper.unbreakTool(in);
          }
          ToolHelper.healTool(in, parent.stackSize, null);
        }
        return true;
      }
    }

    return false;
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
