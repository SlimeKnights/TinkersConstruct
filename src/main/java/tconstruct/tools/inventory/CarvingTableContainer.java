package tconstruct.tools.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import tconstruct.armor.inventory.SlotOnlyTake;
import tconstruct.library.util.IPattern;
import tconstruct.smeltery.inventory.ActiveContainer;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.logic.CarvingTableLogic;
import tconstruct.tools.logic.PartBuilderLogic;

public class CarvingTableContainer extends ActiveContainer
{
    protected InventoryPlayer invPlayer;
    protected CarvingTableLogic logic;
    protected Slot[] input;
    protected Slot[] inventory;
    public boolean largeInventory;

    public CarvingTableContainer (InventoryPlayer inventoryplayer, CarvingTableLogic carveLogic)
    {
        invPlayer = inventoryplayer;
        logic = carveLogic;
        largeInventory = false;

        inventory = new Slot[] { new Slot(carveLogic, 0, 58, 27), new Slot(carveLogic, 1, 58, 45), new SlotOnlyTake(carveLogic, 2, 102, 27),
                new SlotOnlyTake(carveLogic, 3, 120, 27), new SlotOnlyTake(carveLogic, 4, 102, 45), new SlotOnlyTake(carveLogic, 5, 120, 45) };
        for (int iter = 0; iter < inventory.length; iter++)
            this.addSlotToContainer(inventory[iter]);

        /* Player inventory */
        for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 9; row++)
            {
                this.addSlotToContainer(new Slot(inventoryplayer, row + column * 9 + 9, 8 + row * 18, 84 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
            this.addSlotToContainer(new Slot(inventoryplayer, column, 8 + column * 18, 142));
        }

    }

    @Override
    public boolean canInteractWith (EntityPlayer entityplayer)
    {
        Block block = logic.getWorldObj().getBlock(logic.xCoord, logic.yCoord, logic.zCoord);
        //if (block != TinkerTools.toolStationWood && block != TinkerTools.craftingSlabWood)
        //    return false;
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
            else if (!this.mergeItemStack(slotStack, 0, 2, false))
            {
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
