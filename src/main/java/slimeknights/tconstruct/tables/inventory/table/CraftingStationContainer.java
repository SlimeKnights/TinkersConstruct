package slimeknights.tconstruct.tables.inventory.table;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.BaseStationContainer;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

import javax.annotation.Nullable;

public class CraftingStationContainer extends BaseStationContainer<CraftingStationTileEntity> {
  private final LazyResultSlot resultSlot;

  /**
   * Standard constructor
   * @param id    Window ID
   * @param inv   Player inventory
   * @param tile  Relevant tile entity
   */
  public CraftingStationContainer(int id, PlayerInventory inv, @Nullable CraftingStationTileEntity tile) {
    super(TinkerTables.craftingStationContainer.get(), id, inv, tile);

    // unfortunately, nothing works with no tile
    if (tile != null) {
      // send the player the current recipe, as we only sync to open containers
      tile.syncRecipe(inv.player);

      // add crafting slots first, as each added slot will clear the result cache
      for (int row = 0; row < 3; row++) {
        for (int col = 0; col < 3; col++) {
          this.addSlot(new Slot(tile, col + row * 3, 30 + col * 18, 17 + row * 18));
        }
      }
      // add result slot, will fetch result cache
      this.addSlot(resultSlot = new LazyResultSlot(tile.getCraftingResult(), 124, 35));

      World world = tile.getWorld();
      if (world != null) {
        // detect side inventory
        TileEntity inventoryTE = null;
        Direction accessDir = null;

        BlockPos pos = tile.getPos();
        horizontals:
        for (Direction dir : Direction.Plane.HORIZONTAL) {
          // skip any tables in this multiblock
          BlockPos neighbor = pos.offset(dir);
          for (Pair<BlockPos,BlockState> tinkerPos : this.stationBlocks) {
            if (tinkerPos.getLeft().equals(neighbor)) {
              continue horizontals;
            }
          }

          // fetch tile entity
          TileEntity te = world.getTileEntity(neighbor);
          if (te != null && isUsable(te, inv.player)) {
            // try internal access first
            if (hasItemHandler(te, null)) {
              inventoryTE = te;
              accessDir = null;
              break;
            }

            // try sided access next
            Direction side = dir.getOpposite();
            if (hasItemHandler(te, side)) {
              inventoryTE = te;
              accessDir = side;
              break;
            }
          }
        }

        // if we found something, add the side inventory
        if (inventoryTE != null) {
          this.addSubContainer(new SideInventoryContainer<>(TinkerTables.craftingStationContainer.get(), id, inv, inventoryTE, accessDir, -6 - 18 * 6, 8, 6), false);
        }
      }
    } else {
      // requirement for final variable
      resultSlot = null;
    }

    this.addInventorySlots();
  }

  /**
   * Factory constructor
   * @param id   Window ID
   * @param inv  Player inventory
   * @param buf  Buffer for fetching tile
   */
  public CraftingStationContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, CraftingStationTileEntity.class));
  }

  @Override
  public ItemStack transferStackInSlot(PlayerEntity player, int index) {
    Slot slot = this.inventorySlots.get(index);
    // fix issue on shift clicking from the result slot if the recipe result mismatches the displayed item
    if (slot == resultSlot) {
      if (tile != null && slot.getHasStack()) {
        // return the original result so shift click works
        ItemStack original = slot.getStack().copy(); // TODO: are these copies really needed?
        // but add the true result into the inventory
        ItemStack result = tile.getResultForPlayer(player);
        if (!result.isEmpty()) {
          boolean nothingDone = true;
          if (subContainers.size() > 0) { // the sub container check does not do well with 0 sub containers
            nothingDone = this.refillAnyContainer(result, this.subContainers);
          }
          nothingDone &= this.moveToPlayerInventory(result);
          if (subContainers.size() > 0) {
            nothingDone &= this.moveToAnyContainer(result, this.subContainers);
          }
          // if successfully added to an inventory, update
          if (!nothingDone) {
            tile.takeResult(player, result, result.getCount());
            tile.getCraftingResult().clear();
            return original;
          }
        } else {
          tile.notifyUncraftable(player);
        }
      }
      return ItemStack.EMPTY;
    } else {
      return super.transferStackInSlot(player, index);
    }
  }

  /**
   * Checks if the given tile entity is blacklisted
   * @param tileEntity  Tile to check
   * @return  True if blacklisted
   */
  private static boolean isUsable(TileEntity tileEntity, PlayerEntity player) {
    // must not be blacklisted and be usable
    return !TinkerTags.TileEntityTypes.CRAFTING_STATION_BLACKLIST.contains(tileEntity.getType())
           && (!(tileEntity instanceof IInventory) || ((IInventory)tileEntity).isUsableByPlayer(player));
  }

  /**
   * Checks to see if the given Tile Entity has an item handler that's compatible with the side inventory
   * The Tile Entity's item handler must be an instance of IItemHandlerModifiable
   * @param tileEntity Tile to check
   * @param direction the given direction
   * @return True if compatible.
   */
  private static boolean hasItemHandler(TileEntity tileEntity, @Nullable Direction direction) {
    return tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).filter(cap -> cap instanceof IItemHandlerModifiable).isPresent();
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    // handled in TE item display logic
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot slot) {
    return slot != this.resultSlot && super.canMergeSlot(stack, slot);
  }
}
