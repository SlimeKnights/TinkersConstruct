package slimeknights.tconstruct.library.json.predicate.entity;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.predicate.TagPredicateLoader;

/**
 * Predicate matching an entity tag
 */
@RequiredArgsConstructor
public class TagEntityPredicate implements LivingEntityPredicate {
  public static final TagPredicateLoader<EntityType<?>,TagEntityPredicate> LOADER = new TagPredicateLoader<>(Registry.ENTITY_TYPE_REGISTRY, TagEntityPredicate::new, c -> c.tag);

  private final TagKey<EntityType<?>> tag;

  @Override
  public boolean matches(LivingEntity entity) {
    return entity.getType().is(tag);
  }

  @Override
  public IGenericLoader<? extends LivingEntityPredicate> getLoader() {
    return LOADER;
  }
}
