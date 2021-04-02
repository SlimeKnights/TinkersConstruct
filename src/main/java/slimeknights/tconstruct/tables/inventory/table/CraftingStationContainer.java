package slimeknights.tconstruct.tables.inventory.table;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.BaseStationContainer;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

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

      // detect side inventory
      TileEntity inventoryTE = null;
      Direction accessDir = null;

      BlockPos pos = tile.getPos();
      horizontals: for (Direction dir : Direction.Plane.HORIZONTAL) {
        // skip any tables in this multiblock
        BlockPos neighbor = pos.offset(dir);
        for (Pair<BlockPos, BlockState> tinkerPos : this.stationBlocks) {
          if (tinkerPos.getLeft().equals(neighbor)) {
            continue horizontals;
          }
        }

        // fetch tile entity
        TileEntity te = Objects.requireNonNull(tile.getWorld()).getTileEntity(neighbor);
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

  /**
   * Checks if the given tile entity is blacklisted
   * @param tileEntity  Tile to check
   * @return  True if blacklisted
   */
  private static boolean isUsable(TileEntity tileEntity, PlayerEntity player) {
    if (tileEntity instanceof CraftingStationTileEntity) {
      return false;
    }
    List<String> blacklist = Config.COMMON.craftingStationBlacklist.get();
    if (!blacklist.isEmpty()) {
      ResourceLocation registryName = TileEntityType.getId(tileEntity.getType());
      if (registryName == null || blacklist.contains(registryName.toString())) {
        return false;
      }
    }
    // if inventory, check usable
    return !(tileEntity instanceof IInventory) || ((IInventory)tileEntity).isUsableByPlayer(player);
  }

  private static boolean hasItemHandler(TileEntity te, @Nullable Direction direction) {
    return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).isPresent();
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
