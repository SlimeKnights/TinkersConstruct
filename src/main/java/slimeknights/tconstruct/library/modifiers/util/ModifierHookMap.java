package slimeknights.tconstruct.library.modifiers.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import lombok.RequiredArgsConstructor;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.BasicModifier;
import slimeknights.tconstruct.library.modifiers.modules.ModifierHookProvider;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

/** Map working with modifier hooks that automatically maps the objects to the correct generics. */
@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
public class ModifierHookMap {
  /** Instance with no modifiers */
  public static final ModifierHookMap EMPTY = new ModifierHookMap(Collections.emptyMap());

  /** Internal map of modifier hook to object. It's the caller's responsibility to make sure the object is valid for the hook */
  private final Map<ModifierHook<?>,Object> modules;

  /** Checks if a module is registered for the given hook */
  public boolean hasHook(ModifierHook<?> hook) {
    return modules.containsKey(hook);
  }

  /** Gets the module matching the given hook, or null if not defined */
  @SuppressWarnings("unchecked")
  @Nullable
  public <T> T getOrNull(ModifierHook<T> hook) {
    return (T)modules.get(hook);
  }

  /** Gets the module matching the given hook */
  public <T> T getOrDefault(ModifierHook<T> hook) {
    T object = getOrNull(hook);
    if (object != null) {
      return object;
    }
    return hook.getDefaultInstance();
  }

  /** Gets an unchecked view of all internal modules for the sake of serialization */
  public Map<ModifierHook<?>,Object> getAllModules() {
    return modules;
  }

  @SuppressWarnings("UnusedReturnValue")
  public static class Builder {
    /** Builder for the final map */
    private final LinkedHashMultimap<ModifierHook<?>,Object> modules = LinkedHashMultimap.create();

    /**
     * Adds a module to the builder, validating it at runtime. Used for JSON parsing
     * @throws IllegalArgumentException  if the hook type is invalid
     */
    @SuppressWarnings("UnusedReturnValue")
    public Builder addHookChecked(Object object, ModifierHook<?> hook) {
      if (hook.isValid(object)) {
        modules.put(hook, object);
      } else {
        throw new IllegalArgumentException("Object " + object + " is invalid for hook " + hook);
      }
      return this;
    }

    /** Adds a modifier module to the builder, automatically adding all its hooks. Use {@link #addHook(Object, ModifierHook)} to specify hooks. */
    public Builder addModule(ModifierHookProvider module) {
      for (ModifierHook<?> hook : module.getDefaultHooks()) {
        addHookChecked(module, hook);
      }
      return this;
    }

    /** Adds a module to the builder */
    @SuppressWarnings("UnusedReturnValue")
    public <H, T extends H> Builder addHook(T object, ModifierHook<H> hook) {
      modules.put(hook, object);
      return this;
    }

    /** Adds a module to the builder that implements multiple hooks */
    public <T> Builder addHook(T object, ModifierHook<? super T> hook1, ModifierHook<? super T> hook2) {
      addHook(object, hook1);
      addHook(object, hook2);
      return this;
    }

    /** Adds a module to the builder that implements multiple hooks */
    public <T> Builder addHook(T object, ModifierHook<? super T> hook1, ModifierHook<? super T> hook2, ModifierHook<? super T> hook3) {
      addHook(object, hook1);
      addHook(object, hook2);
      addHook(object, hook3);
      return this;
    }

    /** Adds a module to the builder that implements multiple hooks */
    @SafeVarargs
    public final <T> Builder addHook(T object, ModifierHook<? super T>... hooks) {
      for (ModifierHook<? super T> hook : hooks) {
        addHook(object, hook);
      }
      return this;
    }

    /** Helper to deal with generics */
    @SuppressWarnings("unchecked")
    private static <T> void insert(ImmutableMap.Builder<ModifierHook<?>,Object> builder, ModifierHook<T> hook, Collection<Object> objects) {
      if (objects.size() == 1) {
        builder.put(hook, objects.iterator().next());
      } else if (!objects.isEmpty()) {
        builder.put(hook, hook.merge((Collection<T>)objects));
      }
    }

    /** Builds the final map */
    public ModifierHookMap build() {
      if (modules.isEmpty()) {
        return EMPTY;
      }
      ImmutableMap.Builder<ModifierHook<?>,Object> builder = ImmutableMap.builder();
      for (Entry<ModifierHook<?>,Collection<Object>> entry : modules.asMap().entrySet()) {
        insert(builder, entry.getKey(), entry.getValue());
      }
      return new ModifierHookMap(builder.build());
    }

    /** Transitions this builder into a basic modifier builder */
    public BasicModifier.Builder modifier() {
      return BasicModifier.Builder.builder(build());
    }
  }
}
