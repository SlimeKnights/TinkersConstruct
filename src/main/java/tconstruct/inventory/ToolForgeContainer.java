package tconstruct.inventory;

import tconstruct.blocks.logic.ToolForgeLogic;
import tconstruct.blocks.logic.ToolStationLogic;
import tconstruct.library.tools.ToolCore;
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

    @Override
    protected void craftTool (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (!tags.getCompoundTag("InfiTool").hasKey("Built"))
        {
            tags.getCompoundTag("InfiTool").setBoolean("Built", true);
            for (int i = 2; i <= 4; i++)
                logic.decrStackSize(i, 1);
            int amount = logic.getStackInSlot(1).getItem() instanceof ToolCore ? stack.stackSize : 1;
            logic.decrStackSize(1, amount);
            if (!logic.getWorld().isRemote)
                logic.getWorld().playAuxSFX(1021, (int) logic.field_145851_c, (int) logic.field_145848_d, (int) logic.field_145849_e, 0);
        }
    }
}
