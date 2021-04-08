package slimeknights.tconstruct.library.network;

import net.minecraft.network.PacketByteBuf;

/** @deprecated  Migrate to constructor registration */
@Deprecated
public interface INetworkSendable {
  void encode(PacketByteBuf buffer);

  void decode(PacketByteBuf buffer);
}
