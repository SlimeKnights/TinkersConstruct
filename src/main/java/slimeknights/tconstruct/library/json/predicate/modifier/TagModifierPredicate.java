package slimeknights.tconstruct.library.json.predicate.modifier;

import lombok.RequiredArgsConstructor;
import net.minecraft.tags.TagKey;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.TagPredicateLoader;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

/**
 * Predicate matching an entity tag
 */
@RequiredArgsConstructor
public class TagModifierPredicate implements ModifierPredicate {
  public static final TagPredicateLoader<Modifier,TagModifierPredicate> LOADER = new TagPredicateLoader<>(ModifierManager.REGISTRY_KEY, TagModifierPredicate::new, c -> c.tag);

  private final TagKey<Modifier> tag;

  @Override
  public boolean matches(ModifierId modifier) {
    return ModifierManager.isInTag(modifier, tag);
  }

  @Override
  public IGenericLoader<? extends ModifierPredicate> getLoader() {
    return LOADER;
  }
}
