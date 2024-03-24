package slimeknights.tconstruct.library.json.predicate.modifier;

import net.minecraft.tags.TagKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

/**
 * Predicate matching an entity tag
 */
public record TagModifierPredicate(TagKey<Modifier> tag) implements ModifierPredicate {
  public static final RecordLoadable<TagModifierPredicate> LOADER = RecordLoadable.create(TinkerLoadables.MODIFIER_TAGS.requiredField("tag", TagModifierPredicate::tag), TagModifierPredicate::new);

  @Override
  public boolean matches(ModifierId modifier) {
    return ModifierManager.isInTag(modifier, tag);
  }

  @Override
  public IGenericLoader<? extends ModifierPredicate> getLoader() {
    return LOADER;
  }
}
