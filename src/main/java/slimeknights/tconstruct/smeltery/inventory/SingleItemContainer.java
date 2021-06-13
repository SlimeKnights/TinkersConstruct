package slimeknights.tconstruct.smeltery.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import slimeknights.mantle.inventory.ItemHandlerSlot;
import slimeknights.tconstruct.shared.inventory.TriggeringBaseContainer;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;

/**
 * Container for a block with a single item inventory
 */
public class SingleItemContainer extends TriggeringBaseContainer<TileEntity> {
  public SingleItemContainer(int id, @Nullable PlayerInventory inv, @Nullable TileEntity te) {
    super(TinkerSmeltery.singleItemContainer.get(), id, inv, te);
    if (te != null) {
      te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        .ifPresent(handler -> this.addSlot(new ItemHandlerSlot(handler, 0, 80, 20)));
      this.addInventorySlots();
    }
  }

  public SingleItemContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, TileEntity.class));
  }

  @Override
  protected int getInventoryYOffset() {
    return 51;
  }
}
