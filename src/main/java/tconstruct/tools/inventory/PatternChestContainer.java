package tconstruct.tools.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import tconstruct.library.util.IPattern;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.logic.PatternChestLogic;

public class PatternChestContainer extends Container
{
    public PatternChestLogic logic;
    public int progress = 0;
    public int fuel = 0;
    public int fuelGague = 0;

    public PatternChestContainer(InventoryPlayer inventoryplayer, PatternChestLogic chest)
    {
        logic = chest;
        for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 10; row++)
            {
                this.addSlotToContainer(new SlotPattern(chest, row + column * 10, 8 + row * 18, 18 + column * 18));
            }
        }

        /* Player inventory */
        for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 9; row++)
            {
                this.addSlotToContainer(new Slot(inventoryplayer, row + column * 9 + 9, 17 + row * 18, 86 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
            this.addSlotToContainer(new Slot(inventoryplayer, column, 17 + column * 18, 144));
        }
    }

    @Override
    public boolean canInteractWith (EntityPlayer entityplayer)
    {
        Block block = logic.getWorldObj().getBlock(logic.xCoord, logic.yCoord, logic.zCoord);
        if (block != TinkerTools.toolStationWood && block != TinkerTools.craftingSlabWood)
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
    protected boolean mergeItemStack (ItemStack stack, int inventorySize, int slotSize, boolean par4)
    {
        if (!(stack.getItem() instanceof IPattern))
            return false;

        return super.mergeItemStack(stack, inventorySize, slotSize, par4);
    }
}