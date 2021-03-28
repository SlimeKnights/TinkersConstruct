package slimeknights.tconstruct.smeltery.inventory;

import lombok.Getter;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntReferenceHolder;
import slimeknights.mantle.inventory.MultiModuleContainer;
import slimeknights.tconstruct.library.utils.ValidZeroIntReference;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.module.MeltingModuleInventory;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SmelteryContainer extends MultiModuleContainer<SmelteryTileEntity> {
  @Getter
  private final SideInventoryContainer<SmelteryTileEntity> sideInventory;
  public SmelteryContainer(int id, @Nullable PlayerInventory inv, @Nullable SmelteryTileEntity smeltery) {
    super(TinkerSmeltery.smelteryContainer.get(), id, inv, smeltery);
    if (inv != null && smeltery != null) {
      // can hold 7 in a column, so try to fill the first column first
      // cap to 4 columns
      MeltingModuleInventory inventory = smeltery.getMeltingInventory();
      sideInventory = new SideInventoryContainer<>(TinkerSmeltery.smelteryContainer.get(), id, inv, smeltery, 0, 0, calcColumns(inventory.getSlots()));
      addSubContainer(sideInventory, true);

      Consumer<IntReferenceHolder> referenceConsumer = this::trackInt;
      ValidZeroIntReference.trackIntArray(referenceConsumer, smeltery.getFuelModule());
      inventory.trackInts(array -> ValidZeroIntReference.trackIntArray(referenceConsumer, array));
    } else {
      sideInventory = null;
    }
    addInventorySlots();
  }

  public SmelteryContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, SmelteryTileEntity.class));
  }

  /**
   * Calculates the number of columns to use for the screen
   * @param slots  Number of slots
   * @return  Number of columns
   */
  public static int calcColumns(int slots) {
    // every 7 slots gives us a new column, up to a maximum of 4 columns
    return Math.min(4, (slots + 6) / 7);
  }
}
