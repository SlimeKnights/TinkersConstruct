package slimeknights.tconstruct.library.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerCommons;

public class PotionSlimeBounce extends TinkerPotion {

  public PotionSlimeBounce() {
    super(Util.getResource("slimebounce"), false, true);
  }

  public PotionEffect apply(EntityLivingBase entityLivingBase) {
    return apply(entityLivingBase, 0d);
  }

  // The effect gets applied indefinitely, but removes itself if the entity is on ground for a few ticks
  public PotionEffect apply(EntityLivingBase entityLivingBase, double bounce) {
    MinecraftForge.EVENT_BUS.register(new SlimeBounceHandler(entityLivingBase, bounce));
    return this.apply(entityLivingBase, 32767);
  }

  @Override
  public boolean isReady(int p_76397_1_, int p_76397_2_) {
    // applies every tick
    return true;
  }

  @Override
  public void performEffect(EntityLivingBase entity, int p_76394_2_) {
    // keep movement going in the air
    if(!entity.onGround) {
      // Entity living physics: EntityLivingBase.moveEntityWithHeading#1667
      // we just revert the speed decrease in x/z direction
      double f = 0.91d;
      entity.motionX /= f;
      //entity.motionY += 0.8;
      //entity.motionY /= 0.9800000190734863d;
      entity.motionZ /= f;
      entity.isAirBorne = true;
      // apparently not needed
      //if(entity instanceof EntityPlayerMP) {
        //((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
      //}
    }
  }

  @Override
  public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
    mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
    mc.getRenderItem().renderItemIntoGUI(new ItemStack(Items.slime_ball), x + 7, y + 8);
  }

  public static class SlimeBounceHandler {

    public final EntityLivingBase entityLiving;
    private int timer;
    private boolean wasInAir;
    private final double bounce;
    private int bounceTick;

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
    }

    @SubscribeEvent
    public void playerTickPost(TickEvent.PlayerTickEvent event) {
      if(event.phase == TickEvent.Phase.END) {
        if(event.player.ticksExisted == bounceTick) {
          // bounce up
          event.player.motionY = bounce;
          bounceTick = 0;
        }
      }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
      if(entityLiving.onGround && wasInAir) {
        if(++timer > 5) {
          entityLiving.removePotionEffect(TinkerCommons.potionSlimeBounce.getId());
          MinecraftForge.EVENT_BUS.unregister(this);
        }
      }
      else {
        timer = 0;
        wasInAir = true;
      }
    }
  }
}
