package tconstruct.smeltery.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.smeltery.logic.SmelteryLogicOld;

public class SmelteryContainerOld extends ActiveContainer
{
    public SmelteryLogicOld logic;
    public InventoryPlayer playerInv;
    public int fuel = 0;
    int slotRow;

    public SmelteryContainerOld(InventoryPlayer inventoryplayer, SmelteryLogicOld smeltery)
    {
        logic = smeltery;
        playerInv = inventoryplayer;
        slotRow = 0;

        /* Smeltery inventory */
        for (int y = 0; y < smeltery.layers * 3; y++) {
            for (int x = 0; x < 3; x++) {
                this.addDualSlotToContainer(new ActiveSlot(smeltery, x + y * 3, 2 + x * 22, 8 + y * 18, y < 8));
            }
        }

        /* Player inventory */
        for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 9; row++)
            {
                this.addSlotToContainer(new Slot(inventoryplayer, row + column * 9 + 9, 90 + row * 18, 84 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
            this.addSlotToContainer(new Slot(inventoryplayer, column, 90 + column * 18, 142));
        }
    }

    public int updateRows (int invRow)
    {
        if (invRow != slotRow)
        {
            slotRow = invRow;
            // TConstruct.logger.info(invRow);
            int basePos = invRow * 3;
            for (int iter = 0; iter < activeInventorySlots.size(); iter++)
            {
                ActiveSlot slot = (ActiveSlot) activeInventorySlots.get(iter);
                if (slot.activeSlotNumber >= basePos && slot.activeSlotNumber < basePos + 24)
                {
                    slot.setActive(true);
                }
                else
                {
                    slot.setActive(false);
                }
                int xPos = (iter - basePos) % 3;
                int yPos = (iter - basePos) / 3;
                slot.xDisplayPosition = 2 + 22 * xPos;
                slot.yDisplayPosition = 8 + 18 * yPos;
            }
            return slotRow;
        }
        return -1;
    }

    public int scrollTo (float scrollPos)
    {
        float total = (logic.getSizeInventory() - 24) / 3;
        int rowPos = Math.round(total * scrollPos);
        return updateRows(rowPos);
    }

    @Override
    public void detectAndSendChanges () // TODO: Sync with this
    {
        super.detectAndSendChanges();
        /*
         * for (int i = 0; i < crafters.size(); i++) { ICrafting icrafting =
         * (ICrafting)crafters.get(i); if (progress != logic.progress) {
         * icrafting.sendProgressBarUpdate(this, 0, logic.progress); } if (fuel
         * != logic.fuel) { icrafting.sendProgressBarUpdate(this, 1,
         * logic.fuel); } if (fuelGague != logic.fuelGague) {
         * icrafting.sendProgressBarUpdate(this, 2, logic.fuelGague); } }
         * 
         * progress = logic.progress; fuel = logic.fuel; fuelGague =
         * logic.fuelGague;
         */
    }

    @Override
    public void updateProgressBar (int id, int value)
    {
        if (id == 0)
        {
            logic.fuelGague = value;
        }
        /*
         * if (id == 1) { logic.fuel = value; }
         */
        /*
         * if (id == 2) { logic.fuelGague = value; }
         */
    }

    @Override
    public boolean canInteractWith (EntityPlayer entityplayer)
    {
        Block block = logic.getWorldObj().getBlock(logic.xCoord, logic.yCoord, logic.zCoord);
        if (block != TinkerSmeltery.smeltery && block != TinkerSmeltery.smelteryNether)
            return false;
        return logic.isUseableByPlayer(entityplayer);
    }

    @Override
    public ItemStack transferStackInSlot (EntityPlayer player, int slotID)
    {
        ItemStack stack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotID);

        if (slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if (slotID < logic.getSizeInventory())
            {
                if (!this.mergeItemStack(slotStack, logic.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(slotStack, 0, logic.getSizeInventory(), false))
            {
                return null;
            }

            if (slotStack.stackSize == 0)
            {
                slot.putStack((ItemStack) null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return stack;
    }

    @Override
    protected boolean mergeItemStack (ItemStack inputStack, int startSlot, int endSlot, boolean flag)
    {
        // TConstruct.logger.info("Merge");
        boolean merged = false;
        int slotPos = startSlot;

        if (flag)
        {
            slotPos = endSlot - 1;
        }

        Slot slot;
        ItemStack slotStack;

        /*
         * if (inputStack.isStackable() && startSlot >=
         * logic.getSizeInventory()) { TConstruct.logger.info("Rawr!"); while
         * (inputStack.stackSize > 0 && (!flag && slotPos < endSlot || flag &&
         * slotPos >= startSlot)) { slot = (Slot)
         * this.inventorySlots.get(slotPos); slotStack = slot.getStack();
         * 
         * if (slotStack != null && ItemStack.areItemStacksEqual(inputStack,
         * slotStack) && !inputStack.getHasSubtypes()) { int totalSize =
         * slotStack.stackSize + inputStack.stackSize;
         * 
         * if (totalSize <= inputStack.getMaxStackSize()) { inputStack.stackSize
         * = 0; slotStack.stackSize = totalSize; slot.onSlotChanged(); merged =
         * true; } else if (slotStack.stackSize < inputStack.getMaxStackSize())
         * { inputStack.stackSize -= inputStack.getMaxStackSize() -
         * slotStack.stackSize; slotStack.stackSize =
         * inputStack.getMaxStackSize(); slot.onSlotChanged(); merged = true; }
         * }
         * 
         * if (flag) { --slotPos; } else { ++slotPos; } } }
         */

        if (inputStack.isStackable() && startSlot >= logic.getSizeInventory())
        {
            while (inputStack.stackSize > 0 && (!flag && slotPos < endSlot || flag && slotPos >= startSlot))
            {
                slot = (Slot) this.inventorySlots.get(slotPos);
                slotStack = slot.getStack();

                if (slotStack != null && ItemStack.areItemStacksEqual(slotStack, inputStack))
                {
                    int l = slotStack.stackSize + inputStack.stackSize;

                    if (l <= inputStack.getMaxStackSize())
                    {
                        inputStack.stackSize = 0;
                        slotStack.stackSize = l;
                        slot.onSlotChanged();
                        merged = true;
                    }
                    else if (slotStack.stackSize < inputStack.getMaxStackSize())
                    {
                        inputStack.stackSize -= inputStack.getMaxStackSize() - slotStack.stackSize;
                        slotStack.stackSize = inputStack.getMaxStackSize();
                        slot.onSlotChanged();
                        merged = true;
                    }
                }

                if (flag)
                {
                    --slotPos;
                }
                else
                {
                    ++slotPos;
                }
            }
        }

        if (inputStack.stackSize > 0)
        {
            if (flag)
            {
                slotPos = endSlot - 1;
            }
            else
            {
                slotPos = startSlot;
            }

            while (!flag && slotPos < endSlot || flag && slotPos >= startSlot)
            {
                slot = (Slot) this.inventorySlots.get(slotPos);
                slotStack = slot.getStack();

                if (slotStack == null)
                {
                    slot.putStack(inputStack.copy());
                    slot.onSlotChanged();
                    inputStack.stackSize -= 1;
                    merged = true;
                    break;
                }

                if (flag)
                {
                    --slotPos;
                }
                else
                {
                    ++slotPos;
                }
            }
        }

        return merged;
    }
}
