package slimeknights.tconstruct.library.json.predicate.entity;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.predicate.AndJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.InvertedJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.NestedJsonPredicateLoader;
import slimeknights.tconstruct.library.json.predicate.OrJsonPredicate;

import static slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader.singleton;

/** Predicate matching an entity */
public interface LivingEntityPredicate extends IJsonPredicate<LivingEntity> {
  /** Predicate that matches all entities */
  LivingEntityPredicate ANY = singleton(loader -> new LivingEntityPredicate() {
    @Override
    public boolean matches(LivingEntity input) {
      return true;
    }

    @Override
    public IGenericLoader<? extends IJsonPredicate<LivingEntity>> getLoader() {
      return loader;
    }
  });

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
  LivingEntityPredicate WATER_SENSITIVE = singleton(loader -> new LivingEntityPredicate() {
    @Override
    public boolean matches(LivingEntity input) {
      return input.isSensitiveToWater();
    }

    @Override
    public IGenericLoader<? extends IJsonPredicate<LivingEntity>> getLoader() {
      return loader;
    }
  });

  /** Predicate that matches fire immune entities */
  LivingEntityPredicate FIRE_IMMUNE = singleton(loader -> new LivingEntityPredicate() {
    @Override
    public boolean matches(LivingEntity input) {
      return input.fireImmune();
    }

    @Override
    public IGenericLoader<? extends IJsonPredicate<LivingEntity>> getLoader() {
      return loader;
    }
  });

  /** Predicate that matches fire immune entities */
  LivingEntityPredicate ON_FIRE = singleton(loader -> new LivingEntityPredicate() {
    @Override
    public boolean matches(LivingEntity input) {
      return input.isOnFire();
    }

    @Override
    public IGenericLoader<? extends IJsonPredicate<LivingEntity>> getLoader() {
      return loader;
    }
  });
}
