package slimeknights.tconstruct.compat.minecraft.world.entity;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;

/** Compatibility enum for the removed vanilla MobType API. */
public enum MobType implements LivingEntityPredicate {
  UNDEFINED,
  UNDEAD,
  ARTHROPOD,
  ILLAGER,
  WATER;

  @Override
  public boolean matches(LivingEntity entity) {
    return switch (this) {
      case UNDEAD -> entity.getType().is(EntityTypeTags.UNDEAD);
      case ARTHROPOD -> entity.getType().is(EntityTypeTags.ARTHROPOD);
      case ILLAGER -> entity.getType().is(EntityTypeTags.ILLAGER);
      case WATER -> entity.getType().is(EntityTypeTags.AQUATIC);
      case UNDEFINED -> !entity.getType().is(EntityTypeTags.UNDEAD) && !entity.getType().is(EntityTypeTags.ARTHROPOD) && !entity.getType().is(EntityTypeTags.ILLAGER) && !entity.getType().is(EntityTypeTags.AQUATIC);
    };
  }

  @Override
  public slimeknights.mantle.data.loadable.record.RecordLoadable<? extends LivingEntityPredicate> getLoader() {
    return slimeknights.mantle.data.predicate.entity.MobTypePredicate.LOADER;
  }
}
