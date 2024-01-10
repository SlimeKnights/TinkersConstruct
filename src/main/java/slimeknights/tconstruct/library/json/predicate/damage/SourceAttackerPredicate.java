package slimeknights.tconstruct.library.json.predicate.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.serializer.NestedLoader;

/**
 * Predicate that checks for properties of the attacker in a damage source
 */
public record SourceAttackerPredicate(IJsonPredicate<LivingEntity> attacker) implements DamageSourcePredicate {
  public static final IGenericLoader<SourceAttackerPredicate> LOADER = new NestedLoader<>("entity_type", LivingEntityPredicate.LOADER, SourceAttackerPredicate::new, SourceAttackerPredicate::attacker);

  @Override
  public boolean matches(DamageSource source) {
    return source.getEntity() instanceof LivingEntity living && attacker.matches(living);
  }

  @Override
  public IGenericLoader<? extends IJsonPredicate<DamageSource>> getLoader() {
    return LOADER;
  }
}
