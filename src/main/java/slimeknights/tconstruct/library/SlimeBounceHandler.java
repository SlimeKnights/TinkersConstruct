package slimeknights.tconstruct.library;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.IdentityHashMap;


public class SlimeBounceHandler {

  private static final IdentityHashMap<Entity, SlimeBounceHandler> bouncingEntities = new IdentityHashMap<>();


  public final EntityLivingBase entityLiving;
  private int timer;
  private boolean wasInAir;
  private double bounce;
  private int bounceTick;

  private double lastMovX;
  private double lastMovZ;

  public SlimeBounceHandler(EntityLivingBase entityLiving, double bounce) {
    this.entityLiving = entityLiving;
    timer = 0;
    wasInAir = false;
    this.bounce = bounce;

    if(bounce != 0) {
      bounceTick = entityLiving.ticksExisted;
    }
    else {
      bounceTick = 0;
    }

    bouncingEntities.put(entityLiving, this);
    //entityLiving.addChatMessage(new ChatComponentText("added " + entityLiving.worldObj.isRemote));
  }

  @SubscribeEvent
  public void playerTickPost(TickEvent.PlayerTickEvent event) {
    // this is only relevant for the local player
    if(event.phase == TickEvent.Phase.END && event.player == entityLiving && !event.player.isElytraFlying()) {
      // bounce up. This is to pcircumvent the logic that resets y motion after landing
      if(event.player.ticksExisted == bounceTick) {
        event.player.motionY = bounce;
        bounceTick = 0;
      }

      // preserve motion
      if(!entityLiving.onGround && entityLiving.ticksExisted != bounceTick) {
        if(lastMovX != entityLiving.motionX || lastMovZ != entityLiving.motionZ) {
          double f = 0.91d + 0.025d;
          //System.out.println((entityLiving.worldObj.isRemote ? "client: " : "server: ") + entityLiving.motionX);
          entityLiving.motionX /= f;
          entityLiving.motionZ /= f;
          entityLiving.isAirBorne = true;
          lastMovX = entityLiving.motionX;
          lastMovZ = entityLiving.motionZ;
        }
      }

      // timing the effect out
      if(wasInAir && entityLiving.onGround) {
        if(timer == 0) {
          timer = entityLiving.ticksExisted;
        }
        else if(entityLiving.ticksExisted - timer > 5) {
          MinecraftForge.EVENT_BUS.unregister(this);
          bouncingEntities.remove(entityLiving);
          //entityLiving.addChatMessage(new ChatComponentText("removed " + entityLiving.worldObj.isRemote));
        }
      }
      else {
        timer = 0;
        wasInAir = true;
      }
    }
  }

  public static void addBounceHandler(EntityLivingBase entity) {
    addBounceHandler(entity, 0d);
  }

  public static void addBounceHandler(EntityLivingBase entity, double bounce) {
    // only supports actual players as it uses the PlayerTick event
    if(!(entity instanceof EntityPlayer) || entity instanceof FakePlayer) {
      return;
    }
    SlimeBounceHandler handler = bouncingEntities.get(entity);
    if(handler == null) {
      // wasn't bouncing yet, register it
      MinecraftForge.EVENT_BUS.register(new SlimeBounceHandler(entity, bounce));
    }
    else if(bounce != 0) {
      // updated bounce if needed
      handler.bounce = bounce;
      handler.bounceTick = entity.ticksExisted;
    }
  }
}
