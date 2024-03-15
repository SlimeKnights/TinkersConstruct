package slimeknights.tconstruct.library.json.predicate.modifier;

import net.minecraft.tags.TagKey;
import slimeknights.mantle.data.loader.TagKeyLoader;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

/**
 * Predicate matching an entity tag
 */
public record TagModifierPredicate(TagKey<Modifier> tag) implements ModifierPredicate {
  public static final IGenericLoader<TagModifierPredicate> LOADER = new TagKeyLoader<>(ModifierManager.REGISTRY_KEY, TagModifierPredicate::new, TagModifierPredicate::tag);

  @Override
  public boolean matches(ModifierId modifier) {
    return ModifierManager.isInTag(modifier, tag);
  }

  @Override
  public IGenericLoader<? extends ModifierPredicate> getLoader() {
    return LOADER;
  }
}
