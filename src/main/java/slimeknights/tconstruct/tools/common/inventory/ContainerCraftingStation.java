package slimeknights.tconstruct.tools.common.inventory;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.apache.commons.lang3.tuple.Pair;

import slimeknights.tconstruct.shared.inventory.InventoryCraftingPersistent;
import slimeknights.tconstruct.tools.common.tileentity.TileCraftingStation;

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

    // detect te
    TileEntity inventoryTE = null;
    EnumFacing accessDir = null;
    for(EnumFacing dir : EnumFacing.HORIZONTALS) {
      BlockPos neighbor = pos.offset(dir);
      boolean stationPart = false;
      for(Pair<BlockPos, IBlockState> tinkerPos : tinkerStationBlocks) {
        if(tinkerPos.getLeft().equals(neighbor)) {
          stationPart = true;
          break;
        }
      }
      if(!stationPart) {
        TileEntity te = world.getTileEntity(neighbor);
        if(te != null) {
          // try internal access first
          if(te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            if(te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) instanceof IItemHandlerModifiable) {
              inventoryTE = te;
              accessDir = null;
              break;
            }
          }
          // try sided access else
          if(te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite())) {
            if(te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite()) instanceof IItemHandlerModifiable) {
              inventoryTE = te;
              accessDir = dir.getOpposite();
              break;
            }
          }
        }
      }
    }

    if(inventoryTE != null) {
      addSubContainer(new ContainerSideInventory(inventoryTE, accessDir, -6 - 18 * 6, 8, 6), false);
    }

    this.addPlayerInventory(playerInventory, 8, 84);

    this.onCraftMatrixChanged(this.craftMatrix);
  }

  // update crafting
  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    this.craftResult
        .setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.world));
  }

  @Override
  public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
    return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(p_94530_1_, p_94530_2_);
  }

  protected TileEntity detectInventory() {
    for(EnumFacing dir : EnumFacing.HORIZONTALS) {
      BlockPos neighbor = pos.offset(dir);
      boolean stationPart = false;
      for(Pair<BlockPos, IBlockState> tinkerPos : tinkerStationBlocks) {
        if(tinkerPos.getLeft().equals(neighbor)) {
          stationPart = true;
          break;
        }
      }
      if(!stationPart) {
        TileEntity te = world.getTileEntity(neighbor);
        if(te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite())) {
          if(te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite()) instanceof IItemHandlerModifiable) {
            return te;
          }
        }
      }
    }

    return null;
  }

}
