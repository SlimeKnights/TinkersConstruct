package slimeknights.tconstruct.library.fluid.transfer;

import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.network.packet.IThreadsafePacket;

import java.util.Set;

/** Packet to sync fluid container transfer */
@RequiredArgsConstructor
public class FluidContainerTransferPacket implements IThreadsafePacket {
  private final Set<Item> items;

  public FluidContainerTransferPacket(FriendlyByteBuf buffer) {
    ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
    int size = buffer.readVarInt();
    for (int i = 0; i < size; i++) {
      builder.add(buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS));
    }
    this.items = builder.build();
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(items.size());
    for (Item item : items) {
      buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item);
    }
  }

  @Override
  public void handleThreadsafe(Context context) {
    FluidContainerTransferManager.INSTANCE.setContainerItems(items);
  }
}
