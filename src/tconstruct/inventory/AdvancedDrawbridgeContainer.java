package tconstruct.inventory;

import tconstruct.client.gui.AdvDrawbridgeGui;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import tconstruct.blocks.logic.*;

public class AdvancedDrawbridgeContainer extends Container {
	public AdvancedDrawbridgeLogic logic;
	public int progress = 0;
	public int fuel = 0;
	public int fuelGague = 0;

	private AdvDrawbridgeGui gui = null;

	public AdvancedDrawbridgeContainer(InventoryPlayer inventoryplayer, AdvancedDrawbridgeLogic logic) {
		this.logic = logic;

		addContainerSlots(logic);
		bindPlayerInventory(inventoryplayer);
		updateContainerSlots();
	}

	public AdvancedDrawbridgeContainer(InventoryPlayer inventoryplayer, AdvancedDrawbridgeLogic logic, AdvDrawbridgeGui gui) {
		this.logic = logic;
		this.gui = gui;
		
		addContainerSlots(logic);
		bindPlayerInventory(inventoryplayer);
		updateContainerSlots();
	}

	public void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int column = 0; column < 3; column++) {
			for (int row = 0; row < 9; row++) {
				this.addSlotToContainer(new Slot(inventoryPlayer, row + column * 9 + 9, 8 + row * 18, 84 + column * 18));
			}
		}

		for (int column = 0; column < 9; column++) {
			this.addSlotToContainer(new Slot(inventoryPlayer, column, 8 + column * 18, 142));
		}
	}

	public void addContainerSlots(AdvancedDrawbridgeLogic logic) {
		this.addSlotToContainer(new SlotOpaqueBlocksOnly(logic.camoInventory, 0, 35, 36));
		
		for(int i = 0; i < logic.getSizeInventory(); i++){
			this.addSlotToContainer(new DrawbridgeSlot(logic, i, -2 + 18 * i, 40, logic));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return logic.isUseableByPlayer(entityplayer);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		ItemStack stack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotID);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();

			if (slotID < logic.getSizeInventory()) {
				if (!this.mergeItemStack(slotStack, logic.getSizeInventory(), this.inventorySlots.size(), true)) {
					return null;
				}
			} else if (!this.mergeItemStack(slotStack, 0, logic.getSizeInventory(), false)) {
				return null;
			}

			if (slotStack.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
		}

		return stack;
	}

	public void updateContainerSlots() {
		if (gui == null || gui.isGuiExpanded) {

		}
	}
}
