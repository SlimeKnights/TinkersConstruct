package slimeknights.tconstruct.tools.common.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import slimeknights.mantle.network.packet.IThreadsafePacket;

public class EntityMovementChangePacket implements IThreadsafePacket {

  private int entityID;
  private double x;
  private double y;
  private double z;
  private float yaw;
  private float pitch;

  public EntityMovementChangePacket(Entity entity) {
    this.entityID = entity.getEntityId();
    this.x = entity.getVelocity().x;
    this.y = entity.getVelocity().y;
    this.z = entity.getVelocity().z;
    this.yaw = entity.yaw;
    this.pitch = entity.pitch;
  }

  public EntityMovementChangePacket(PacketByteBuf buffer) {
    this.entityID = buffer.readInt();
    this.x = buffer.readDouble();
    this.y = buffer.readDouble();
    this.z = buffer.readDouble();
    this.yaw = buffer.readFloat();
    this.pitch = buffer.readFloat();
  }

  @Override
  public void encode(PacketByteBuf packetBuffer) {
    packetBuffer.writeInt(this.entityID);
    packetBuffer.writeDouble(this.x);
    packetBuffer.writeDouble(this.y);
    packetBuffer.writeDouble(this.z);
    packetBuffer.writeFloat(this.yaw);
    packetBuffer.writeFloat(this.pitch);
  }

  @Override
  public void handleThreadsafe(PlayerEntity player, PacketSender context) {
    HandleClient.handle(this);
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {
    private static void handle(EntityMovementChangePacket packet) {
      assert MinecraftClient.getInstance().world != null;
      Entity entity = MinecraftClient.getInstance().world.getEntityById(packet.entityID);
      if (entity != null) {
        entity.setVelocity(packet.x, packet.y, packet.z);
        entity.yaw = packet.yaw;
        entity.pitch = packet.pitch;
      }
    }
  }
}
