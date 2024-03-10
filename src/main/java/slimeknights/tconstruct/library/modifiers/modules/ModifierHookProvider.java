package slimeknights.tconstruct.library.modifiers.modules;

import slimeknights.tconstruct.library.modifiers.ModifierHook;

import java.util.List;

/** Interface to simplify building of modifier hook maps */
public interface ModifierHookProvider {
  /** Gets the default list of hooks this module implements. */
  List<ModifierHook<?>> getDefaultHooks();
}
