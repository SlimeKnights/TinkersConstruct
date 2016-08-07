package slimeknights.tconstruct.library.capability.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import slimeknights.tconstruct.library.tools.ranged.IAmmo;
import slimeknights.tconstruct.library.utils.AmmoHelper;
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
  public boolean pickup(EntityLivingBase entity, boolean simulate) {
    ItemStack stack = AmmoHelper.getMatchingItemstackFromInventory(parent, entity, true);
    if(stack != null && stack.getItem() instanceof IAmmo) {
      if(!simulate && parent.stackSize > 0) {
        ToolHelper.unbreakTool(stack);
        ((IAmmo) stack.getItem()).addAmmo(stack, entity);
      }
      return true;
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
