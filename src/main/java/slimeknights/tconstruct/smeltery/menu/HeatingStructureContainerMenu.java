package slimeknights.tconstruct.smeltery.menu;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import slimeknights.mantle.util.sync.ValidZeroDataSlot;
import slimeknights.tconstruct.shared.inventory.TriggeringMultiModuleContainerMenu;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.tables.menu.module.SideInventoryContainer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class HeatingStructureContainerMenu extends TriggeringMultiModuleContainerMenu<HeatingStructureBlockEntity> {
  @Getter
  private final SideInventoryContainer<HeatingStructureBlockEntity> sideInventory;
  public HeatingStructureContainerMenu(int id, @Nullable Inventory inv, @Nullable HeatingStructureBlockEntity structure) {
    super(TinkerSmeltery.smelteryContainer.get(), id, inv, structure);
    if (inv != null && structure != null) {
      // can hold 7 in a column, so try to fill the first column first
      // cap to 4 columns
      MeltingModuleInventory inventory = structure.getMeltingInventory();
      sideInventory = new SideInventoryContainer<>(TinkerSmeltery.smelteryContainer.get(), id, inv, structure, 0, 0, calcColumns(inventory.getSlots()));
      addSubContainer(sideInventory, true);

      Consumer<DataSlot> referenceConsumer = this::addDataSlot;
      ValidZeroDataSlot.trackIntArray(referenceConsumer, structure.getFuelModule());
      inventory.trackInts(array -> ValidZeroDataSlot.trackIntArray(referenceConsumer, array));
    } else {
      sideInventory = null;
    }
    addInventorySlots();
  }

  public HeatingStructureContainerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, HeatingStructureBlockEntity.class));
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
