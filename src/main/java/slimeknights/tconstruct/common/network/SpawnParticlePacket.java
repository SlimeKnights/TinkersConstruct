package slimeknights.tconstruct.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.tools.TinkerTools;

public class SpawnParticlePacket extends AbstractPacketThreadsafe {

  public Particles particle;
  double x;
  double y;
  double z;
  double xSpeed;
  double ySpeed;
  double zSpeed;

  public SpawnParticlePacket() {
  }

  public SpawnParticlePacket(Particles particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    this.particle = particle;
    this.x = x;
    this.y = y;
    this.z = z;
    this.xSpeed = xSpeed;
    this.ySpeed = ySpeed;
    this.zSpeed = zSpeed;
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    TinkerTools.proxy.spawnParticle(particle, Minecraft.getMinecraft().theWorld, x,y,z, xSpeed,ySpeed,zSpeed);
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
  }
}
