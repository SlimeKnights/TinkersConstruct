package tconstruct.tools.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.event.ToolCraftedEvent;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.logic.CraftingStationLogic;

public class CraftingStationContainer extends Container
{
    /** The crafting matrix inventory (3x3). */
    public InventoryCrafting craftMatrix;// = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult;// = new InventoryCraftResult();
    private CraftingStationLogic logic;
    private World worldObj;
    EntityPlayer player;
    private int posX;
    private int posY;
    private int posZ;

    public CraftingStationContainer(InventoryPlayer inventorplayer, CraftingStationLogic logic, int x, int y, int z)
    {
        this.worldObj = logic.getWorldObj();
        this.player = inventorplayer.player;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.logic = logic;
        craftMatrix = new InventoryCraftingStation(this, 3, 3, logic);
        craftResult = new InventoryCraftingStationResult(logic);

        int row;
        int column;

        int craftingOffsetX = 30;
        int inventoryOffsetX = 8;

        if (logic.chest != null)
        {
            craftingOffsetX += 122;
            inventoryOffsetX += 122;
        }

        this.addSlotToContainer(new SlotCraftingStation(inventorplayer.player, this.craftMatrix, this.craftResult, 0, craftingOffsetX + 94, 35));

        for (row = 0; row < 3; ++row)
        {
            for (column = 0; column < 3; ++column)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, column + row * 3, craftingOffsetX + column * 18, 17 + row * 18));
            }
        }

        //Player Inventory
        for (row = 0; row < 3; ++row)
        {
            for (column = 0; column < 9; ++column)
            {
                this.addSlotToContainer(new Slot(inventorplayer, column + row * 9 + 9, inventoryOffsetX + column * 18, 84 + row * 18));
            }
        }

        for (column = 0; column < 9; ++column)
        {
            this.addSlotToContainer(new Slot(inventorplayer, column, inventoryOffsetX + column * 18, 142));
        }

        //Side inventory
        if (logic.chest != null)
        {
            IInventory firstChest = logic.chest.get();
            IInventory secondChest = logic.doubleChest == null ? null : logic.doubleChest.get();

            if (logic.doubleFirst && logic.doubleChest != null)
            {
                secondChest = logic.chest.get();
                firstChest = logic.doubleChest.get();
            }

            int count = 0;
            for (column = 0; column < 9; column++)
            {
                for (row = 0; row < 6; row++)
                {
                    int value = count < 27 ? count : count - 27;
                    this.addSlotToContainer(new Slot(count < 27 ? firstChest : secondChest, value, 14 + row * 18, 19 + column * 18));
                    count++;
                    if (count >= 27 && secondChest == null)
                        break;
                }
                if (count >= 27 && secondChest == null)
                    break;
            }
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    public void onCraftMatrixChanged (IInventory par1IInventory)
    {
        ItemStack tool = modifyItem();
        if (tool != null)
            this.craftResult.setInventorySlotContents(0, tool);
        else
            this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
    }

    public ItemStack modifyItem ()
    {
        ItemStack input = craftMatrix.getStackInSlot(4);
        if (input != null)
        {
            Item item = input.getItem();
            if (item instanceof IModifyable)
            {
                ItemStack[] slots = new ItemStack[8];
                for (int i = 0; i < 4; i++)
                {
                    slots[i] = craftMatrix.getStackInSlot(i);
                    slots[i + 4] = craftMatrix.getStackInSlot(i + 5);
                }
                ItemStack output = ModifyBuilder.instance.modifyItem(input, slots);
                if (output != null)
                    return output;
            }
        }
        return null;
    }

    @Override
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

    @Override
    public boolean canInteractWith (EntityPlayer player)
    {

        Block block = worldObj.getBlock(this.posX, this.posY, this.posZ);
        if (block != TinkerTools.craftingStationWood && block != TinkerTools.craftingSlabWood)
            return false;
        return player.getDistanceSq((double) this.posX + 0.5D, (double) this.posY + 0.5D, (double) this.posZ + 0.5D) <= 64.0D;
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
                if (itemstack.getItem() instanceof IModifyable)
                {
                    if (!this.mergeCraftedStack(itemstack1, logic.getSizeInventory(), this.inventorySlots.size(), true, par1EntityPlayer))
                    {
                        return null;
                    }
                }
                else
                {
                    if (!this.mergeItemStack(itemstack1, 10, 46, true))
                    {
                        return null;
                    }
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

    protected void craftTool (ItemStack stack)
    {
        if (stack.getItem() instanceof IModifyable)
        {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag(((IModifyable) stack.getItem()).getBaseTagName());
            Boolean full = (logic.getStackInSlot(2) != null || logic.getStackInSlot(3) != null);
            /*for (int i = 1; i <= 4; i++)
                logic.decrStackSize(i, 1);
            for (int i = 6; i <= 9; i++)
                logic.decrStackSize(i, 1);*/
            ItemStack compare = logic.getStackInSlot(5);
            int amount = compare.getItem() instanceof IModifyable ? compare.stackSize : 1;
            logic.decrStackSize(5, amount);
            if (!player.worldObj.isRemote && full)
                worldObj.playSoundEffect(logic.xCoord, logic.yCoord, logic.zCoord, "tinker:little_saw", 1.0F, (AbilityHelper.random.nextFloat() - AbilityHelper.random.nextFloat()) * 0.2F + 1.0F);
            MinecraftForge.EVENT_BUS.post(new ToolCraftedEvent(this.logic, player, stack));
        }
    }

    protected boolean mergeCraftedStack (ItemStack stack, int slotsStart, int slotsTotal, boolean playerInventory, EntityPlayer player)
    {
        boolean failedToMerge = false;
        int slotIndex = slotsStart;

        if (playerInventory)
        {
            slotIndex = slotsTotal - 1;
        }

        Slot otherInventorySlot;
        ItemStack copyStack = null;

        if (stack.stackSize > 0)
        {
            if (playerInventory)
            {
                slotIndex = slotsTotal - 1;
            }
            else
            {
                slotIndex = slotsStart;
            }

            while (!playerInventory && slotIndex < slotsTotal || playerInventory && slotIndex >= slotsStart)
            {
                otherInventorySlot = (Slot) this.inventorySlots.get(slotIndex);
                copyStack = otherInventorySlot.getStack();

                if (copyStack == null)
                {
                    craftTool(stack);
                    otherInventorySlot.putStack(stack.copy());
                    otherInventorySlot.onSlotChanged();
                    stack.stackSize = 0;
                    failedToMerge = true;
                    break;
                }

                if (playerInventory)
                {
                    --slotIndex;
                }
                else
                {
                    ++slotIndex;
                }
            }
        }

        return failedToMerge;
    }

    public boolean func_94530_a (ItemStack par1ItemStack, Slot par2Slot)
    {
        return par2Slot.inventory != this.craftResult && super.func_94530_a(par1ItemStack, par2Slot);
    }
}
