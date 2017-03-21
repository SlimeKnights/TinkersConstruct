package tconstruct.tools.logic;

import mantle.blocks.abstracts.InventoryLogic;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import tconstruct.library.crafting.StencilBuilder;
import tconstruct.tools.inventory.PatternShaperContainer;

public class StencilTableLogic extends InventoryLogic implements ISidedInventory
{
    private ItemStack selectedStack;

    public StencilTableLogic()
    {
        super(2);
    }

    @Override
    public boolean canUpdate ()
    {
        return false;
    }

    @Override
    public String getDefaultName ()
    {
        return "toolstation.PatternShaper";
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new PatternShaperContainer(inventoryplayer, this);
    }

    /*@Override
    public void onInventoryChanged()
    {
    	if (inventory[0] == null)
    		inventory[1] = null;
    	super.onInventoryChanged();
    }*/

    public void setSelectedPattern (ItemStack stack)
    {
        selectedStack = stack;
        this.setInventorySlotContents(1, stack);
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        super.setInventorySlotContents(slot, itemstack);
        if (slot == 0 && itemstack != null && StencilBuilder.isBlank(itemstack))
        {
            setInventorySlotContents(1, selectedStack);
        }
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        if (slot == 1)
        {
            super.decrStackSize(0, 1);
            if (inventory[0] == null)
                return super.decrStackSize(slot, quantity);
            else
                return inventory[1].copy();
        }
        else
        {
            ItemStack ret = super.decrStackSize(slot, quantity);
            if (inventory[0] == null)
                super.decrStackSize(1, 1);
            return ret;
        }
    }

    public void altDecrStackSize (int slot, int quantity)
    {
        if (inventory[slot] != null)
        {
            if (inventory[slot].stackSize <= quantity)
            {
                ItemStack stack = inventory[slot];
                inventory[slot] = null;
                return;
            }
            ItemStack split = inventory[slot].splitStack(quantity);
            if (inventory[slot].stackSize == 0)
            {
                inventory[slot] = null;
            }
            return;
        }
    }

    @Override
    public boolean canDropInventorySlot (int slot)
    {
        if (slot == 0)
            return true;
        else
            return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide (int side)
    {
        return new int[0];
    }

    @Override
    public boolean canInsertItem (int i, ItemStack itemstack, int j)
    {
        return false;
    }

    @Override
    public boolean canExtractItem (int i, ItemStack itemstack, int j)
    {
        return false;
    }

    @Override
    public String getInventoryName ()
    {
        return StatCollector.translateToLocal("toolstation.PatternShaper");
    }

    @Override
    public boolean hasCustomInventoryName ()
    {
        return true;
    }

    @Override
    public void closeInventory ()
    {
    }

    @Override
    public void openInventory ()
    {
    }
}