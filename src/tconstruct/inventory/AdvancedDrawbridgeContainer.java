package tconstruct.inventory;

import net.minecraft.entity.player.EntityPlayer;

import java.util.Iterator;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import tconstruct.blocks.logic.AdvancedDrawbridgeLogic;
import tconstruct.client.gui.AdvDrawbridgeGui;

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

		for (int i = 0; i < logic.getSizeInventory(); i++) {
			int ind = (int) (Math.floor(i / 8) * 8);
			int x = i < 8 ? 10 + 20 * i : 10 + 20 * (i - 8);
			int y = 35 + (int) Math.floor(i / 8) * 18 + (i < 8 ? 0 : 1);
			this.addSlotToContainer(new DrawbridgeSlot(logic, i, x, y, logic));
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
		if (gui == null) {
			return;
		} else if (gui.isGuiExpanded) {
			Iterator<Slot> i1 = inventorySlots.iterator();
			int index = 0;
			while (i1.hasNext()) {
				Slot sl = i1.next();
				if (index == 0) {
					sl.xDisplayPosition = -1000;
					sl.yDisplayPosition = -1000;
				} else if (sl instanceof DrawbridgeSlot) {
					sl.xDisplayPosition = (index - 1) < 8 ? 10 + 20 * (index - 1) : 10 + 20 * ((index - 1) - 8);
					sl.yDisplayPosition = 35 + (int) Math.floor((index - 1) / 8) * 18 + ((index - 1) < 8 ? 0 : 1);
				}
				index++;
			}
		} else {
			Iterator<Slot> i1 = inventorySlots.iterator();
			int index = 0;
			while (i1.hasNext()) {
				Slot sl = i1.next();
				if (index == 0) {
					sl.xDisplayPosition = 35;
					sl.yDisplayPosition = 36;
				} else if (sl instanceof DrawbridgeSlot) {
					sl.xDisplayPosition = -1000;
					sl.yDisplayPosition = -1000;
				}
				index++;
			}
		}
	}
	
	@Override
    public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer){
		if(gui != null && gui.containerNeglectMouse){
			return null;
		}else{
			return super.slotClick(par1, par2, par3, par4EntityPlayer);
		}
	}
}
