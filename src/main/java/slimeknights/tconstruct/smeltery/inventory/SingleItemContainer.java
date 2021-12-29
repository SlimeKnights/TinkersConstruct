package slimeknights.tconstruct.smeltery.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import slimeknights.mantle.inventory.SmartItemHandlerSlot;
import slimeknights.tconstruct.shared.inventory.TriggeringBaseContainer;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;

/**
 * Container for a block with a single item inventory
 */
public class SingleItemContainer extends TriggeringBaseContainer<BlockEntity> {
  public SingleItemContainer(int id, @Nullable Inventory inv, @Nullable BlockEntity te) {
    super(TinkerSmeltery.singleItemContainer.get(), id, inv, te);
    if (te != null) {
      te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        .ifPresent(handler -> this.addSlot(new SmartItemHandlerSlot(handler, 0, 80, 20)));
      this.addInventorySlots();
    }
  }

  public SingleItemContainer(int id, Inventory inv, FriendlyByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, BlockEntity.class));
  }

  @Override
  protected int getInventoryYOffset() {
    return 51;
  }
}
