package tconstruct.tools.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.common.inventory.SlotRestrictedItem;
import tconstruct.library.mantle.InventoryCraftingPersistent;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.tileentity.TilePatternChest;
import tconstruct.tools.tileentity.TileStencilTable;

public class ContainerStencilTable extends ContainerMultiModule<TileStencilTable> {

  public InventoryCraftingPersistent craftMatrix;
  public IInventory craftResult;

  private final Container patternChestSideInventory;

  public ContainerStencilTable(InventoryPlayer playerInventory, TileStencilTable tile) {
    super(tile);

    this.craftMatrix = new InventoryCraftingPersistent(this, tile, 1, 1);
    this.craftResult = new InventoryCraftResult();

    this.addSlotToContainer(new SlotRestrictedItem(TinkerTools.pattern, this.craftMatrix, 0, 48, 35));
    this.addSlotToContainer(new SlotStencilCrafting(playerInventory.player, craftMatrix, craftResult, 1, 106, 35));

    TilePatternChest chest = detectTE(TilePatternChest.class);
    // TE present?
    if(chest != null) {
      patternChestSideInventory = new ContainerPatternChest.SideInventory(chest, chest, 6 + 176, 8, 6);
      addSubContainer(patternChestSideInventory, true);
    }
    else {
      patternChestSideInventory = null;
    }

    this.addPlayerInventory(playerInventory, 8, 84);
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    updateResult();
  }

  // Sets the result in the output slot depending on if there's a pattern in the input and on which pattern was selected
  public void updateResult() {
    // no pattern :(
    if(craftMatrix.getStackInSlot(0) == null) {
      craftResult.setInventorySlotContents(0, null);
    }
    else {
      // set pattern from selection (or null if no selection)
      craftResult.setInventorySlotContents(0, craftMatrix.getStackInSlot(0).copy());
    }
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
    // we always want to shiftclick the output into a pattern chest if present
    // note that we leave out a few checks/callbacks because we rely on how the stenciltable works
    if(index != 1) {
      return super.transferStackInSlot(playerIn, index);
    }

    Slot slot = (Slot) this.inventorySlots.get(index);
    if(slot == null || !slot.getHasStack()) {
      return null;
    }

    ItemStack itemstack = slot.getStack().copy();
    ItemStack ret = slot.getStack().copy();

    if(patternChestSideInventory != null) {
      if(moveToContainer(itemstack, patternChestSideInventory)) {
        return null;
      }

      return notifySlotAfterTransfer(playerIn, itemstack, ret, slot);
    }

    return super.transferStackInSlot(playerIn, index);
  }

  // copy of the slotCrafting class that prevents the extra crafting-recipe-result-getting the vanilla one does
  protected static class SlotStencilCrafting extends SlotCrafting {

    private final InventoryCrafting craftMatrix;

    public SlotStencilCrafting(EntityPlayer player, InventoryCrafting craftingInventory, IInventory p_i45790_3_, int slotIndex, int xPosition, int yPosition) {
      super(player, craftingInventory, p_i45790_3_, slotIndex, xPosition, yPosition);

      this.craftMatrix = craftingInventory;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
      net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, craftMatrix);
      this.onCrafting(stack);

      ItemStack itemstack1 = this.craftMatrix.getStackInSlot(0);

      // Assumption: Only 1 input, will always be decreased by only 1
      if(itemstack1 != null) {
        this.craftMatrix.decrStackSize(0, 1);
      }
    }
  }
}
