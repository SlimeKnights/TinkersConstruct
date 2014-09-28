package tconstruct.mechworks.inventory;

import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import tconstruct.armor.inventory.*;
import tconstruct.mechworks.TinkerMechworks;
import tconstruct.mechworks.logic.TileEntityLandmine;

/**
 * 
 * @author fuj1n
 * 
 */
public class ContainerLandmine extends Container
{

    public TileEntityLandmine te;
    public EntityPlayer entityPlayer;
    private int field_94536_g;
    private int field_94535_f = -1;
    private final Set field_94537_h = new HashSet();

    public ContainerLandmine(EntityPlayer player, TileEntityLandmine te)
    {
        this.te = te;
        this.entityPlayer = player;
        addInventorySlots(te);
        bindPlayerInventory(player.inventory);
    }

    public void addInventorySlots (TileEntityLandmine inv)
    {
        addSlotToContainer(new SlotBehavedOnly(inv, 0, 35, 53));
        addSlotToContainer(new SlotBehavedOnly(inv, 1, 80, 53));
        addSlotToContainer(new SlotBehavedOnly(inv, 2, 125, 53));
        addSlotToContainer(new SlotOpaqueBlocksOnly(inv, 3, 80, 21));
    }

    public void bindPlayerInventory (InventoryPlayer inventory)
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith (EntityPlayer entityplayer)
    {
        Block block = te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord);
        if (block != TinkerMechworks.landmine)
            return false;
        return te.isUseableByPlayer(entityplayer);
    }

    @Override
    public ItemStack transferStackInSlot (EntityPlayer par1EntityPlayer, int par2)
    {
        // This code actually works perfectly, but does not respect the global
        // TileEntity stack limit, shift clicking ability disabled.
        /*
         * ItemStack itemstack = null; Slot slot = (Slot)
         * this.inventorySlots.get(par2);
         * 
         * if (slot != null && slot.getHasStack()) { ItemStack itemstack1 =
         * slot.getStack(); itemstack = itemstack1.copy();
         * 
         * if (par2 == 2) { if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
         * return null; }
         * 
         * slot.onSlotChange(itemstack1, itemstack); } else if (par2 != 1 &&
         * par2 != 0) { if (!this.mergeItemStack(itemstack1, 0, 1, false)) { if
         * (!this.mergeItemStack(itemstack1, 1, 2, false)) { if
         * (!this.mergeItemStack(itemstack1, 30, 39, false)) { return null; }
         * else if (par2 >= 30 && par2 < 39 && !this.mergeItemStack(itemstack1,
         * 3, 30, false)) { return null; } } } } else if
         * (!this.mergeItemStack(itemstack1, 3, 39, false)) { return null; }
         * 
         * if (itemstack1.stackSize == 0) { slot.putStack((ItemStack) null); }
         * else { slot.onSlotChanged(); }
         * 
         * if (itemstack1.stackSize == itemstack.stackSize) { return null; }
         * 
         * slot.onPickupFromSlot(par1EntityPlayer, itemstack1); }
         * 
         * return itemstack;
         */
        return null;
    }

    /* Disabled for another(less buggy and sticky) system */
    @Override
    public ItemStack slotClick (int par1, int par2, int par3, EntityPlayer par4EntityPlayer)
    {
        ItemStack itemstack = null;
        InventoryPlayer inventoryplayer = par4EntityPlayer.inventory;
        int l;
        ItemStack itemstack1;

        if (par3 == 5)
        {
            int i1 = this.field_94536_g;
            this.field_94536_g = func_94532_c(par2);

            if ((i1 != 1 || this.field_94536_g != 2) && i1 != this.field_94536_g)
            {
                this.func_94533_d();
            }
            else if (inventoryplayer.getItemStack() == null)
            {
                this.func_94533_d();
            }
            else if (this.field_94536_g == 0)
            {
                this.field_94535_f = func_94529_b(par2);

                if (func_94528_d(this.field_94535_f))
                {
                    this.field_94536_g = 1;
                    this.field_94537_h.clear();
                }
                else
                {
                    this.func_94533_d();
                }
            }
            else if (this.field_94536_g == 1)
            {
                Slot slot = (Slot) this.inventorySlots.get(par1);

                if (slot != null && func_94527_a(slot, inventoryplayer.getItemStack(), true) && slot.isItemValid(inventoryplayer.getItemStack()) && inventoryplayer.getItemStack().stackSize > this.field_94537_h.size() && this.canDragIntoSlot(slot))
                {
                    this.field_94537_h.add(slot);
                }
            }
            else if (this.field_94536_g == 2)
            {
                if (!this.field_94537_h.isEmpty())
                {
                    itemstack1 = inventoryplayer.getItemStack().copy();
                    l = inventoryplayer.getItemStack().stackSize;
                    Iterator iterator = this.field_94537_h.iterator();

                    while (iterator.hasNext())
                    {
                        Slot slot1 = (Slot) iterator.next();

                        if (slot1 != null && func_94527_a(slot1, inventoryplayer.getItemStack(), true) && slot1.isItemValid(inventoryplayer.getItemStack()) && inventoryplayer.getItemStack().stackSize >= this.field_94537_h.size() && this.canDragIntoSlot(slot1))
                        {
                            ItemStack itemstack2 = itemstack1.copy();
                            int j1 = slot1.getHasStack() ? slot1.getStack().stackSize : 0;
                            func_94525_a(this.field_94537_h, this.field_94535_f, itemstack2, j1);

                            if (itemstack2.stackSize > itemstack2.getMaxStackSize())
                            {
                                itemstack2.stackSize = itemstack2.getMaxStackSize();
                            }

                            if (itemstack2.stackSize > slot1.getSlotStackLimit())
                            {
                                itemstack2.stackSize = slot1.getSlotStackLimit();
                            }

                            l -= itemstack2.stackSize - j1;
                            slot1.putStack(itemstack2);
                        }
                    }

                    itemstack1.stackSize = l;

                    if (itemstack1.stackSize <= 0)
                    {
                        itemstack1 = null;
                    }

                    inventoryplayer.setItemStack(itemstack1);
                }

                this.func_94533_d();
            }
            else
            {
                this.func_94533_d();
            }
        }
        else if (this.field_94536_g != 0)
        {
            this.func_94533_d();
        }
        else
        {
            Slot slot2;
            int k1;
            ItemStack itemstack3;

            if ((par3 == 0 || par3 == 1) && (par2 == 0 || par2 == 1))
            {
                if (par1 == -999)
                {
                    if (inventoryplayer.getItemStack() != null && par1 == -999)
                    {
                        if (par2 == 0)
                        {
                            par4EntityPlayer.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack(), false);
                            inventoryplayer.setItemStack((ItemStack) null);
                        }

                        if (par2 == 1)
                        {
                            par4EntityPlayer.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack().splitStack(1), false);

                            if (inventoryplayer.getItemStack().stackSize == 0)
                            {
                                inventoryplayer.setItemStack((ItemStack) null);
                            }
                        }
                    }
                }
                else if (par3 == 1)
                {
                    if (par1 < 0)
                    {
                        return null;
                    }

                    Object localObject2;

                    slot2 = (Slot) this.inventorySlots.get(par1);

                    if (slot2 != null && slot2.canTakeStack(par4EntityPlayer))
                    {
                        itemstack1 = this.transferStackInSlot(par4EntityPlayer, par1);

                        if (itemstack1 != null)
                        {
                            localObject2 = ((ItemStack) itemstack1).getItem();
                            itemstack = itemstack1.copy();

                            if (slot2 != null && slot2.getStack() != null && slot2.getStack().getItem() == localObject2)
                            {
                                this.retrySlotClick(par1, par2, true, par4EntityPlayer);
                            }
                        }
                    }
                }
                else
                {
                    if (par1 < 0)
                    {
                        return null;
                    }

                    slot2 = (Slot) this.inventorySlots.get(par1);

                    boolean shouldDoStuff = true;

                    if ((!(slot2 instanceof SlotBehavedOnly)))
                    {
                        shouldDoStuff = false;
                    }

                    if (slot2 != null)
                    {
                        itemstack1 = slot2.getStack();
                        ItemStack itemstack4 = inventoryplayer.getItemStack();

                        if (itemstack1 != null)
                        {
                            itemstack = itemstack1.copy();
                        }

                        if (itemstack1 == null)
                        {
                            if (itemstack4 != null && slot2.isItemValid(itemstack4))
                            {
                                k1 = par2 == 0 ? itemstack4.stackSize : 1;

                                if (k1 > slot2.getSlotStackLimit())
                                {
                                    k1 = slot2.getSlotStackLimit();
                                }

                                if (itemstack4.stackSize >= k1)
                                {
                                    slot2.putStack(itemstack4.splitStack(k1));
                                }

                                if (itemstack4.stackSize == 0)
                                {
                                    inventoryplayer.setItemStack((ItemStack) null);
                                }
                            }
                            else if (shouldDoStuff && slot2 instanceof SlotBehavedOnly && itemstack4 != null && slot2.isItemValid(new ItemStack(itemstack4.getItem(), 1, itemstack4.getItemDamage())))
                            {
                                k1 = par2 == 0 ? 1 : 1;

                                if (k1 > slot2.getSlotStackLimit())
                                {
                                    k1 = slot2.getSlotStackLimit();
                                }

                                if (itemstack4.stackSize >= k1)
                                {
                                    slot2.putStack(itemstack4.splitStack(k1));
                                }

                                if (itemstack4.stackSize == 0)
                                {
                                    inventoryplayer.setItemStack((ItemStack) null);
                                }
                            }
                        }
                        else if (slot2.canTakeStack(par4EntityPlayer))
                        {
                            if (itemstack4 == null)
                            {
                                k1 = par2 == 0 ? itemstack1.stackSize : (itemstack1.stackSize + 1) / 2;
                                itemstack3 = slot2.decrStackSize(k1);
                                inventoryplayer.setItemStack(itemstack3);

                                if (itemstack1.stackSize == 0)
                                {
                                    slot2.putStack((ItemStack) null);
                                }

                                slot2.onPickupFromSlot(par4EntityPlayer, inventoryplayer.getItemStack());
                            }
                            else if (slot2.isItemValid(itemstack4))
                            {
                                if (ItemStack.areItemStacksEqual(itemstack1, itemstack4))
                                {
                                    k1 = par2 == 0 ? itemstack4.stackSize : 1;

                                    if (k1 > slot2.getSlotStackLimit() - itemstack1.stackSize)
                                    {
                                        k1 = slot2.getSlotStackLimit() - itemstack1.stackSize;
                                    }

                                    if (k1 > itemstack4.getMaxStackSize() - itemstack1.stackSize)
                                    {
                                        k1 = itemstack4.getMaxStackSize() - itemstack1.stackSize;
                                    }

                                    itemstack4.splitStack(k1);

                                    if (itemstack4.stackSize == 0)
                                    {
                                        inventoryplayer.setItemStack((ItemStack) null);
                                    }

                                    itemstack1.stackSize += k1;
                                }
                                else if (itemstack4.stackSize <= slot2.getSlotStackLimit())
                                {
                                    slot2.putStack(itemstack4);
                                    inventoryplayer.setItemStack(itemstack1);
                                }
                            }
                            else if (ItemStack.areItemStacksEqual(itemstack1, itemstack4))
                            {
                                k1 = itemstack1.stackSize;

                                if (k1 > 0 && k1 + itemstack4.stackSize <= itemstack4.getMaxStackSize())
                                {
                                    itemstack4.stackSize += k1;
                                    itemstack1 = slot2.decrStackSize(k1);

                                    if (itemstack1.stackSize == 0)
                                    {
                                        slot2.putStack((ItemStack) null);
                                    }

                                    slot2.onPickupFromSlot(par4EntityPlayer, inventoryplayer.getItemStack());
                                }
                            }
                        }

                        slot2.onSlotChanged();
                    }
                }
            }
            else if (par3 == 2 && par2 >= 0 && par2 < 9)
            {
                slot2 = (Slot) this.inventorySlots.get(par1);

                if (slot2.canTakeStack(par4EntityPlayer))
                {
                    itemstack1 = inventoryplayer.getStackInSlot(par2);
                    boolean flag = itemstack1 == null || slot2.inventory == inventoryplayer && slot2.isItemValid(itemstack1);
                    k1 = -1;

                    if (!flag)
                    {
                        k1 = inventoryplayer.getFirstEmptyStack();
                        flag |= k1 > -1;
                    }

                    if (slot2.getHasStack() && flag)
                    {
                        itemstack3 = slot2.getStack();
                        inventoryplayer.setInventorySlotContents(par2, itemstack3.copy());

                        if ((slot2.inventory != inventoryplayer || !slot2.isItemValid(itemstack1)) && itemstack1 != null)
                        {
                            if (k1 > -1)
                            {
                                inventoryplayer.addItemStackToInventory(itemstack1);
                                slot2.decrStackSize(itemstack3.stackSize);
                                slot2.putStack((ItemStack) null);
                                slot2.onPickupFromSlot(par4EntityPlayer, itemstack3);
                            }
                        }
                        else
                        {
                            slot2.decrStackSize(itemstack3.stackSize);
                            slot2.putStack(itemstack1);
                            slot2.onPickupFromSlot(par4EntityPlayer, itemstack3);
                        }
                    }
                    else if (!slot2.getHasStack() && itemstack1 != null && slot2.isItemValid(itemstack1))
                    {
                        inventoryplayer.setInventorySlotContents(par2, (ItemStack) null);
                        slot2.putStack(itemstack1);
                    }
                }
            }
            else if (par3 == 3 && par4EntityPlayer.capabilities.isCreativeMode && inventoryplayer.getItemStack() == null && par1 >= 0)
            {
                slot2 = (Slot) this.inventorySlots.get(par1);

                if (slot2 != null && slot2.getHasStack())
                {
                    itemstack1 = slot2.getStack().copy();
                    itemstack1.stackSize = itemstack1.getMaxStackSize();
                    inventoryplayer.setItemStack(itemstack1);
                }
            }
            else if (par3 == 4 && inventoryplayer.getItemStack() == null && par1 >= 0)
            {
                slot2 = (Slot) this.inventorySlots.get(par1);

                if (slot2 != null && slot2.getHasStack() && slot2.canTakeStack(par4EntityPlayer))
                {
                    itemstack1 = slot2.decrStackSize(par2 == 0 ? 1 : slot2.getStack().stackSize);
                    slot2.onPickupFromSlot(par4EntityPlayer, itemstack1);
                    par4EntityPlayer.dropPlayerItemWithRandomChoice(itemstack1, false);
                }
            }
            else if (par3 == 6 && par1 >= 0)
            {
                slot2 = (Slot) this.inventorySlots.get(par1);
                itemstack1 = inventoryplayer.getItemStack();

                if (itemstack1 != null && (slot2 == null || !slot2.getHasStack() || !slot2.canTakeStack(par4EntityPlayer)))
                {
                    l = par2 == 0 ? 0 : this.inventorySlots.size() - 1;
                    k1 = par2 == 0 ? 1 : -1;

                    for (int l1 = 0; l1 < 2; ++l1)
                    {
                        for (int i2 = l; i2 >= 0 && i2 < this.inventorySlots.size() && itemstack1.stackSize < itemstack1.getMaxStackSize(); i2 += k1)
                        {
                            Slot slot3 = (Slot) this.inventorySlots.get(i2);

                            if (slot3.getHasStack() && func_94527_a(slot3, itemstack1, true) && slot3.canTakeStack(par4EntityPlayer) && this.func_94530_a(itemstack1, slot3) && (l1 != 0 || slot3.getStack().stackSize != slot3.getStack().getMaxStackSize()))
                            {
                                int j2 = Math.min(itemstack1.getMaxStackSize() - itemstack1.stackSize, slot3.getStack().stackSize);
                                ItemStack itemstack5 = slot3.decrStackSize(j2);
                                itemstack1.stackSize += j2;

                                if (itemstack5.stackSize <= 0)
                                {
                                    slot3.putStack((ItemStack) null);
                                }

                                slot3.onPickupFromSlot(par4EntityPlayer, itemstack5);
                            }
                        }
                    }
                }

                this.detectAndSendChanges();
            }
        }

        return itemstack;
    }

    @Override
    public boolean canDragIntoSlot (Slot par1Slot)
    {
        return false;
    }

}
