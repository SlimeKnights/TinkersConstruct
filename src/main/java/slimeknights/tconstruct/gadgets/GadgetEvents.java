package slimeknights.tconstruct.gadgets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.gadgets.item.SlimeBootsItem;
import slimeknights.tconstruct.items.GadgetItems;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.capability.piggyback.TinkerPiggybackSerializer;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tools.common.network.BouncedPacket;

public class GadgetEvents {

  @SubscribeEvent
  public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
    if (event.getObject() instanceof PlayerEntity) {
      event.addCapability(Util.getResource("piggyback"), new TinkerPiggybackSerializer((PlayerEntity) event.getObject()));
    }
  }

  @SubscribeEvent
  public void onFall(LivingFallEvent event) {
    LivingEntity entity = event.getEntityLiving();
    if (entity == null) {
      return;
    }

    ItemStack feet = entity.getItemStackFromSlot(EquipmentSlotType.FEET);
    if (!(feet.getItem() instanceof SlimeBootsItem)) {
      return;
    }

    // thing is wearing slime boots. let's get bouncyyyyy
    boolean isClient = entity.getEntityWorld().isRemote;
    if (!entity.isCrouching() && event.getDistance() > 2) {
      event.setDamageMultiplier(0);
      entity.fallDistance =  0.0F;

      if (isClient) {
        entity.setMotion(entity.getMotion().x, entity.getMotion().y * -0.9, entity.getMotion().z);
        entity.isAirBorne = true;
        entity.onGround = false;
        double f = 0.91d + 0.04d;
        // only slow down half as much when bouncing
        entity.setMotion(entity.getMotion().x / f, entity.getMotion().y, entity.getMotion().z / f);
        TinkerNetwork.getInstance().sendToServer(new BouncedPacket());
      } else {
        event.setCanceled(true); // we don't care about previous cancels, since we just bounceeeee
      }

      entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1f, 1f);
      SlimeBounceHandler.addBounceHandler(entity, entity.getMotion().y);
    } else if (!isClient && entity.isCrouching()) {
      event.setDamageMultiplier(0.2f);
    }
  }
}
