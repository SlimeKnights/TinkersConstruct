package tinker.tconstruct.client.gui;

import tinker.tconstruct.logic.PartCrafterLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PartCrafterContainer extends Container
{
	protected InventoryPlayer invPlayer;
	protected PartCrafterLogic logic;
	protected Slot[] input;
	protected Slot[] inventory;
	public boolean largeInventory;
	
	public PartCrafterContainer(InventoryPlayer inventoryplayer, PartCrafterLogic partLogic)
	{
		invPlayer = inventoryplayer;
		logic = partLogic;
		largeInventory = false;
		
		inventory = new Slot[] { new SlotPattern(partLogic, 0, 40, 27), new Slot(partLogic, 1, 58, 27), new SlotPattern(partLogic, 2, 40, 45), new Slot(partLogic, 3, 58, 45),
				new SlotOnlyTake(partLogic, 4, 102, 27), new SlotOnlyTake(partLogic, 5, 120, 27), new SlotOnlyTake(partLogic, 6, 102, 45), new SlotOnlyTake(partLogic, 7, 120, 45) };		
		for (int iter = 0; iter < inventory.length; iter ++)
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
	public boolean canInteractWith(EntityPlayer var1)
	{
		return true;
	}
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer entityplayer, int i)
    {
		//TODO: Shift-click override
		return null;
    }
}
