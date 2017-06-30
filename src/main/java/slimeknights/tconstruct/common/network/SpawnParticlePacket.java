package slimeknights.tconstruct.common.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.tools.TinkerTools;

public class SpawnParticlePacket extends AbstractPacketThreadsafe {

  public Particles particle;
  private double x;
  private double y;
  private double z;
  private double xSpeed;
  private double ySpeed;
  private double zSpeed;
  private int[] data;

  public SpawnParticlePacket() {
  }

  public SpawnParticlePacket(Particles particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... data) {
    this.particle = particle;
    this.x = x;
    this.y = y;
    this.z = z;
    this.xSpeed = xSpeed;
    this.ySpeed = ySpeed;
    this.zSpeed = zSpeed;
    this.data = data;
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    TinkerTools.proxy.spawnParticle(particle, null, x, y, z, xSpeed, ySpeed, zSpeed, data);
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    // clients have particles, servers don't!
    throw new UnsupportedOperationException("Clientside only");
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    particle = Particles.values()[buf.readInt()];
    x = buf.readDouble();
    y = buf.readDouble();
    z = buf.readDouble();
    xSpeed = buf.readDouble();
    ySpeed = buf.readDouble();
    zSpeed = buf.readDouble();

    data = new int[buf.readInt()];
    for(int i = 0; i < data.length; i++) {
      data[i] = buf.readInt();
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(particle.ordinal());
    buf.writeDouble(x);
    buf.writeDouble(y);
    buf.writeDouble(z);
    buf.writeDouble(xSpeed);
    buf.writeDouble(ySpeed);
    buf.writeDouble(zSpeed);

    buf.writeInt(data.length);
    for(int i : data) {
      buf.writeInt(i);
    }
  }
}
