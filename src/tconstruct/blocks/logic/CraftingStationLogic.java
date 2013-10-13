package tconstruct.blocks.logic;

import net.minecraft.item.ItemStack;

import net.minecraft.inventory.ISidedInventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import tconstruct.inventory.CraftingStationContainer;
import tconstruct.library.blocks.InventoryLogic;

public class CraftingStationLogic extends InventoryLogic implements ISidedInventory
{
    public CraftingStationLogic()
    {
        super(11); //9 for crafting, 1 for output, 1 for plans
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new CraftingStationContainer(inventoryplayer, this, x, y, z);
    }

    @Override
    protected String getDefaultName ()
    {
        return "crafters.craftingstation";
    }

    public boolean canDropInventorySlot (int slot)
    {
        if (slot == 0)
            return false;
        return true;
    }

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return i != 0;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return i != 0;
	}
}
