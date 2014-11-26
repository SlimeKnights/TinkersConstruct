package tconstruct.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mantle.common.network.AbstractPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;

public class MovementUpdatePacket extends AbstractPacket {
    public int entityID;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;

    public MovementUpdatePacket() {
    }

    public MovementUpdatePacket(Entity entity) {
        this.entityID = entity.getEntityId();
        this.x = entity.motionX;
        this.y = entity.motionY;
        this.z = entity.motionZ;
        this.yaw = entity.rotationYaw;
        this.pitch = entity.rotationPitch;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        buffer.writeInt(entityID);
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
        buffer.writeFloat(yaw);
        buffer.writeFloat(pitch);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        this.entityID = buffer.readInt();
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
        this.yaw = buffer.readFloat();
        this.pitch = buffer.readFloat();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        Entity entity = player.worldObj.getEntityByID(entityID);
        if(entity != null) {
            entity.motionX = x;
            entity.motionY = y;
            entity.motionZ = z;
            entity.rotationYaw = yaw;
            entity.rotationPitch = pitch;
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        // only sent to client
    }
}
