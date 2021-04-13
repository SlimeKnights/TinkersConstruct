package slimeknights.tconstruct.tools.common.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import slimeknights.mantle.network.packet.IThreadsafePacket;

// TODO: this is pretty unsecure, nothing stops the client from sending any time, can that be fixed?
public class BouncedPacket implements IThreadsafePacket {

  public BouncedPacket() {}

  public BouncedPacket(PacketByteBuf buffer) {}

  @Override
  public void encode(PacketByteBuf packetBuffer) {}

  @Override
  public void handleThreadsafe(PlayerEntity player, PacketSender context) {
    ServerPlayerEntity entity = (ServerPlayerEntity) player;
    if (entity != null) {
      entity.fallDistance = 0.0f;
    }
  }
}
