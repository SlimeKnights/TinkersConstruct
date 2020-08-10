package slimeknights.tconstruct.library.network;

import net.minecraft.network.PacketBuffer;

/** @deprecated  Migrate to constructor registration */
@Deprecated
public interface INetworkSendable {
  void encode(PacketBuffer buffer);

  void decode(PacketBuffer buffer);
}
