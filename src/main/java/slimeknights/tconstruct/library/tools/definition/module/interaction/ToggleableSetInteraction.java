package slimeknights.tconstruct.library.tools.definition.module.interaction;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.recipe.worktable.ModifierSetWorktableRecipe;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Variant of {@link PreferenceSetInteraction} that supports toggling modifiers into the other set like {@link DualOptionInteraction}
 */
public record ToggleableSetInteraction(IJsonPredicate<ModifierId> interactModifiers) implements InteractionToolModule, ToolModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<PreferenceSetInteraction>defaultHooks(ToolHooks.INTERACTION);
  public static final RecordLoadable<ToggleableSetInteraction> LOADER = RecordLoadable.create(
    ModifierPredicate.LOADER.requiredField("interact_modifiers", ToggleableSetInteraction::interactModifiers),
    ToggleableSetInteraction::new);

  @Override
  public boolean canInteract(IToolStackView tool, ModifierId modifier, InteractionSource source) {
    // no usecase for mixing armor and not armor
    if (source == InteractionSource.ARMOR) {
      return false;
    }
    // interaction modifiers toggle to left click, otherwise they toggle to right click
    InteractionSource toggled = interactModifiers.matches(modifier) ? InteractionSource.LEFT_CLICK : InteractionSource.RIGHT_CLICK;
    // if the source is the toggled target, must be in the toggled set
    // if the source is not the toggled target, must not be in the toggled set
    return (source == toggled) == ModifierSetWorktableRecipe.isInSet(tool.getPersistentData(), toggled.getKey(), modifier);
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<ToggleableSetInteraction> getLoader() {
    return LOADER;
  }
}
