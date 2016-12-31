package slimeknights.tconstruct.tools.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetHandlerPlayServer;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacketThreadsafe;

public class EntityMovementChangePacket extends AbstractPacketThreadsafe {

  public int entityID;
  public double x;
  public double y;
  public double z;
  public float yaw;
  public float pitch;

  public EntityMovementChangePacket() {
  }

  public EntityMovementChangePacket(Entity entity) {
    this.entityID = entity.getEntityId();
    this.x = entity.motionX;
    this.y = entity.motionY;
    this.z = entity.motionZ;
    this.yaw = entity.rotationYaw;
    this.pitch = entity.rotationPitch;
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    Entity entity = Minecraft.getMinecraft().world.getEntityByID(entityID);
    if(entity != null) {
      entity.motionX = x;
      entity.motionY = y;
      entity.motionZ = z;
      entity.rotationYaw = yaw;
      entity.rotationPitch = pitch;
    }
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    // only ever sent to clients
    throw new UnsupportedOperationException("Serverside only");
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.entityID = buf.readInt();
    this.x = buf.readDouble();
    this.y = buf.readDouble();
    this.z = buf.readDouble();
    this.yaw = buf.readFloat();
    this.pitch = buf.readFloat();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(entityID);
    buf.writeDouble(x);
    buf.writeDouble(y);
    buf.writeDouble(z);
    buf.writeFloat(yaw);
    buf.writeFloat(pitch);
  }
}
