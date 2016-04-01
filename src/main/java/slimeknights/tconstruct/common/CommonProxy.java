package slimeknights.tconstruct.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

import slimeknights.mantle.network.AbstractPacket;
import slimeknights.tconstruct.TConstruct;

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

    registerModels();
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

  protected void registerModels() {
    if(Loader.instance().hasReachedState(LoaderState.INITIALIZATION)) {
      TConstruct.log.error(
          "Proxy.registerModels has to be called during preInit. Otherwise the models wont be found on first load.");
    }
  }

  public void sendPacketToServerOnly(AbstractPacket packet) {

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
    if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion)) return;
    explosion.doExplosionA();
    explosion.doExplosionB(false);

    if (!explosion.isSmoking)
    {
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
}
