package slimeknights.tconstruct.tools.common.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;

// TODO: this is pretty unsecure, nothing stops the client from sending any time, can that be fixed?
public class BouncedPacket implements IThreadsafePacket {

  public BouncedPacket() {}

  public BouncedPacket(PacketBuffer buffer) {}

  @Override
  public void encode(PacketBuffer packetBuffer) {}

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayerEntity entity = context.getSender();
    if (entity != null) {
      entity.fallDistance = 0.0f;
    }
  }
}
