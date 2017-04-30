package slimeknights.tconstruct.tools.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.inventory.IContainerCraftingCustom;
import slimeknights.mantle.inventory.SlotCraftingCustom;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.inventory.InventoryCraftingPersistent;
import slimeknights.tconstruct.tools.common.network.StencilTableSelectionPacket;
import slimeknights.tconstruct.tools.common.tileentity.TilePatternChest;
import slimeknights.tconstruct.tools.common.tileentity.TileStencilTable;

public class ContainerStencilTable
    extends ContainerTinkerStation<TileStencilTable>
    implements IContainerCraftingCustom {

  public InventoryCraftingPersistent craftMatrix;
  public IInventory craftResult;

  private ItemStack output;

  private final Container patternChestSideInventory;

  public ContainerStencilTable(InventoryPlayer playerInventory, TileStencilTable tile) {
    super(tile);

    this.output = ItemStack.EMPTY;
    this.craftMatrix = new InventoryCraftingPersistent(this, tile, 1, 1);
    this.craftResult = new InventoryCraftResult();

    this.addSlotToContainer(new SlotStencil(this.craftMatrix, 0, 48, 35, true));
    this.addSlotToContainer(new SlotCraftingCustom(this, playerInventory.player, craftMatrix, craftResult, 1, 106, 35));

    TilePatternChest chest = detectTE(TilePatternChest.class);
    // TE present?
    if(chest != null) {
      patternChestSideInventory = new ContainerPatternChest.DynamicChestInventory(chest, chest, 6 + 176, 8, 6);
      addSubContainer(patternChestSideInventory, true);
    }
    else {
      patternChestSideInventory = null;
    }

    this.addPlayerInventory(playerInventory, 8, 84);
    onCraftMatrixChanged(null);
  }

  @Override
  protected void syncWithOtherContainer(BaseContainer<TileStencilTable> otherContainer, EntityPlayerMP player) {
    syncWithOtherContainer((ContainerStencilTable) otherContainer, player);
  }

  protected void syncWithOtherContainer(ContainerStencilTable otherContainer, EntityPlayerMP player) {
    this.setOutput(otherContainer.output);
    if(!output.isEmpty()) {
      TinkerNetwork.sendTo(new StencilTableSelectionPacket(output), player);
    }
  }

  public void setOutput(ItemStack stack) {
    if(stack.isEmpty()) {
      return;
    }
    // ensure that the output is valid
    for(ItemStack candidate : TinkerRegistry.getStencilTableCrafting()) {
      // NBT sensitive
      if(ItemStack.areItemStacksEqual(stack, candidate)) {
        // yay
        output = stack;
        updateResult();
        return;
      }
    }
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    updateResult();
  }

  // Sets the result in the output slot depending on if there's a pattern in the input and on which pattern was selected
  public void updateResult() {
    // no pattern :(
    if(craftMatrix.getStackInSlot(0).isEmpty() || output.isEmpty()) {
      craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
    }
    else {
      // set pattern from selection (or null if no selection)
      craftResult.setInventorySlotContents(0, output.copy());
    }
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
    // we always want to shiftclick the output into a pattern chest if present
    // note that we leave out a few checks/callbacks because we rely on how the stenciltable works
    if(index != 1) {
      return super.transferStackInSlot(playerIn, index);
    }

    Slot slot = this.inventorySlots.get(index);
    if(slot == null || !slot.getHasStack()) {
      return ItemStack.EMPTY;
    }

    ItemStack itemstack = slot.getStack().copy();
    ItemStack ret = slot.getStack().copy();

    if(patternChestSideInventory != null) {
      if(moveToContainer(itemstack, patternChestSideInventory)) {
        return ItemStack.EMPTY;
      }

      return notifySlotAfterTransfer(playerIn, itemstack, ret, slot);
    }

    return super.transferStackInSlot(playerIn, index);
  }

  @Override
  public void onCrafting(EntityPlayer player, ItemStack output, IInventory craftMatrix) {
    ItemStack itemstack1 = craftMatrix.getStackInSlot(0);

    // Assumption: Only 1 input, will always be decreased by only 1
    if(!itemstack1.isEmpty()) {
      craftMatrix.decrStackSize(0, 1);
    }

    updateResult();
  }

  @Override
  public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
    // prevents that doubleclicking on a stencil pulls them out of the crafting slot
    return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(p_94530_1_, p_94530_2_);
  }
}
