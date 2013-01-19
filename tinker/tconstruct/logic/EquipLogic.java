package tinker.tconstruct.logic;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import tinker.common.IActiveLogic;
import tinker.common.InventoryLogic;
import cpw.mods.fml.common.registry.GameRegistry;

/* Slots
 * 0: Frying pan item
 * 1: Fuel
 * 2-9: Food
 */

public abstract class EquipLogic extends InventoryLogic 
{
	
	public EquipLogic(int invSize)
	{
		super(invSize);
	}

	public void setEquipmentItem(ItemStack stack)
	{
		inventory[0] = stack.copy();
	}
	
	public boolean hasEquipmentItem()
	{
		return inventory[0] != null;
	}
	
	public ItemStack getEquipmentItem()
	{
		return inventory[0];
	}
	
	/*@Override
	public ItemStack getStackInSlot(int slot)
    {
        return inventory[slot +1];
    }
    
    public boolean isStackInSlot(int slot)
    {
    	return inventory[slot +1] != null;
    }
	
    @Override
	public int getSizeInventory()
    {
        return inventory.length - 1;
    }*/
}
