package slimeknights.tconstruct.library.json.predicate.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.AndJsonPredicate;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.InvertedJsonPredicate;
import slimeknights.mantle.data.predicate.NestedJsonPredicateLoader;
import slimeknights.mantle.data.predicate.OrJsonPredicate;

import java.util.function.Predicate;

import static slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader.singleton;

/**
 * Predicate testing for damage sources
 */
public interface DamageSourcePredicate extends IJsonPredicate<DamageSource> {
  /** Predicate that matches all sources */
  DamageSourcePredicate ANY = simple(source -> true);
  /** Loader for item predicates */
  GenericLoaderRegistry<IJsonPredicate<DamageSource>> LOADER = new GenericLoaderRegistry<>(ANY, true);
  /** Loader for inverted conditions */
  InvertedJsonPredicate.Loader<DamageSource> INVERTED = new InvertedJsonPredicate.Loader<>(LOADER);
  /** Loader for and conditions */
  NestedJsonPredicateLoader<DamageSource,AndJsonPredicate<DamageSource>> AND = AndJsonPredicate.createLoader(LOADER, INVERTED);
  /** Loader for or conditions */
  NestedJsonPredicateLoader<DamageSource,OrJsonPredicate<DamageSource>> OR = OrJsonPredicate.createLoader(LOADER, INVERTED);

  /* Vanilla getters */
  DamageSourcePredicate PROJECTILE = simple(DamageSource::isProjectile);
  DamageSourcePredicate EXPLOSION = simple(DamageSource::isExplosion);
  DamageSourcePredicate BYPASS_ARMOR = simple(DamageSource::isBypassArmor);
  DamageSourcePredicate DAMAGE_HELMET = simple(DamageSource::isDamageHelmet);
  DamageSourcePredicate BYPASS_INVULNERABLE = simple(DamageSource::isBypassInvul);
  DamageSourcePredicate BYPASS_MAGIC = simple(DamageSource::isBypassMagic);
  DamageSourcePredicate FIRE = simple(DamageSource::isFire);
  DamageSourcePredicate MAGIC = simple(DamageSource::isMagic);
  DamageSourcePredicate FALL = simple(DamageSource::isFall);

  /** Damage that protection works against */
  DamageSourcePredicate CAN_PROTECT = simple(source -> !source.isBypassMagic() && !source.isBypassInvul());
  /** Custom concept: damage dealt by non-projectile entities */
  DamageSourcePredicate MELEE = simple(source -> {
    if (source.isProjectile()) {
      return false;
    }
    // if it's caused by an entity, require it to simply not be thorns
    // meets most normal melee attacks, like zombies, but also means a melee fire or melee magic attack will work
    if (source.getEntity() != null) {
      return source instanceof EntityDamageSource entityDamage && !entityDamage.isThorns();
    } else {
      // for non-entity damage, require it to not be any other type
      // blocks fall damage, falling blocks, cactus, but not starving, drowning, freezing
      return !source.isBypassArmor() && !source.isFire() && !source.isMagic() && !source.isExplosion();
    }
  });

  @Override
  default IJsonPredicate<DamageSource> inverted() {
    return INVERTED.create(this);
  }

  /** Creates a simple predicate with no parameters */
  private static DamageSourcePredicate simple(Predicate<DamageSource> predicate) {
    return singleton(loader -> new DamageSourcePredicate() {
      @Override
      public boolean matches(DamageSource source) {
        return predicate.test(source);
      }

      @Override
      public IGenericLoader<? extends IJsonPredicate<DamageSource>> getLoader() {
        return loader;
      }
    });
  }
}
