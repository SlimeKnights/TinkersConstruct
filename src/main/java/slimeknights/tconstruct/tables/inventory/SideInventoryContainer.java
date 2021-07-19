package slimeknights.tconstruct.tables.inventory;

import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.library.inventory.SmelterySlot;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;

public class SideInventoryContainer<TILE extends BlockEntity> extends BaseContainer<TILE> {

  @Getter
  private final int columns;
  @Getter
  private final int slotCount;
  protected final Inventory itemHandler;

  public SideInventoryContainer(ScreenHandlerType<?> containerType, int windowId, PlayerInventory inv, @Nullable TILE tile, int x, int y, int columns) {
    this(containerType, windowId, inv, tile, null, x, y, columns);
  }

  //TODO this is all wrong
  public SideInventoryContainer(ScreenHandlerType<?> containerType, int windowId, PlayerInventory inv, @Nullable TILE tile, @Nullable Direction inventoryDirection, int x, int y, int columns) {
    super(containerType, windowId, inv, tile);

    if(tile instanceof SmelteryTileEntity) {
      this.itemHandler = ((SmelteryTileEntity) tile).meltingInventory;
    } else if(tile instanceof CraftingStationTileEntity) {
      this.itemHandler = ((CraftingStationTileEntity) tile).craftingInventory;
    } else {
      if (tile.getWorld().getBlockEntity(tile.getPos()) instanceof ChestBlockEntity) {
        ChestBlockEntity chest = (ChestBlockEntity) tile.getWorld().getBlockEntity(tile.getPos());
        this.itemHandler = new SimpleInventory(chest.inventory.toArray(new ItemStack[0]));
      } else {
        this.itemHandler = new SimpleInventory();
      }
    }

    // slot properties
    this.slotCount = itemHandler.size();
    this.columns = columns;
    int rows = this.slotCount / columns;
    if (this.slotCount % columns != 0) {
      rows++;
    }

    // add slots
    int index = 0;
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        if (index >= this.slotCount) {
          break;
        }
        if(index == 0)
          LogManager.getLogger().info(x + column * 18);
        this.addSlot(this.createSlot(itemHandler, index, x + column * 18, y + row * 18));
        index++;
      }
    }
  }

  /**
   * Creates a slot for this inventory
   * @param itemHandler  Item handler
   * @param index        Slot index
   * @param x            Slot X position
   * @param y            Slot Y position
   * @return  Inventory slot
   */
  protected Slot createSlot(Inventory itemHandler, int index, int x, int y) {
    return new SmelterySlot(itemHandler, index, x, y);
  }
}
