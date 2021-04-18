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

    // some entities are natively bouncy
    if (!TinkerTags.EntityTypes.BOUNCY.contains(entity.getType())) {
      // otherwise, is the thing is wearing slime boots?
      ItemStack feet = entity.getItemStackFromSlot(EquipmentSlotType.FEET);
      if (!(feet.getItem() instanceof SlimeBootsItem)) {
        return;
      }
    }

    // let's get bouncyyyyy
    if (event.getDistance() > 2) {
      // if crouching, take damage
      if (entity.isCrouching()) {
        event.setDamageMultiplier(0.2f);
      } else {
        event.setDamageMultiplier(0);
        entity.fallDistance =  0.0F;

        // players only bounce on the client, due to movement rules
        boolean isPlayer = entity instanceof PlayerEntity;
        if (!isPlayer || entity.getEntityWorld().isRemote) {
          double f = 0.91d + 0.04d;
          // only slow down half as much when bouncing
          entity.setMotion(entity.getMotion().x / f, entity.getMotion().y * -0.9, entity.getMotion().z / f);
          entity.isAirBorne = true;
          entity.setOnGround(false);
        }
        event.setCanceled(true); // we don't care about previous cancels, since we just bounceeeee
        entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1f, 1f);
        SlimeBounceHandler.addBounceHandler(entity, entity.getMotion().y);
      }
    }
  }
}
