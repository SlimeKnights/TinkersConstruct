package slimeknights.tconstruct.library.network;

import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface ITinkerPacket extends INetworkSendable {

  default void handle(Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    this.handle(context);
    context.setPacketHandled(true);
  }

  void handle(NetworkEvent.Context context);
}
