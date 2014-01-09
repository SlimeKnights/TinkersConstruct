package tconstruct.inventory;

import java.util.Random;

import tconstruct.blocks.logic.ToolStationLogic;
import tconstruct.library.tools.ToolCore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ToolStationContainer extends ActiveContainer
{
    public InventoryPlayer invPlayer;
    public ToolStationLogic logic;
    public Slot[] slots;
    public SlotTool toolSlot;
    public Random random = new Random();

    public ToolStationContainer(InventoryPlayer inventoryplayer, ToolStationLogic builderlogic)
    {
        initializeContainer(inventoryplayer, builderlogic);
    }

    public void initializeContainer (InventoryPlayer inventoryplayer, ToolStationLogic builderlogic)
    {
        invPlayer = inventoryplayer;
        logic = builderlogic;

        toolSlot = new SlotTool(inventoryplayer.player, builderlogic, 0, 115, 38);
        this.addSlotToContainer(toolSlot);
        slots = new Slot[] { new Slot(builderlogic, 1, 57, 29), new Slot(builderlogic, 2, 39, 38), new Slot(builderlogic, 3, 57, 47) };

        for (int iter = 0; iter < 3; iter++)
            this.addSlotToContainer(slots[iter]);

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

    //posX and posY must be the same length
    public void resetSlots (int[] posX, int[] posY)
    {
        /* Station inventory */
        inventorySlots.clear();
        inventoryItemStacks.clear();
        this.addSlotToContainer(toolSlot);
        for (int iter = 0; iter < 3; iter++)
        {
            slots[iter].xDisplayPosition = posX[iter] + 1;
            slots[iter].yDisplayPosition = posY[iter] + 1;
            addSlotToContainer(slots[iter]);
        }

        /* Player inventory */
        for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 9; row++)
            {
                this.addSlotToContainer(new Slot(invPlayer, row + column * 9 + 9, 8 + row * 18, 84 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
            this.addSlotToContainer(new Slot(invPlayer, column, 8 + column * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith (EntityPlayer var1)
    {
        return true;
    }

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
                if (slotID == 0)
                {
                    if (!this.mergeCraftedStack(slotStack, logic.getSizeInventory(), this.inventorySlots.size(), true, player))
                    {
                        return null;
                    }
                }
                else if (!this.mergeItemStack(slotStack, logic.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(slotStack, 1, logic.getSizeInventory(), false))
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

    protected void craftTool (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (!tags.getCompoundTag("InfiTool").hasKey("Built"))
        {
            tags.getCompoundTag("InfiTool").setBoolean("Built", true);
            for (int i = 2; i <= 3; i++)
                logic.decrStackSize(i, 1);
            int amount = logic.getStackInSlot(1).getItem() instanceof ToolCore ? stack.stackSize : 1;
            logic.decrStackSize(1, amount);

            logic.func_145831_w().playSoundEffect(logic.field_145851_c, logic.field_145848_d, logic.field_145849_e, "tinker:little_saw", 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        }
    }

    protected boolean mergeCraftedStack (ItemStack stack, int slotsStart, int slotsTotal, boolean playerInventory, EntityPlayer player)
    {
        boolean failedToMerge = false;
        int slotIndex = slotsStart;

        if (playerInventory)
        {
            slotIndex = slotsTotal - 1;
        }

        Slot otherInventorySlot;
        ItemStack copyStack = null;

        /*if (stack.isStackable())
        {
            while (stack.stackSize > 0 && (!playerInventory && slotIndex < slotsTotal || playerInventory && slotIndex >= slotsStart))
            {
                otherInventorySlot = (Slot)this.inventorySlots.get(slotIndex);
                copyStack = otherInventorySlot.getStack();

                if (copyStack != null && copyStack.itemID == stack.itemID && (!stack.getHasSubtypes() || stack.getItemDamage() == copyStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, copyStack))
                {
                    int totalSize = copyStack.stackSize + stack.stackSize;

                    if (totalSize <= stack.getMaxStackSize())
                    {
                        stack.stackSize = 0;
                        copyStack.stackSize = totalSize;
                        otherInventorySlot.onSlotChanged();
                        failedToMerge = true;
                    }
                    else if (copyStack.stackSize < stack.getMaxStackSize())
                    {
                        stack.stackSize -= stack.getMaxStackSize() - copyStack.stackSize;
                        copyStack.stackSize = stack.getMaxStackSize();
                        otherInventorySlot.onSlotChanged();
                        failedToMerge = true;
                    }
                }

                if (playerInventory)
                {
                    --slotIndex;
                }
                else
                {
                    ++slotIndex;
                }
            }
        }*/

        if (stack.stackSize > 0)
        {
            if (playerInventory)
            {
                slotIndex = slotsTotal - 1;
            }
            else
            {
                slotIndex = slotsStart;
            }

            while (!playerInventory && slotIndex < slotsTotal || playerInventory && slotIndex >= slotsStart)
            {
                otherInventorySlot = (Slot) this.inventorySlots.get(slotIndex);
                copyStack = otherInventorySlot.getStack();

                if (copyStack == null)
                {
                    craftTool(stack);
                    otherInventorySlot.putStack(stack.copy());
                    otherInventorySlot.onSlotChanged();
                    stack.stackSize = 0;
                    failedToMerge = true;
                    break;
                }

                if (playerInventory)
                {
                    --slotIndex;
                }
                else
                {
                    ++slotIndex;
                }
            }
        }

        /*boolean emptySlots = ( ((Slot) inventorySlots.get(2)).getStack() == null && ((Slot) inventorySlots.get(3)).getStack() == null );
        TConstruct.logger.info("Empty slots");
        if (!failedToMerge && emptySlots)
        	player.worldObj.playAuxSFX(1021, (int)player.posX, (int)player.posY, (int)player.posZ, 0);*/

        return failedToMerge;
    }
}
