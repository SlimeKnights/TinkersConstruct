package tconstruct.tools.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import tconstruct.armor.inventory.SlotOnlyTake;
import tconstruct.library.util.IPattern;
import tconstruct.smeltery.inventory.ActiveContainer;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.logic.*;

public class PartCrafterChestContainer extends ActiveContainer
{
    protected PatternChestLogic patternLogic;
    protected InventoryPlayer invPlayer;
    protected PartBuilderLogic logic;
    protected Slot[] input;
    protected Slot[] inventory;
    public boolean largeInventory;

    public PartCrafterChestContainer(InventoryPlayer inventoryplayer, PartBuilderLogic partLogic, PatternChestLogic pLogic)
    {
        logic = partLogic;
        patternLogic = pLogic;
        largeInventory = true;

        inventory = new Slot[] { new SlotPattern(partLogic, 0, 156, 27), new SlotPattern(partLogic, 1, 156, 45), new Slot(partLogic, 2, 174, 27), new Slot(partLogic, 3, 174, 45), new SlotOnlyTake(partLogic, 4, 218, 27), new SlotOnlyTake(partLogic, 5, 236, 27), new SlotOnlyTake(partLogic, 6, 218, 45), new SlotOnlyTake(partLogic, 7, 236, 45) };
        for (int iter = 0; iter < inventory.length; iter++)
            this.addSlotToContainer(inventory[iter]);

        /* Holder inventory */
        for (int column = 0; column < 5; column++)
        {
            for (int row = 0; row < 6; row++)
            {
                this.addSlotToContainer(new SlotPattern(pLogic, row + column * 6, 8 + row * 18, 30 + column * 18));
            }
        }

        /* Player inventory */
        for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 9; row++)
            {
                this.addSlotToContainer(new Slot(inventoryplayer, row + column * 9 + 9, 124 + row * 18, 84 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
            this.addSlotToContainer(new Slot(inventoryplayer, column, 124 + column * 18, 142));
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
                if (!this.mergeItemStack(slotStack, logic.getSizeInventory() + patternLogic.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else
            {
                if (slotStack.getItem() instanceof IPattern)
                {
                    if (!this.mergeItemStack(slotStack, 0, 2, false) && !this.mergeItemStack(slotStack, 8, 38, false))
                        return null;
                }
                else if (!this.mergeItemStack(slotStack, 2, 4, false))
                    return null;
            }

            if (slotStack.stackSize == 0)
            {
                slot.putStack((ItemStack) null);
                logic.tryBuildPart(slotID);
            }
            slot.onSlotChanged();
        }

        return stack;
    }
}
