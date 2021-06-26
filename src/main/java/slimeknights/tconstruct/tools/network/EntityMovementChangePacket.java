package slimeknights.tconstruct.tools.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
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
    this.x = entity.getMotion().x;
    this.y = entity.getMotion().y;
    this.z = entity.getMotion().z;
    this.yaw = entity.rotationYaw;
    this.pitch = entity.rotationPitch;
  }

  public EntityMovementChangePacket(PacketBuffer buffer) {
    this.entityID = buffer.readInt();
    this.x = buffer.readDouble();
    this.y = buffer.readDouble();
    this.z = buffer.readDouble();
    this.yaw = buffer.readFloat();
    this.pitch = buffer.readFloat();
  }

  @Override
  public void encode(PacketBuffer packetBuffer) {
    packetBuffer.writeInt(this.entityID);
    packetBuffer.writeDouble(this.x);
    packetBuffer.writeDouble(this.y);
    packetBuffer.writeDouble(this.z);
    packetBuffer.writeFloat(this.yaw);
    packetBuffer.writeFloat(this.pitch);
  }

  @Override
  public void handleThreadsafe(Context context) {
    if (context.getSender() != null) {
      HandleClient.handle(this);
    }
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {
    private static void handle(EntityMovementChangePacket packet) {
      assert Minecraft.getInstance().world != null;
      Entity entity = Minecraft.getInstance().world.getEntityByID(packet.entityID);
      if (entity != null) {
        entity.setMotion(packet.x, packet.y, packet.z);
        entity.rotationYaw = packet.yaw;
        entity.rotationPitch = packet.pitch;
      }
    }
  }
}
