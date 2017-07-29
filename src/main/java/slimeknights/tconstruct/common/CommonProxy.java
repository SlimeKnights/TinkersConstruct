package slimeknights.tconstruct.common;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import slimeknights.mantle.network.AbstractPacket;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.network.SpawnParticlePacket;
import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.shared.client.ParticleEffect;

/**
 * This class contains all the base functions for server and clientside proxy that should be called. Can be used when no
 * specific handling is needed. Can be replaced with a specific implementation at any time.
 *
 * Also doubles as documentation when what should happen.
 */
public class CommonProxy {

  public void preInit() {
    if(!Loader.instance().isInState(LoaderState.PREINITIALIZATION)) {
      TConstruct.log.error(
          "Proxy.preInit has to be called during Pre-Initialisation.");
    }
  }

  public void init() {
    if(!Loader.instance().isInState(LoaderState.INITIALIZATION)) {
      TConstruct.log.error(
          "Proxy.init has to be called during Initialisation.");
    }
  }

  public void postInit() {
    if(!Loader.instance().isInState(LoaderState.POSTINITIALIZATION)) {
      TConstruct.log.error(
          "Proxy.postInit has to be called during Post-Initialisation.");
    }
  }

  public void registerModels() {
    if(Loader.instance().hasReachedState(LoaderState.INITIALIZATION)) {
      TConstruct.log.error(
          "Proxy.registerModels has to be called during preInit. Otherwise the models wont be found on first load.");
    }
  }

  public void sendPacketToServerOnly(AbstractPacket packet) {

  }

  public void spawnAttackParticle(Particles particleType, Entity entity, double height) {
    float distance = 0.017453292f;

    double xd = -MathHelper.sin(entity.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(entity.rotationPitch / 180.0F * (float) Math.PI);
    double zd = +MathHelper.cos(entity.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(entity.rotationPitch / 180.0F * (float) Math.PI);
    double yd = -MathHelper.sin(entity.rotationPitch / 180.0F * (float) Math.PI);

    distance = 1f;
    xd *= distance;
    yd *= distance;
    zd *= distance;

    //double xd = (double)(-MathHelper.sin(entity.rotationYaw * distance));
    //double zd = (double)MathHelper.cos(entity.rotationYaw * distance);
    //double yd = (double)(-MathHelper.sin(entity.rotationPitch * distance));

    spawnParticle(particleType,
                  entity.getEntityWorld(),
                  entity.posX + xd,
                  entity.posY + entity.height * height,
                  entity.posZ + zd,
                  xd, yd, zd);
  }

  public void spawnEffectParticle(ParticleEffect.Type type, Entity entity, int count) {
    spawnParticle(Particles.EFFECT, entity.getEntityWorld(), entity.posX, entity.posY + entity.height * 0.5f, entity.posZ, 0d, 1d, 0d, count, type.ordinal());
  }

  public void spawnEffectParticle(ParticleEffect.Type type, World world, double x, double y, double z, int count) {
    spawnParticle(Particles.EFFECT, world, x, y, z, 0d, -1d, 0d, count, type.ordinal());
  }

  public void spawnParticle(Particles particleType, World world, double x, double y, double z, int... data) {
    spawnParticle(particleType, world, x, y, z, 0d, 0d, 0d, data);
  }

  public void spawnParticle(Particles particleType, World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... data) {
    // 32*32 = 1024 = vanilla particle range
    NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, 32);
    AbstractPacket packet = new SpawnParticlePacket(particleType, x, y, z, xSpeed, ySpeed, zSpeed, data);
    TinkerNetwork.sendToAllAround(packet, point);
  }

  public void spawnSlimeParticle(World world, double x, double y, double z) {

  }

  public void registerFluidModels(Fluid fluid) {

  }

  public void preventPlayerSlowdown(Entity player, float originalSpeed, Item item) {
    // clientside only
  }

  // replicates the World.newExplosion code to separate behaviour on server/client for any explosion implementation
  public void customExplosion(World world, Explosion explosion) {
    // server side
    if(net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion)) {
      return;
    }
    explosion.doExplosionA();
    explosion.doExplosionB(false);

    if(!explosion.damagesTerrain) {
      explosion.clearAffectedBlockPositions();
    }

    // todo: send custom explosion packet to clients
    // Send packets so player are moved around
    /*
    for (EntityPlayer entityplayer : world.playerEntities)
    {
      //if (entityplayer.getDistanceSq(x, y, z) < 4096.0D)
      {
        Vec3d vec = explosion.getPosition();
        ((EntityPlayerMP)entityplayer).playerNetServerHandler.sendPacket(new SPacketExplosion(vec.xCoord, vec.yCoord, vec.zCoord, 1, explosion.getAffectedBlockPositions(), (Vec3d)explosion.getPlayerKnockbackMap().get(entityplayer)));
      }
    }*/
  }

  public void updateEquippedItemForRendering(EnumHand hand) {

  }
}
