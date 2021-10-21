package slimeknights.tconstruct.library.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import slimeknights.tconstruct.common.Sounds;

public class TeleportHelper {

  /** Randomly teleports an entity, mostly copied from chorus fruit */
  public static void randomNearbyTeleport(LivingEntity living, ITeleportEventFactory factory) {
    double posX = living.getPosX();
    double posY = living.getPosY();
    double posZ = living.getPosZ();

    for(int i = 0; i < 16; ++i) {
      double x = posX + (living.getRNG().nextDouble() - 0.5D) * 16.0D;
      double y = MathHelper.clamp(posY + (double)(living.getRNG().nextInt(16) - 8), 0.0D, living.getEntityWorld().func_234938_ad_() - 1);
      double z = posZ + (living.getRNG().nextDouble() - 0.5D) * 16.0D;
      if (living.isPassenger()) {
        living.stopRiding();
      }

      EntityTeleportEvent event = factory.create(living, x, y, z);
      if (!event.isCanceled() && living.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
        SoundEvent soundevent = Sounds.SLIME_TELEPORT.getSound();
        living.getEntityWorld().playSound(null, posX, posY, posZ, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
        living.playSound(soundevent, 1.0F, 1.0F);
        break;
      }
    }
  }

  /** Predicate to test if the entity can teleport, typically just fires a cancelable event */
  @FunctionalInterface
  public interface ITeleportEventFactory {
    EntityTeleportEvent create(LivingEntity entity, double x, double y, double z);
  }
}
