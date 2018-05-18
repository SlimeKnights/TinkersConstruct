package slimeknights.tconstruct.tools.common.inventory;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.apache.commons.lang3.tuple.Pair;

import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.shared.inventory.InventoryCraftingPersistent;
import slimeknights.tconstruct.tools.common.network.LastRecipeMessage;
import slimeknights.tconstruct.tools.common.tileentity.TileCraftingStation;

// nearly the same as ContainerWorkbench but uses the TileEntities inventory
public class ContainerCraftingStation extends ContainerTinkerStation<TileCraftingStation> {

  private final EntityPlayer player;
  private final InventoryCraftingPersistent craftMatrix;
  private final InventoryCraftResult craftResult;

  private IRecipe lastRecipe;
  private IRecipe lastLastRecipe;

  public ContainerCraftingStation(InventoryPlayer playerInventory, TileCraftingStation tile) {
    super(tile);

    craftResult = new InventoryCraftResult();
    craftMatrix = new InventoryCraftingPersistent(this, tile, 3, 3);
    player = playerInventory.player;

    this.addSlotToContainer(new SlotCraftingFastWorkbench(this, playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));
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
        if(te != null && !(te instanceof TileCraftingStation)) {
          // if blacklisted, skip checks entirely
          if(blacklisted(te.getClass())) {
            continue;
          }

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

  private boolean blacklisted(Class<? extends TileEntity> clazz) {
    if(Config.craftingStationBlacklist.isEmpty()) {
      return false;
    }

    // first, try registry name
    ResourceLocation registryName = TileEntity.getKey(clazz);
    if(registryName != null && Config.craftingStationBlacklist.contains(registryName.toString())) {
      return true;
    }

    // then try class name
    if(Config.craftingStationBlacklist.contains(clazz.getName())) {
      return true;
    }

    return false;
  }

  // update crafting
  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    this.slotChangedCraftingGrid(this.world, this.player, this.craftMatrix, this.craftResult);
  }

  @Override
  protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting inv, InventoryCraftResult result) {
    ItemStack itemstack = ItemStack.EMPTY;

    if(lastRecipe == null || !lastRecipe.matches(inv, world)) {
      lastRecipe = CraftingManager.findMatchingRecipe(inv, world);
    }

    if(lastRecipe != null) {
      itemstack = lastRecipe.getCraftingResult(inv);
    }

    if(!world.isRemote) {
      result.setInventorySlotContents(0, itemstack);
      EntityPlayerMP entityplayermp = (EntityPlayerMP) player;
      if(lastLastRecipe != lastRecipe) {
        entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, itemstack));
      }
      else if(lastLastRecipe != null && lastLastRecipe == lastRecipe && !ItemStack.areItemStacksEqual(lastLastRecipe.getCraftingResult(inv), lastRecipe.getCraftingResult(inv))) {
        entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, itemstack));
      }
      TinkerNetwork.sendTo(new LastRecipeMessage(lastRecipe), entityplayermp);
    }

    lastLastRecipe = lastRecipe;
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

  /**
   * @return the starting slot for the player inventory. Present for usage in the JEI crafting station support
   */
  public int getPlayerInventoryStart() {
    return playerInventoryStart;
  }

  public InventoryCrafting getCraftMatrix() {
    return craftMatrix;
  }

  public void updateLastRecipeFromServer(IRecipe recipe) {
    lastRecipe = recipe;
  }


  public NonNullList<ItemStack> getRemainingItems() {
    if(lastRecipe != null && lastRecipe.matches(craftMatrix, world)) {
      return lastRecipe.getRemainingItems(craftMatrix);
    }
    return craftMatrix.stackList;
  }
}
