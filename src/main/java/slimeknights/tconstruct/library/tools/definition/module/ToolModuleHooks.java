package slimeknights.tconstruct.library.tools.definition.module;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.definition.module.interaction.InteractionToolModule;

/** Modules for tool definition data */
public class ToolModuleHooks {
  private ToolModuleHooks() {}

  public static void init() {}

  /** Hook for configuring interaction behaviors on the tool */
  public static final ModifierHook<InteractionToolModule> INTERACTION = register("tool_interaction", InteractionToolModule.class, (t, m, s) -> true);


  /** Registers a new modifier hook under {@code tconstruct}  that cannot merge. Merging is not supported for tool hooks */
  @SuppressWarnings("SameParameterValue")
  private static <T> ModifierHook<T> register(String name, Class<T> filter, T defaultInstance) {
    return ModifierHooks.register(TConstruct.getResource(name), filter, defaultInstance);
  }
}
