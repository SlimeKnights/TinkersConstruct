package slimeknights.tconstruct.library.json.predicate.modifier;

import net.minecraft.tags.TagKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

/**
 * Predicate matching an entity tag.
 * @deprecated use {@link ModifierPredicate#tag(TagKey)}
 */
@Deprecated
public record TagModifierPredicate(TagKey<Modifier> tag) implements ModifierPredicate {
  public static final RecordLoadable<TagModifierPredicate> LOADER = RecordLoadable.create(TinkerLoadables.MODIFIER_TAGS.requiredField("tag", TagModifierPredicate::tag), TagModifierPredicate::new);

  /** @deprecated use {@link ModifierPredicate#tag(TagKey)} */
  @Deprecated(forRemoval = true)
  public TagModifierPredicate {}

  @Override
  public boolean matches(ModifierId modifier) {
    return ModifierManager.isInTag(modifier, tag);
  }

  @Override
  public RecordLoadable<TagModifierPredicate> getLoader() {
    return LOADER;
  }
}
