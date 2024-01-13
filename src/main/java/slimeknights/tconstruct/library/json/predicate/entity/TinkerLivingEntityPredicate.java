package slimeknights.tconstruct.library.json.predicate.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;

import java.util.function.Predicate;

/** Additional living predicates added by Tinkers, Mantle controls the loader we use these days */
public interface TinkerLivingEntityPredicate {
  /** Entities that are in the air, notably does not count you as airborne if swimming, riding, or climbing */
  LivingEntityPredicate AIRBORNE = simple(entity -> !entity.isOnGround() && !entity.onClimbable() && !entity.isInWater() && !entity.isPassenger());
  /** Checks if the entity is on the ground */
  LivingEntityPredicate ON_GROUND = simple(Entity::isOnGround);
  /** Entities that are in the air */
  LivingEntityPredicate CROUCHING = simple(Entity::isCrouching);
  /** Entities with eyes in water */
  LivingEntityPredicate EYES_IN_WATER = simple(entity -> entity.wasEyeInWater);
  /** Entities with feet in water */
  LivingEntityPredicate FEET_IN_WATER = simple(Entity::isInWater);
  /** Checks if the entity is being hit by rain at their location */
  LivingEntityPredicate RAINING = simple(entity -> entity.level.isRainingAt(entity.blockPosition()));

  private static LivingEntityPredicate simple(Predicate<LivingEntity> predicate) {
    return SingletonLoader.singleton(loader -> new LivingEntityPredicate() {
      @Override
      public boolean matches(LivingEntity entity) {
        return predicate.test(entity);
      }

      @Override
      public IGenericLoader<? extends IJsonPredicate<LivingEntity>> getLoader() {
        return loader;
      }
    });
  }
}
