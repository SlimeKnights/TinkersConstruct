package slimeknights.tconstruct.library.network;

import net.minecraft.network.FriendlyByteBuf;

/** @deprecated  Migrate to constructor registration */
@Deprecated
public interface INetworkSendable {
  void encode(FriendlyByteBuf buffer);

  void decode(FriendlyByteBuf buffer);
}
