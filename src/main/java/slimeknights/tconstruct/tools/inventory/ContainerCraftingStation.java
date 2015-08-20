package slimeknights.tconstruct.tools.inventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntityChest;

import slimeknights.tconstruct.tools.tileentity.TileCraftingStation;
import slimeknights.tconstruct.library.mantle.InventoryCraftingPersistent;

// nearly the same as ContainerWorkbench but uses the TileEntities inventory
public class ContainerCraftingStation extends ContainerTinkerStation<TileCraftingStation> {

  public InventoryCraftingPersistent craftMatrix;
  public IInventory craftResult;

  public ContainerCraftingStation(InventoryPlayer playerInventory, TileCraftingStation tile) {
    super(tile);

    craftResult = new InventoryCraftResult();
    craftMatrix = new InventoryCraftingPersistent(this, tile, 3, 3);

    this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));
    int i;
    int j;

    for(i = 0; i < 3; ++i) {
      for(j = 0; j < 3; ++j) {
        this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
      }
    }

    TileEntityChest chest = detectTE(TileEntityChest.class);
    // TE present?
    if(chest != null) {
      Block blockChest = world.getBlockState(chest.getPos()).getBlock();
      // It's a chest?
      if(blockChest instanceof BlockChest) {
        IInventory inventory = ((BlockChest) blockChest).getLockableContainer(world, chest.getPos());
        if(inventory != null) {
          Container sideInventory = new ContainerSideInventory(chest, inventory, -6 - 18 * 6, 8, 6);

          addSubContainer(sideInventory, false);
        }
      }
    }

    this.addPlayerInventory(playerInventory, 8, 84);

    this.onCraftMatrixChanged(this.craftMatrix);
  }

  // update crafting
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    this.craftResult
        .setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.world));
  }

  public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
    return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(p_94530_1_, p_94530_2_);
  }


}
