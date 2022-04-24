package slimeknights.tconstruct.library.fluid.transfer;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;

import java.util.List;

/** Packet to sync fluid container transfer */
@RequiredArgsConstructor
public class FluidContainerTransferPacket implements IThreadsafePacket {
  private final List<IFluidContainerTransfer> transfers;

  public FluidContainerTransferPacket(FriendlyByteBuf buffer) {
    ImmutableList.Builder<IFluidContainerTransfer> builder = ImmutableList.builder();
    int size = buffer.readVarInt();
    for (int i = 0; i < size; i++) {
      builder.add(FluidContainerTransferManager.TRANSFER_LOADERS.fromNetwork(buffer));
    }
    this.transfers = builder.build();
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(transfers.size());
    for (IFluidContainerTransfer transfer : transfers) {
      FluidContainerTransferManager.TRANSFER_LOADERS.toNetwork(transfer, buffer);
    }
  }

  @Override
  public void handleThreadsafe(Context context) {
    FluidContainerTransferManager.INSTANCE.setTransfers(transfers);
  }
}
