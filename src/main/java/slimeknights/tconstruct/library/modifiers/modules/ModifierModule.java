package slimeknights.tconstruct.library.modifiers.modules;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.WithHooks;

import javax.annotation.Nullable;
import java.util.List;

/** Interface for a module in a composable modifier. This is the serializable version of {@link HookProvider}. */
public interface ModifierModule extends IHaveLoader, HookProvider {
  /** Empty module instance. Used as fallback for {@link ConditionalModifierModule} modules. */
  ModifierModule EMPTY = SingletonLoader.singleton(loader -> new ModifierModule() {
    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return List.of();
    }

    @Override
    public RecordLoadable<? extends ModifierModule> getLoader() {
      return loader;
    }
  });
  /** Loader instance to register new modules. Note that loaders should not use the key "hooks" else composable modifiers will not parse */
  GenericLoaderRegistry<ModifierModule> LOADER = new GenericLoaderRegistry<>("Modifier Module", EMPTY, false);
  /** Loadable for modules including hooks */
  RecordLoadable<WithHooks<ModifierModule>> WITH_HOOKS = WithHooks.makeLoadable(LOADER, ModifierHooks.LOADER);

  @Override
  RecordLoadable<? extends ModifierModule> getLoader();

  /**
   * Gets the priority for this module.
   * All modules are polled to choose the priority of the final modifier with the following criteria:
   * <ol>
   *   <li>If no modifier sets a priority in its JSON, that is used</li>
   *   <li>If no module has nonnull priority, then the modifier will use {@link Modifier#DEFAULT_PRIORITY}</li>
   *   <li>If one module has nonnull priority, that priority will be used</li>
   *   <li>If two or more modules has nonnull priority, the first will be used and a warning will be logged</li>
   * </ol>>
   * @return Priority
   */
  @Nullable
  default Integer getPriority() {
    return null;
  }
}
