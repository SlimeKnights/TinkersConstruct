package tinker.tconstruct.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import tinker.tconstruct.logic.ToolStationLogic;

public class ToolStationContainer extends Container
{
	InventoryPlayer invPlayer;
	ToolStationLogic logic;
	String toolName;
	Slot[] slots;
	SlotTool toolSlot;
	
	public ToolStationContainer(InventoryPlayer inventoryplayer, ToolStationLogic builderlogic)
	{
		invPlayer = inventoryplayer;
		logic = builderlogic;
		
		toolSlot = new SlotTool(inventoryplayer.player, builderlogic, 0, 115, 38);
		this.addSlotToContainer(toolSlot);
		slots = new Slot[] { new Slot(builderlogic, 1, 57, 29), new Slot(builderlogic, 2, 39, 38), new Slot(builderlogic, 3, 57, 47) };
		
		for (int iter = 0; iter < 3; iter ++)
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
        
        toolName = "";
		
	}
	
	//posX and posY must be the same length
	public void resetSlots(int[] posX, int[] posY)
	{
		/* Station inventory */
		inventorySlots.clear();
		inventoryItemStacks.clear();
		this.addSlotToContainer(toolSlot);
		for (int iter = 0; iter < posX.length; iter++)
		{
			slots[iter].xDisplayPosition = posX[iter]+1;
			slots[iter].yDisplayPosition = posY[iter]+1;
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
        
        logic.dumpExtraItems(posX.length, invPlayer.player);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer var1)
	{
		return true;
	}
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer entityplayer, int i)
    {
		return null;
    }

	public void setToolName(String name)
	{
		toolName = name;
		logic.buildTool(name);
		detectAndSendChanges();
	}
}
