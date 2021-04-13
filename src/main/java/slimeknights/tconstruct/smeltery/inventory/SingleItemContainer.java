package slimeknights.tconstruct.smeltery.inventory;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

/**
 * Container for a block with a single item inventory
 */
public class SingleItemContainer extends BaseContainer<BlockEntity> {
  public SingleItemContainer(int id, @Nullable PlayerInventory inv, @Nullable BlockEntity te) {
    super(TinkerSmeltery.singleItemContainer, id, inv, te);
    if (te != null) {
      throw new RuntimeException("CRAB!"); // FIXME: PORT
//      te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
//        .ifPresent(handler -> this.addSlot(new ItemHandlerSlot(handler, 0, 80, 20)));
//      this.addInventorySlots();
    }
  }

  public SingleItemContainer(int id, PlayerInventory inv, PacketByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, BlockEntity.class));
  }

  @Override
  protected int getInventoryYOffset() {
    return 51;
  }
}
