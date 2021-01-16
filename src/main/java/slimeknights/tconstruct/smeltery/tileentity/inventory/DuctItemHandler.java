package slimeknights.tconstruct.smeltery.tileentity.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.common.TinkerTags;

/**
 * Item handler for the duct
 */
@RequiredArgsConstructor
public class DuctItemHandler implements IItemHandlerModifiable {
  private final MantleTileEntity parent;

  /** Current item in this slot */
  @Getter
  private ItemStack stack = ItemStack.EMPTY;

  public void setStack(ItemStack stack) {
    this.stack = stack;
    parent.markDirtyFast();
  }

  /**
   * Gets the fluid filter for this duct
   * @return  Fluid filter
   */
  public Fluid getFluid() {
    if (stack.isEmpty()) {
      return Fluids.EMPTY;
    }
    return FluidUtil.getFluidHandler(stack)
                    .map(handler -> handler.getFluidInTank(0).getFluid())
                    .orElse(Fluids.EMPTY);
  }


  /* Properties */

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return slot == 0 && stack.getItem().isIn(TinkerTags.Items.DUCT_CONTAINERS);
  }

  @Override
  public int getSlots() {
    return 1;
  }

  @Override
  public int getSlotLimit(int slot) {
    return 1;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    if (slot == 0) {
      return stack;
    }
    return ItemStack.EMPTY;
  }


  /* Interaction */

  @Override
  public void setStackInSlot(int slot, ItemStack stack) {
    if (slot == 0) {
      setStack(stack);
    }
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if (stack.isEmpty()) {
      return ItemStack.EMPTY;
    }
    if (slot != 0 || !this.stack.isEmpty()) {
      return stack;
    }
    if (!isItemValid(slot, stack)) {
      return stack;
    }
    if (!simulate) {
      setStack(ItemHandlerHelper.copyStackWithSize(stack, 1));
    }
    return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (amount == 0 || slot != 0) {
      return ItemStack.EMPTY;
    }
    if (stack.isEmpty()) {
      return ItemStack.EMPTY;
    }
    if (simulate) {
      return stack.copy();
    } else {
      ItemStack ret = stack;
      setStack(ItemStack.EMPTY);
      return ret;
    }
  }

  /**
   * Writes this module to NBT
   * @return  Module in NBT
   */
  public CompoundNBT writeToNBT() {
    CompoundNBT nbt = new CompoundNBT();
    if (!stack.isEmpty()) {
      stack.write(nbt);
    }
    return nbt;
  }

  /**
   * Reads this module from NBT
   * @param nbt  NBT
   */
  public void readFromNBT(CompoundNBT nbt) {
    stack = ItemStack.read(nbt);
  }
}
