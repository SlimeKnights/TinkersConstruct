package slimeknights.tconstruct.smeltery.inventory;

import lombok.Getter;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import slimeknights.mantle.inventory.MultiModuleContainer;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;

import javax.annotation.Nullable;

public class SmelteryContainer extends MultiModuleContainer<SmelteryTileEntity> {
  @Getter
  private final SideInventoryContainer<SmelteryTileEntity> sideInventory;
  public SmelteryContainer(int id, @Nullable PlayerInventory inv, @Nullable SmelteryTileEntity smeltery) {
    super(TinkerSmeltery.smelteryContainer.get(), id, inv, smeltery);
    if (inv != null && smeltery != null) {
      sideInventory = new SideInventoryContainer<>(TinkerSmeltery.smelteryContainer.get(), id, inv, smeltery, 0, 0, 3);
      addSubContainer(sideInventory, true);
      smeltery.getMeltingInventory().trackInts(this::trackIntArray);
    } else {
      sideInventory = null;
    }
    addInventorySlots();
  }

  public SmelteryContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, SmelteryTileEntity.class));
  }
}
