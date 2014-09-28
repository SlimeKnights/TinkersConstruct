package tconstruct.tools.inventory;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.library.event.ToolCraftedEvent;
import tconstruct.library.modifier.IModifyable;
import tconstruct.smeltery.inventory.ActiveContainer;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.logic.ToolStationLogic;

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

        toolSlot = new SlotTool(inventoryplayer.player, builderlogic, 0, 225, 38);
        this.addSlotToContainer(toolSlot);
        slots = new Slot[] { new Slot(builderlogic, 1, 167, 29), new Slot(builderlogic, 2, 149, 38), new Slot(builderlogic, 3, 167, 47) };

        for (int iter = 0; iter < 3; iter++)
            this.addSlotToContainer(slots[iter]);

        /* Player inventory */
        for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 9; row++)
            {
                this.addSlotToContainer(new Slot(inventoryplayer, row + column * 9 + 9, 118 + row * 18, 84 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
            this.addSlotToContainer(new Slot(inventoryplayer, column, 118 + column * 18, 142));
        }
    }

    // posX and posY must be the same length
    public void resetSlots (int[] posX, int[] posY)
    {
        /* Station inventory */
        inventorySlots.clear();
        inventoryItemStacks.clear();
        this.addSlotToContainer(toolSlot);
        for (int iter = 0; iter < 3; iter++)
        {
            slots[iter].xDisplayPosition = posX[iter] + 111;
            slots[iter].yDisplayPosition = posY[iter] + 1;
            addSlotToContainer(slots[iter]);
        }

        /* Player inventory */
        for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 9; row++)
            {
                this.addSlotToContainer(new Slot(invPlayer, row + column * 9 + 9, 118 + row * 18, 84 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
            this.addSlotToContainer(new Slot(invPlayer, column, 118 + column * 18, 142));
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
        if (stack.getItem() instanceof IModifyable)
        {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag(((IModifyable) stack.getItem()).getBaseTagName());
            Boolean full = (logic.getStackInSlot(2) != null || logic.getStackInSlot(3) != null);
            for (int i = 2; i <= 3; i++)
                logic.decrStackSize(i, 1);
            ItemStack compare = logic.getStackInSlot(1);
            int amount = compare.getItem() instanceof IModifyable ? compare.stackSize : 1;
            logic.decrStackSize(1, amount);
            EntityPlayer player = invPlayer.player;
            if (!player.worldObj.isRemote && full)
                player.worldObj.playSoundEffect(logic.xCoord, logic.yCoord, logic.zCoord, "tinker:little_saw", 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            MinecraftForge.EVENT_BUS.post(new ToolCraftedEvent(this.logic, player, stack));
        }
        else
        //Simply naming items        
        {
            int amount = logic.getStackInSlot(1).stackSize;
            logic.decrStackSize(1, amount);
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

        return failedToMerge;
    }
}
