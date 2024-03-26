package slimeknights.tconstruct.library.tools.definition.module.interaction;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.definition.module.IToolModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Interaction that makes only a limited set work in the preferred hand, the rest working in the other hand
 */
public record PreferenceSetInteraction(InteractionSource preferredSource, IJsonPredicate<ModifierId> preferenceModifiers) implements InteractionToolModule, IToolModule {
  public static final RecordLoadable<PreferenceSetInteraction> LOADER = RecordLoadable.create(
    TinkerLoadables.INTERACTION_SOURCE.requiredField("preferred_source", PreferenceSetInteraction::preferredSource),
    ModifierPredicate.LOADER.requiredField("preferred_modifiers", PreferenceSetInteraction::preferenceModifiers),
    PreferenceSetInteraction::new);

  @Override
  public boolean canInteract(IToolStackView tool, ModifierId modifier, InteractionSource source) {
    return (source == preferredSource) == preferenceModifiers.matches(modifier);
  }

  @Override
  public IGenericLoader<? extends IToolModule> getLoader() {
    return LOADER;
  }
}
