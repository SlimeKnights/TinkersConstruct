package slimeknights.tconstruct.tools.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;

public class EntityMovementChangePacket implements IThreadsafePacket {
  private final int entityID;
  private final double x;
  private final double y;
  private final double z;
  private final float yRot;
  private final float xRot;

  public EntityMovementChangePacket(Entity entity) {
    this.entityID = entity.getId();
    this.x = entity.getDeltaMovement().x;
    this.y = entity.getDeltaMovement().y;
    this.z = entity.getDeltaMovement().z;
    this.yRot = entity.getYRot();
    this.xRot = entity.getXRot();
  }

  public EntityMovementChangePacket(FriendlyByteBuf buffer) {
    this.entityID = buffer.readInt();
    this.x = buffer.readDouble();
    this.y = buffer.readDouble();
    this.z = buffer.readDouble();
    this.yRot = buffer.readFloat();
    this.xRot = buffer.readFloat();
  }

  @Override
  public void encode(FriendlyByteBuf packetBuffer) {
    packetBuffer.writeInt(this.entityID);
    packetBuffer.writeDouble(this.x);
    packetBuffer.writeDouble(this.y);
    packetBuffer.writeDouble(this.z);
    packetBuffer.writeFloat(this.yRot);
    packetBuffer.writeFloat(this.xRot);
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
      assert Minecraft.getInstance().level != null;
      Entity entity = Minecraft.getInstance().level.getEntity(packet.entityID);
      if (entity != null) {
        entity.setDeltaMovement(packet.x, packet.y, packet.z);
        entity.setYRot(packet.yRot);
        entity.setXRot(packet.xRot);
      }
    }
  }
}
