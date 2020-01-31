package slimeknights.tconstruct.library.network;

import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class TinkerPacket implements INetworkSendable {

  public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    this.handle(context);
    context.setPacketHandled(true);
  }

  protected abstract  void handle(NetworkEvent.Context context);
}
