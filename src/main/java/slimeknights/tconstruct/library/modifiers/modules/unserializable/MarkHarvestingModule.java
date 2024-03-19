package slimeknights.tconstruct.library.modifiers.modules.unserializable;

import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierHookProvider;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;

import java.util.List;

/** Simple module with hooks form of {@link BlockHarvestModifierHook.MarkHarvesting}. */
public enum MarkHarvestingModule implements BlockHarvestModifierHook.MarkHarvesting, ModifierHookProvider {
  INSTANCE;

  private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierModule.<MarkHarvestingModule>defaultHooks(TinkerHooks.BLOCK_HARVEST);

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
}
