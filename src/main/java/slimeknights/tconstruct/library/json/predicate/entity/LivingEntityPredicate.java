package slimeknights.tconstruct.library.json.predicate.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.predicate.AndJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.InvertedJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.NestedJsonPredicateLoader;
import slimeknights.tconstruct.library.json.predicate.OrJsonPredicate;

import java.util.function.Predicate;

import static slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader.singleton;

/** Predicate matching an entity */
public interface LivingEntityPredicate extends IJsonPredicate<LivingEntity> {
  /** Predicate that matches all entities */
  LivingEntityPredicate ANY = simple(entity -> true);

  /** Loader for block state predicates */
  GenericLoaderRegistry<IJsonPredicate<LivingEntity>> LOADER = new GenericLoaderRegistry<>(ANY, true);
  /** Loader for inverted conditions */
  InvertedJsonPredicate.Loader<LivingEntity> INVERTED = new InvertedJsonPredicate.Loader<>(LOADER);
  /** Loader for and conditions */
  NestedJsonPredicateLoader<LivingEntity,AndJsonPredicate<LivingEntity>> AND = AndJsonPredicate.createLoader(LOADER, INVERTED);
  /** Loader for or conditions */
  NestedJsonPredicateLoader<LivingEntity,OrJsonPredicate<LivingEntity>> OR = OrJsonPredicate.createLoader(LOADER, INVERTED);

  /** Gets an inverted condition */
  @Override
  default IJsonPredicate<LivingEntity> inverted() {
    return INVERTED.create(this);
  }

  /* Singletons */

  /** Predicate that matches water sensitive entities */
  LivingEntityPredicate WATER_SENSITIVE = simple(LivingEntity::isSensitiveToWater);
  /** Predicate that matches fire immune entities */
  LivingEntityPredicate FIRE_IMMUNE = simple(LivingEntity::fireImmune);
  /** Predicate that matches fire immune entities */
  LivingEntityPredicate ON_FIRE = simple(LivingEntity::isOnFire);
  /** Entities that are in the air */
  LivingEntityPredicate AIRBORNE = simple(entity -> !entity.isOnGround() && !entity.onClimbable() && !entity.isInWater() && !entity.isPassenger());
  /** Entities that are in the air */
  LivingEntityPredicate CROUCHING = simple(Entity::isCrouching);
  /** Entities with eyes in water */
  LivingEntityPredicate EYES_IN_WATER = simple(entity -> entity.wasEyeInWater);
  /** Entities with feet in water */
  LivingEntityPredicate FEET_IN_WATER = simple(Entity::isInWater);

  /** Creates a simple predicate with no parameters */
  private static LivingEntityPredicate simple(Predicate<LivingEntity> predicate) {
    return singleton(loader -> new LivingEntityPredicate() {
      @Override
      public boolean matches(LivingEntity entity) {
        return predicate.test(entity);
      }

      @Override
      public IGenericLoader<? extends LivingEntityPredicate> getLoader() {
        return loader;
      }
    });
  }
}
