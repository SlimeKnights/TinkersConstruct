package tinker.armory.client;

import tinker.armory.content.EntityEquipment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ArmorStandContainer extends Container
{
	EntityEquipment stand;
	InventoryPlayer player;

	public ArmorStandContainer(InventoryPlayer inventoryplayer, EntityEquipment equipment)
	{
		stand = equipment;
		player = inventoryplayer;
		this.addSlotToContainer(new Slot(equipment, 0, 62, 26));

		for (int slot = 0; slot < 4; ++slot)
		{
			this.addSlotToContainer(new SlotArmorCopy(this, equipment, slot + 1, 80, 8 + (3-slot) * 18, 3-slot));
		}

		// Slot: inventory, slot index, xDisplay, yDisplay
		
		int var3;

        for (var3 = 0; var3 < 3; ++var3)
        {
            for (int var4 = 0; var4 < 9; ++var4)
            {
                this.addSlotToContainer(new Slot(player, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }

        for (var3 = 0; var3 < 9; ++var3)
        {
            this.addSlotToContainer(new Slot(player, var3, 8 + var3 * 18, 142));
        }
    }

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityplayer, int slot)
	{
		ItemStack var3 = null;
		Slot var4 = (Slot) this.inventorySlots.get(slot);

		if (var4 != null && var4.getHasStack())
		{
			ItemStack var5 = var4.getStack();
			var3 = var5.copy();

			if (slot < 5)
			{
				if (!this.mergeItemStack(var5, 5, this.inventorySlots.size(), true))
				{
					return null;
				}
			} else if (!this.mergeItemStack(var5, 0, 5, false))
			{
				return null;
			}

			if (var5.stackSize == 0)
			{
				var4.putStack((ItemStack) null);
			} else
			{
				var4.onSlotChanged();
			}
		}

		return var3;
	}
}
