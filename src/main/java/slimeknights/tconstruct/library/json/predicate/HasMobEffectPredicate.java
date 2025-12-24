package slimeknights.tconstruct.library.json.predicate;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;

/**
 * Predicate that checks if an entity has the given mob effect.
 * @deprecated use {@link slimeknights.mantle.data.predicate.entity.HasMobEffectPredicate}
 */
@Deprecated
public record HasMobEffectPredicate(MobEffect effect) implements LivingEntityPredicate {
  public static final RecordLoadable<HasMobEffectPredicate> LOADER = RecordLoadable.create(Loadables.MOB_EFFECT.requiredField("effect", HasMobEffectPredicate::effect), HasMobEffectPredicate::new);

  @Override
  public boolean matches(LivingEntity living) {
    return living.hasEffect(effect);
  }

  @Override
  public RecordLoadable<? extends IJsonPredicate<LivingEntity>> getLoader() {
    return LOADER;
  }
}
