package tconstruct.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import tconstruct.blocks.logic.CraftingStationLogic;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.ToolCore;

public class CraftingStationContainer extends Container
{
    /** The crafting matrix inventory (3x3). */
    public InventoryCrafting craftMatrix;// = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult;// = new InventoryCraftResult();
    private CraftingStationLogic logic;
    private World worldObj;
    private int posX;
    private int posY;
    private int posZ;

    public CraftingStationContainer(InventoryPlayer inventorplayer, CraftingStationLogic logic, int x, int y, int z)
    {
        this.worldObj = logic.getWorld();
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.logic = logic;
        craftMatrix = new InventoryCraftingStation(this, 3, 3, logic);
        craftResult = new InventoryCraftingStationResult(logic);

        this.addSlotToContainer(new SlotCrafting(inventorplayer.player, this.craftMatrix, this.craftResult, 0, 124, 35));

        int row;
        int column;

        for (row = 0; row < 3; ++row)
        {
            for (column = 0; column < 3; ++column)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, column + row * 3, 30 + column * 18, 17 + row * 18));
            }
        }

        //Player Inventory
        for (row = 0; row < 3; ++row)
        {
            for (column = 0; column < 9; ++column)
            {
                this.addSlotToContainer(new Slot(inventorplayer, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (column = 0; column < 9; ++column)
        {
            this.addSlotToContainer(new Slot(inventorplayer, column, 8 + column * 18, 142));
        }

        //Side inventory
        if (logic.chest != null)
        {
            IInventory chest = logic.chest.get();
            IInventory doubleChest = logic.doubleChest == null ? null : logic.doubleChest.get();
            int count = 0;
            for (column = 0; column < 9; column++)
            {
                for (row = 0; row < 6; row++)
                {
                    int value = count < 27 ? count : count - 27;
                    this.addSlotToContainer(new Slot(count < 27 ? chest : doubleChest, value, -108 + row * 18, 19 + column * 18));
                    count++;
                    if (count >= 27 && doubleChest == null)
                        break;
                }
                if (count >= 27 && doubleChest == null)
                    break;
            }
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    public void onCraftMatrixChanged (IInventory par1IInventory)
    {
        ItemStack tool = modifyTool();
        if (tool != null)
            this.craftResult.setInventorySlotContents(0, tool);
        else
            this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
    }

    public ItemStack modifyTool ()
    {
        ItemStack input = craftMatrix.getStackInSlot(4);
        if (input != null)
        {
            Item item = input.getItem();
            if (item instanceof ToolCore)
            {
                ItemStack[] slots = new ItemStack[8];
                for (int i = 0; i < 4; i++)
                {
                    slots[i] = craftMatrix.getStackInSlot(i);
                    slots[i + 4] = craftMatrix.getStackInSlot(i + 5);
                }
                ItemStack output = ToolBuilder.instance.modifyTool(input, slots, "");
                if (output != null)
                    return output;
            }
            else if (item instanceof ArmorCore)
            {
                ItemStack[] slots = new ItemStack[8];
                for (int i = 0; i < 4; i++)
                {
                    slots[i] = craftMatrix.getStackInSlot(i);
                    slots[i + 4] = craftMatrix.getStackInSlot(i + 5);
                }
                ItemStack output = ToolBuilder.instance.modifyArmor(input, slots, "");
                if (output != null)
                    return output;
            }
        }
        return null;
    }

    public void onContainerClosed (EntityPlayer par1EntityPlayer)
    {
        super.onContainerClosed(par1EntityPlayer);

        if (!this.worldObj.isRemote)
        {
            for (int i = 0; i < 9; ++i)
            {
                ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);

                if (itemstack != null)
                {
                    par1EntityPlayer.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
    }

    public boolean canInteractWith (EntityPlayer par1EntityPlayer)
    {
        return par1EntityPlayer.getDistanceSq((double) this.posX + 0.5D, (double) this.posY + 0.5D, (double) this.posZ + 0.5D) <= 64.0D;
    }

    public ItemStack transferStackInSlot (EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 == 0)
            {
                if (!this.mergeItemStack(itemstack1, 10, 46, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (par2 >= 10 && par2 < 37)
            {
                if (!this.mergeItemStack(itemstack1, 37, 46, false))
                {
                    return null;
                }
            }
            else if (par2 >= 37 && par2 < 46)
            {
                if (!this.mergeItemStack(itemstack1, 10, 37, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 10, 46, false))
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

    public boolean func_94530_a (ItemStack par1ItemStack, Slot par2Slot)
    {
        return par2Slot.inventory != this.craftResult && super.func_94530_a(par1ItemStack, par2Slot);
    }
}
