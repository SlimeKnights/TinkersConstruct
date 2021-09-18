package slimeknights.tconstruct.gadgets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.gadgets.item.SlimeBootsItem;
import slimeknights.tconstruct.library.utils.SlimeBounceHandler;

public class GadgetEvents {
  @SubscribeEvent
  public void onFall(LivingFallEvent event) {
    LivingEntity entity = event.getEntityLiving();
    if (entity == null) {
      return;
    }

    // do not care about client handles of this event except for players
    boolean isPlayer = entity instanceof PlayerEntity;
    boolean isClient = entity.getEntityWorld().isRemote;
    if (isClient && !isPlayer) {
      return;
    }

    // some entities are natively bouncy
    if (isPlayer || !TinkerTags.EntityTypes.BOUNCY.contains(entity.getType())) {
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
        if (!isPlayer || isClient) {
          double f = 0.91d + 0.04d;
          // only slow down half as much when bouncing
          entity.setMotion(entity.getMotion().x / f, entity.getMotion().y * -0.9, entity.getMotion().z / f);
          entity.isAirBorne = true;
          entity.setOnGround(false);
        }
        event.setCanceled(true); // we don't care about previous cancels, since we just bounceeeee
        entity.playSound(Sounds.SLIMY_BOUNCE.getSound(), 1f, 1f);
        SlimeBounceHandler.addBounceHandler(entity, entity.getMotion().y);
      }
    }
  }
}
