package tconstruct.tools.inventory;

import com.google.common.primitives.Ints;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.modifier.IModifyable;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.gui.ChestSlot;
import tconstruct.tools.logic.CraftingStationLogic;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class CraftingStationContainer extends Container {
    private final World worldObj;
    private final int posX;
    private final int posY;
    private final int posZ;
    
    @SuppressWarnings("rawtypes")
    private final WeakReference [] inventories;
    
    /**
     * The crafting matrix inventory (3x3).
     */
    public InventoryCrafting craftMatrix;
    public IInventory craftResult;
    public CraftingStationLogic logic;
    EntityPlayer player;

    public CraftingStationContainer(InventoryPlayer inventoryplayer, CraftingStationLogic logic, int x, int y, int z) {
        this.worldObj = logic.getWorldObj();
        this.player = inventoryplayer.player;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.logic = logic;
        craftMatrix = new InventoryCraftingStation(this, 3, 3, logic);
        craftResult = new InventoryCraftingStationResult(logic);
        this.inventories = logic.getInventories();

        int row, col;

        int bothOffset = 0;
        
        if (logic.chest != null) {
            if(logic.slotCount > 54)
                bothOffset += 12; // SlideBar.width

            bothOffset += 122;
        }
        final int craftingOffsetX = 30 + bothOffset;
        final int inventoryOffsetX = 8 + bothOffset;
        
        
        // 0 - crafting slot
        this.addSlotToContainer(new SlotCraftingStation(inventoryplayer.player, this.craftMatrix, this.craftResult, 0, craftingOffsetX + 94, 35));

        // 1 - 9 - Crafting Matrix
        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 3; ++col) {
                this.addSlotToContainer(new Slot(this.craftMatrix, col + row * 3, craftingOffsetX + col * 18, 17 + row * 18));
            }
        }

        // Player Inventory 10 - 36
        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(inventoryplayer, col + row * 9 + 9, inventoryOffsetX + col * 18, 84 + row * 18));
            }
        }
        // Player Hotbar - 37 - 45
        for (col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(inventoryplayer, col, inventoryOffsetX + col * 18, 142));
        }

        // Side inventory - 46+
        if (logic.chest != null) {
            IInventory inv = logic.getFirstInventory();
            IInventory secondInv = logic.getSecondInventory();

            final Set<Integer> accessibleSlots = inv instanceof ISidedInventory ? new HashSet<>(Ints.asList(((ISidedInventory) inv).getAccessibleSlotsFromSide(logic.chestDirection.getOpposite().ordinal()))) : null;
            
            int index = 0, curIndex = 0;
            IInventory curInv;
            final int invSize = inv.getSizeInventory() * (secondInv != null ? 2 : 1); 
            for (row = 0; row < logic.invRows; row++) {
                for (col = 0; col < logic.invColumns; col++) {
                    if(index >= invSize) break;
                    // Adjust the inventory to account for double chests
                    curInv = secondInv != null && index >= 27 ? secondInv : inv;
                    // Adjust the index for the inventory
                    curIndex = secondInv != null && index >= 27 ? index - 27 : index;
                    
                    if(accessibleSlots == null || accessibleSlots.contains(curIndex)) {
                        this.addSlotToContainer(new ChestSlot(curInv, curIndex, 8 + col * 18, 19 + row * 18));
                    }
                    index++;
                }
            }
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    public ItemStack modifyItem() {
        ItemStack input = craftMatrix.getStackInSlot(4);
        if (input != null) {
            Item item = input.getItem();
            if (item instanceof IModifyable) {
                ItemStack[] slots = new ItemStack[8];
                for (int i = 0; i < 4; i++) {
                    slots[i] = craftMatrix.getStackInSlot(i);
                    slots[i + 4] = craftMatrix.getStackInSlot(i + 5);
                }
                return ModifyBuilder.instance.modifyItem(input, slots);
            }
        }
        return null;
    }

    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int index) {
        Slot slot = (Slot) this.inventorySlots.get(index);
        
        if(slot == null || !slot.getHasStack()) {
            return null;
        }
        
        ItemStack ret = slot.getStack().copy();
        ItemStack itemstack = slot.getStack().copy();

        boolean nothingDone = true;
        
        if (index == 0) {
            // Crafting Result
            if (ret.getItem() instanceof IModifyable) {
                nothingDone &= this.mergeCraftedStack(itemstack, logic.getSizeInventory(), this.inventorySlots.size(), true, entityPlayer);
            } else {
                // First refill the attached chests
                nothingDone &= this.refillChest(itemstack);

                // Then try moving to player inventory
                nothingDone &= moveToPlayerInventory(itemstack);
            }

            slot.onSlotChange(itemstack, ret);
        } else if(index >= 1 && index < 10) { // From Crafting Grid
            // First refill the attached chests
            nothingDone &= this.refillChest(itemstack);
            
            // Then try moving to player inventory
            nothingDone &= moveToPlayerInventory(itemstack);
        }
        else if (index >= 10 && index < 46) { // From Player Inv or Hotbar
            // First to the crafting Matrix
            nothingDone &= moveToCraftingGrid(itemstack);
            
            // Then to any attached chest
            nothingDone &= this.moveToChest(itemstack);
        } else {  // From the Attached Chests
            // First to the crafting Matrix
            nothingDone &= moveToCraftingGrid(itemstack);

            // Then To Player Inv or Hotbar
            nothingDone &= moveToPlayerInventory(itemstack);
        }

        if(nothingDone) {
            return null;
        }
        
        if (itemstack.stackSize == 0) {
            slot.putStack(null);
        } else {
            slot.onSlotChanged();
        }

        if (itemstack.stackSize == ret.stackSize) {
            return null;
        }

        slot.onPickupFromSlot(entityPlayer, itemstack);

        return ret;
    }
    protected boolean refillChest(ItemStack itemstack) {
        if(itemstack == null || itemstack.stackSize <= 0 || logic.slotCount == 0) return false;
        
        return !this.mergeItemStackRefill(itemstack, 46, 46 + logic.slotCount, false);
    }
    
    protected boolean moveToChest(ItemStack itemstack) {
        if(itemstack == null || itemstack.stackSize <= 0 || logic.slotCount == 0) return false;
        
        return !this.mergeItemStack(itemstack, 46, 46 + logic.slotCount, false);
    }
  
    protected boolean moveToPlayerInventory(ItemStack itemstack) {
        if(itemstack == null || itemstack.stackSize <= 0) return false;
        
        return !this.mergeItemStack(itemstack, 10, 46, false);
    }
    
    protected boolean moveToCraftingGrid(ItemStack itemstack) {
        if(itemstack == null || itemstack.stackSize <= 0) return false;
        
        return !this.mergeItemStack(itemstack, 1, 10, true);
    }

    public boolean func_94530_a/*canMergeSlot*/(ItemStack par1ItemStack, Slot par2Slot) {
        return par2Slot.inventory != this.craftResult && super.func_94530_a(par1ItemStack, par2Slot);
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);

        if (!this.worldObj.isRemote) {
            for (int i = 0; i < 9; ++i) {
                ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);

                if (itemstack != null) {
                    par1EntityPlayer.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
    }

    public void onCraftMatrixChanged(IInventory par1IInventory) {
        ItemStack tool = modifyItem();
        if (tool != null)
            this.craftResult.setInventorySlotContents(0, tool);
        else
            this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        Block block = worldObj.getBlock(this.posX, this.posY, this.posZ);
        if (block != TinkerTools.craftingStationWood && block != TinkerTools.craftingSlabWood)
            return false;

        if (!this.logic.isUseableByPlayer(player) || !CraftingStationLogic.isUseableByPlayer(player, this.inventories))
            return false;

        return player.getDistanceSq((double) this.posX + 0.5D, (double) this.posY + 0.5D, (double) this.posZ + 0.5D) <= 64.0D;
    }

    protected boolean mergeCraftedStack(ItemStack stack, int slotsStart, int slotsTotal, boolean playerInventory, EntityPlayer player) {
        boolean failedToMerge = false;
        int slotIndex = slotsStart;

        if (playerInventory) {
            slotIndex = slotsTotal - 1;
        }

        Slot otherInventorySlot;
        ItemStack copyStack;

        if (stack.stackSize > 0) {
            while (!playerInventory && slotIndex < slotsTotal || playerInventory && slotIndex >= slotsStart) {
                otherInventorySlot = (Slot) this.inventorySlots.get(slotIndex);
                copyStack = otherInventorySlot.getStack();

                if (copyStack == null) {
                    otherInventorySlot.putStack(stack.copy());
                    otherInventorySlot.onSlotChanged();
                    stack.stackSize = 0;
                    failedToMerge = true;
                    break;
                }

                if (playerInventory) {
                    --slotIndex;
                } else {
                    ++slotIndex;
                }
            }
        }

        return failedToMerge;
    }

    @Override
    protected boolean mergeItemStack(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
        boolean ret = mergeItemStackRefill(stack, startIndex, endIndex, useEndIndex);
        if(stack.stackSize > 0) {
            ret |= mergeItemStackMove(stack, startIndex, endIndex, useEndIndex);
        }
        return ret;
    }

    // only refills items that are already present
    protected boolean mergeItemStackRefill(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
        if(stack.stackSize <= 0) {
            return false;
        }

        boolean didSomething = false;
        int k = useEndIndex ? endIndex - 1 : startIndex;


        Slot slot;
        ItemStack itemstack1;

        if(stack.isStackable()) {
            while(stack.stackSize > 0 && (!useEndIndex && k < endIndex || useEndIndex && k >= startIndex)) {
                slot = (Slot)this.inventorySlots.get(k);
                itemstack1 = slot.getStack();

                if(itemstack1 != null
                    && itemstack1.getItem() == stack.getItem()
                    && (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage())
                    && ItemStack.areItemStackTagsEqual(stack, itemstack1)
                    && this.func_94530_a/*canMergeSlot*/(stack, slot)) {
                    int l = itemstack1.stackSize + stack.stackSize;
                    int limit = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());

                    if(l <= limit) {
                        stack.stackSize = 0;
                        itemstack1.stackSize = l;
                        slot.onSlotChanged();
                        didSomething = true;
                    }
                    else if(itemstack1.stackSize < limit) {
                        stack.stackSize -= (limit - itemstack1.stackSize);
                        itemstack1.stackSize = limit;
                        slot.onSlotChanged();
                        didSomething = true;
                    }
                }
                
                if(useEndIndex) --k;
                else            ++k;
            }
        }

        return didSomething;
    }

    // only moves items into empty slots
    protected boolean mergeItemStackMove(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
        if(stack.stackSize <= 0) {
            return false;
        }

        boolean didSomething = false;
        int k = useEndIndex ? endIndex - 1 : startIndex;

        while(!useEndIndex && k < endIndex || useEndIndex && k >= startIndex) {
            final Slot slot = (Slot)this.inventorySlots.get(k);
            ItemStack itemstack1 = slot.getStack();

            if((itemstack1 == null || itemstack1.stackSize == 0) && slot.isItemValid(stack) && this.func_94530_a/*canMergeSlot*/(stack, slot)) {
                // Forge: Make sure to respect isItemValid in the slot.
                int limit = slot.getSlotStackLimit();
                ItemStack stack2 = stack.copy();
                if(stack2.stackSize > limit) {
                    stack2.stackSize = limit;
                    stack.stackSize -= limit;
                }
                else {
                    stack.stackSize = 0;
                }
                slot.putStack(stack2);
                slot.onSlotChanged();
                didSomething = true;

                if(stack.stackSize <= 0) {
                    break;
                }
            }

            if(useEndIndex) --k;
            else            ++k;
        }
        
        return didSomething;
    }
    
}

