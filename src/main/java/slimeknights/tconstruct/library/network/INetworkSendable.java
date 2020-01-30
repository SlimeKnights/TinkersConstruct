package slimeknights.tconstruct.library.network;

import net.minecraft.network.PacketBuffer;

public interface INetworkSendable {

  void encode(PacketBuffer buffer);

  void decode(PacketBuffer buffer);
}
