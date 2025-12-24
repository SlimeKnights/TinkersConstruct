package slimeknights.tconstruct.library.tools.definition.module.interaction;

import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Module that makes all applicable interaction modifiers run on left. Used by shields notably. Redundant on bows since they don't even call right click interaction hooks. */
public enum AttackInteraction implements InteractionToolModule, ToolModule {
  /** Singleton instance */
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<AttackInteraction>defaultHooks(ToolHooks.INTERACTION);
  /** Loader instance */
  public static final SingletonLoader<AttackInteraction> LOADER = new SingletonLoader<>(INSTANCE);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean canInteract(IToolStackView tool, ModifierId modifier, InteractionSource source) {
    return source == InteractionSource.LEFT_CLICK;
  }

  @Override
  public SingletonLoader<AttackInteraction> getLoader() {
    return LOADER;
  }
}
