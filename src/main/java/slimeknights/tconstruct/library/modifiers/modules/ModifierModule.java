package slimeknights.tconstruct.library.modifiers.modules;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/** Interface for a module in a composable modifier. This is the serializable version of {@link ModifierHookProvider}. */
public interface ModifierModule extends IHaveLoader<ModifierModule>, ModifierHookProvider {
  /** Loader instance to register new modules. Note that loaders should not use the key "hooks" else composable modifiers will not parse */
  GenericLoaderRegistry<ModifierModule> LOADER = new GenericLoaderRegistry<>();

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

  /** Represents a modifier module with a list of hooks */
  record ModuleWithHooks(ModifierModule module, List<ModifierHook<?>> hooks) {
    /** Gets the list of hooks to use for this module */
    public List<ModifierHook<?>> getModuleHooks() {
      if (hooks.isEmpty()) {
        return module.getDefaultHooks();
      }
      return hooks;
    }

    /** Serializes this to a JSON object */
    public JsonObject serialize() {
      JsonElement json = LOADER.serialize(module);
      if (!json.isJsonObject()) {
        throw new JsonSyntaxException("Serializers for modifier modules must return json objects");
      }
      JsonObject object = json.getAsJsonObject();
      if (!this.hooks.isEmpty()) {
        JsonArray hooks = new JsonArray();
        for (ModifierHook<?> hook : this.hooks) {
          hooks.add(hook.getName().toString());
        }
        object.add("hooks", hooks);
      }
      return object;
    }

    /** Deserializes a module with hooks from a JSON object */
    public static ModuleWithHooks deserialize(JsonObject json) {
      // if there are no hooks in JSON, we use the default list from the module
      List<ModifierHook<?>> hooks = Collections.emptyList();
      if (json.has("hooks")) {
        hooks = JsonHelper.parseList(json, "hooks", (element, key) -> {
          ResourceLocation name = JsonHelper.convertToResourceLocation(element, key) ;
          ModifierHook<?> hook = ModifierHooks.getHook(name);
          if (hook == null) {
            throw new JsonSyntaxException("Unknown modifier hook " + name);
          }
          return hook;
        });
      }
      ModifierModule module = LOADER.deserialize(json);
      return new ModuleWithHooks(module, hooks);
    }

    /** Writes this module to the buffer */
    public void toNetwork(FriendlyByteBuf buffer) {
      buffer.writeVarInt(hooks.size());
      for (ModifierHook<?> hook : hooks) {
        buffer.writeResourceLocation(hook.getName());
      }
      LOADER.toNetwork(module, buffer);
    }

    /** Reads this module from the buffer */
    public static ModuleWithHooks fromNetwork(FriendlyByteBuf buffer) {
      int hookCount = buffer.readVarInt();
      ImmutableList.Builder<ModifierHook<?>> hooks = ImmutableList.builder();
      for (int i = 0; i < hookCount; i++) {
        ResourceLocation location = buffer.readResourceLocation();
        ModifierHook<?> hook = ModifierHooks.getHook(location);
        if (hook == null) {
          throw new DecoderException("Unknown modifier hook " + location);
        }
        hooks.add(hook);
      }
      ModifierModule module = LOADER.fromNetwork(buffer);
      return new ModuleWithHooks(module, hooks.build());
    }
  }

  /**
   * Creates a modifier hook map from the given module list
   * @param modules  List of modules
   * @return  Modifier hook map
   */
  static ModifierHookMap createMap(List<ModuleWithHooks> modules) {
    ModifierHookMap.Builder builder = new ModifierHookMap.Builder();
    for (ModuleWithHooks module : modules) {
      for (ModifierHook<?> hook : module.getModuleHooks()) {
        builder.addHookChecked(module.module(), hook);
      }
    }
    return builder.build();
  }

  /**
   * Helper method to validate generics on the hooks when building a default hooks list. To use, make sure you set the generics instead of leaving it automatic.
   * TODO 1.19: move to {@link ModifierHookProvider}.
   */
  @SafeVarargs
  static <T> List<ModifierHook<?>> defaultHooks(ModifierHook<? super T> ... hooks) {
    return List.of(hooks);
  }
}
