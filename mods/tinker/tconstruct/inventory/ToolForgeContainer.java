package mods.tinker.tconstruct.inventory;

import mods.tinker.tconstruct.blocks.logic.ToolForgeLogic;
import mods.tinker.tconstruct.blocks.logic.ToolStationLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ToolForgeContainer extends ToolStationContainer
{

    public ToolForgeContainer(InventoryPlayer inventoryplayer, ToolForgeLogic logic)
    {
        super(inventoryplayer, logic);
    }

    public void initializeContainer (InventoryPlayer inventoryplayer, ToolStationLogic builderlogic)
    {
        invPlayer = inventoryplayer;
        this.logic = builderlogic;

        toolSlot = new SlotToolForge(inventoryplayer.player, logic, 0, 115, 38);
        this.addSlotToContainer(toolSlot);
        slots = new Slot[] { new Slot(logic, 1, 57, 29), new Slot(logic, 2, 39, 29), new Slot(logic, 3, 57, 47), new Slot(logic, 4, 39, 47) };

        for (int iter = 0; iter < 4; iter++)
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
        inventorySlots.clear();
        inventoryItemStacks.clear();
        this.addSlotToContainer(toolSlot);
        for (int iter = 0; iter < 4; iter++)
        {
            slots[iter].xDisplayPosition = posX[iter] + 1;
            slots[iter].yDisplayPosition = posY[iter] + 1;
            addSlotToContainer(slots[iter]);
        }

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

    protected void craftTool (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (!tags.getCompoundTag("InfiTool").hasKey("Built"))
        {
            tags.getCompoundTag("InfiTool").setBoolean("Built", true);
            for (int i = 1; i <= 4; i++)
                logic.decrStackSize(i, 1);
            if (!logic.worldObj.isRemote)
                logic.worldObj.playAuxSFX(1021, (int) logic.xCoord, (int) logic.yCoord, (int) logic.zCoord, 0);
        }
    }
}
