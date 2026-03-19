package slimeknights.tconstruct.tools.modifiers.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.common.TinkerEffect;

import java.util.List;

/** TODO 1.21: move to {@link slimeknights.tconstruct.shared.effect} */
public class MagneticEffect extends TinkerEffect {
  public MagneticEffect() {
    super(MobEffectCategory.BENEFICIAL, 0x720000, false);
  }

  @Override
  public boolean isDurationEffectTick(int duration, int amplifier) {
    return (duration & 1) == 0;
  }

  @Override
  public void applyEffectTick(LivingEntity entity, int amplifier) {
    applyMagnet(entity, amplifier);
  }

  /** Performs the magnetic effect. */
  public static <T extends Entity> void applyVelocity(LivingEntity entity, int amplifier, Class<T> targetClass, int minRange, float speed, int maxPush) {
    applyVelocity(entity.level(), entity.position(), amplifier, targetClass, minRange, speed, maxPush);
  }

  /** Performs the magnetic effect */
  public static <T extends Entity> void applyVelocity(Level level, Vec3 origin, int amplifier, Class<T> targetClass, int minRange, float speed, int maxPush) {
    // super magnetic - inspired by botanias code
    double x = origin.x;
    double y = origin.y;
    double z = origin.z;
    float range = minRange + amplifier;
    List<T> targets = level.getEntitiesOfClass(targetClass, new AABB(x - range, y - range, z - range, x + range, y + range, z + range));

    // only pull up to a max targets
    int pulled = 0;
    for (T target : targets) {
      if (target.isRemoved() || target.position().distanceToSqr(origin) < 0.25f) {
        continue;
      }
      // calculate direction: item -> player
      Vec3 vec = origin.subtract(target.getX(), target.getY(), target.getZ()).normalize().scale(speed * (amplifier + 1));
      if (!target.isNoGravity()) {
        vec = vec.add(0, 0.04f, 0);
      }

      // we calculated the movement vector and set it to the correct strength.. now we apply it \o/
      target.setDeltaMovement(target.getDeltaMovement().add(vec));

      pulled++;
      if (pulled > maxPush) {
        break;
      }
    }
  }

  /** Performs the magnetic effect */
  public static void applyMagnet(LivingEntity entity, int amplifier) {
    applyVelocity(entity, amplifier, ItemEntity.class, 3, 0.05f, 100);
  }
}
