package slimeknights.tconstruct.library.modifiers;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/** Registry for modifier hooks. All hooks must be registered to be usable in JSON */
public class ModifierHooks {
  private ModifierHooks() {}

  /** Map of ID to hook */
  private static final Map<ResourceLocation,ModifierHook<?>> HOOKS = new ConcurrentHashMap<>();
  /** Unmodifiable view of the hook map */
  private static final Collection<ResourceLocation> HOOK_IDS = Collections.unmodifiableCollection(HOOKS.keySet());


  /* Registry */

  /**
   * Gets the hook for the given name
   * @param name  Name
   * @return  Hook, or null if unknown
   */
  @Nullable
  public static ModifierHook<?> getHook(ResourceLocation name) {
    return HOOKS.get(name);
  }

  /**
   * Registers a new hook
   * @param hook  Hook to register
   * @param <T>  Stat type
   * @return  Registered hooks
   * @throws IllegalArgumentException If duplicate hooks are registered
   */
  public static <T extends ModifierHook<?>> T register(T hook) {
    if (HOOKS.containsKey(hook.getName())) {
      throw new IllegalArgumentException("Attempt to register duplicate modifier hook " + hook.getName());
    }
    HOOKS.put(hook.getName(), hook);
    return hook;
  }

  /**
   * Registers a new modifier hook
   * @param name             Hook name, must be unique
   * @param filter           Filter for valid classes implementing the hook
   * @param defaultInstance  Default instance of the hook
   * @param merger           Logic to merge multiple hook instances into one
   * @param <T>  Hook type
   * @return  Registered hook
   */
  public static <T> ModifierHook<T> register(ResourceLocation name, Class<T> filter, T defaultInstance, @Nullable Function<Collection<T>,T> merger) {
    return register(new ModifierHook<>(name, filter, defaultInstance, merger));
  }

  /**
   * Registers a new modifier hook that cannot merge
   * @param name             Hook name, must be unique
   * @param filter           Filter for valid classes implementing the hook
   * @param defaultInstance  Default instance of the hook
   * @param <T>  Hook type
   * @return  Registered hook
   */
  public static <T> ModifierHook<T> register(ResourceLocation name, Class<T> filter, T defaultInstance) {
    return register(name, filter, defaultInstance, null);
  }

  /** Gets an unmodifiable view of the list of all registered hooks */
  public static Collection<ResourceLocation> listAllIDs() {
    return HOOK_IDS;
  }
}
