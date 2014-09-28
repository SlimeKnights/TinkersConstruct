package tconstruct.tools.inventory;

import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import tconstruct.tools.logic.FurnaceLogic;

public class FurnaceContainer extends Container
{
    private FurnaceLogic furnace;
    private int lastCookTime;
    private int lastBurnTime;
    private int lastItemBurnTime;

    public FurnaceContainer(InventoryPlayer par1InventoryPlayer, FurnaceLogic par2TileEntityFurnace)
    {
        this.furnace = par2TileEntityFurnace;
        this.addSlotToContainer(new Slot(par2TileEntityFurnace, 0, 56, 17));
        this.addSlotToContainer(new Slot(par2TileEntityFurnace, 1, 56, 53));
        this.addSlotToContainer(new SlotFurnace(par1InventoryPlayer.player, par2TileEntityFurnace, 2, 116, 35));
        int i;

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void addCraftingToCrafters (ICrafting par1ICrafting)
    {
        super.addCraftingToCrafters(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 0, this.furnace.progress);
        par1ICrafting.sendProgressBarUpdate(this, 1, this.furnace.fuel);
        par1ICrafting.sendProgressBarUpdate(this, 2, this.furnace.fuelGague);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    @Override
    public void detectAndSendChanges ()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting) this.crafters.get(i);

            if (this.lastCookTime != this.furnace.progress)
            {
                icrafting.sendProgressBarUpdate(this, 0, this.furnace.progress);
            }

            if (this.lastBurnTime != this.furnace.fuel)
            {
                icrafting.sendProgressBarUpdate(this, 1, this.furnace.fuel);
            }

            if (this.lastItemBurnTime != this.furnace.fuelGague)
            {
                icrafting.sendProgressBarUpdate(this, 2, this.furnace.fuelGague);
            }
        }

        this.lastCookTime = this.furnace.progress;
        this.lastBurnTime = this.furnace.fuel;
        this.lastItemBurnTime = this.furnace.fuelGague;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar (int par1, int par2)
    {
        if (par1 == 0)
        {
            this.furnace.progress = par2;
        }

        if (par1 == 1)
        {
            this.furnace.fuel = par2;
        }

        if (par1 == 2)
        {
            this.furnace.fuelGague = par2;
        }
    }

    @Override
    public boolean canInteractWith (EntityPlayer par1EntityPlayer)
    {
        return this.furnace.isUseableByPlayer(par1EntityPlayer);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or
     * you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot (EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 == 2)
            {
                if (!this.mergeItemStack(itemstack1, 3, 39, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (par2 != 1 && par2 != 0)
            {
                if (FurnaceRecipes.smelting().getSmeltingResult(itemstack1) != null)
                {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return null;
                    }
                }
                else if (TileEntityFurnace.isItemFuel(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false))
                    {
                        return null;
                    }
                }
                else if (par2 >= 3 && par2 < 30)
                {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false))
                    {
                        return null;
                    }
                }
                else if (par2 >= 30 && par2 < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 3, 39, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack) null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }

        return itemstack;
    }
}
