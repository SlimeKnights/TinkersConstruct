package slimeknights.tconstruct.library.json.predicate;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;

import javax.annotation.Nullable;

/** Additional living predicates added by Tinkers, Mantle controls the loader we use these days */
public class TinkerPredicate {
  private TinkerPredicate() {}

  /** Entities that are in the air, notably does not count you as airborne if swimming, riding, or climbing */
  public static LivingEntityPredicate AIRBORNE = LivingEntityPredicate.simple(entity -> !entity.isOnGround() && !entity.onClimbable() && !entity.isInWater() && !entity.isPassenger());

  /** Helper for dealing with the common case of nullable entities, often used when they are entity but not living. */
  public static boolean matches(IJsonPredicate<LivingEntity> predicate, @Nullable LivingEntity entity) {
    if (entity == null) {
      return predicate == LivingEntityPredicate.ANY;
    }
    return predicate.matches(entity);
  }

  /** Helper for dealing with the common case of nullable entities, often used when they are entity but not living. */
  public static boolean matches(IJsonPredicate<DamageSource> predicate, @Nullable DamageSource source) {
    if (source == null) {
      return predicate == DamageSourcePredicate.ANY;
    }
    return predicate.matches(source);
  }
}
