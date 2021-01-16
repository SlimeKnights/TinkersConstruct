package slimeknights.tconstruct.smeltery.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.common.inventory.ItemHandlerSlot;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.DuctTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.inventory.DuctItemHandler;

import javax.annotation.Nullable;

public class DuctContainer extends BaseContainer<DuctTileEntity> {
  public DuctContainer(int id, @Nullable PlayerInventory inv, @Nullable DuctTileEntity duct) {
    super(TinkerSmeltery.ductContainer.get(), id, inv, duct);
    if (duct != null) {
      DuctItemHandler itemHandler = duct.getItemHandler();
      this.addSlot(new ItemHandlerSlot(duct.getItemHandler(), 0, 80, 20));
      this.addInventorySlots();
    }
  }

  public DuctContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, DuctTileEntity.class));
  }

  @Override
  protected int getInventoryYOffset() {
    return 51;
  }
}
