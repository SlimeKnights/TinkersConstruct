package slimeknights.tconstruct.library.tools.definition.module;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map.Entry;

/**
 * Base interface for modules within the tool definition data
 */
public interface IToolModule extends IHaveLoader<IToolModule> {
  /** Loader instance for any modules loadable in tools */
  GenericLoaderRegistry<IToolModule> LOADER = new GenericLoaderRegistry<>();

  /** Reads the module map from the buffer */
  static ModifierHookMap read(FriendlyByteBuf buffer) {
    int size = buffer.readVarInt();
    ModifierHookMap.Builder builder = new ModifierHookMap.Builder();
    for (int i = 0; i < size; i++) {
      ResourceLocation hookName = buffer.readResourceLocation();
      ModifierHook<?> hook = ModifierHooks.getHook(hookName);
      if (hook == null) {
        throw new DecoderException("Unknown hook from network, this likely indicates a broken or outdated mod: " + hookName);
      }
      IToolModule module = LOADER.fromNetwork(buffer);
      builder.addHookChecked(module, hook);
    }
    return builder.build();
  }

  /** Writes the module map to the buffer */
  static void write(ModifierHookMap modules, FriendlyByteBuf buffer) {
    // need to filter first else the count will be wrong and break the buffer
    Collection<Entry<ModifierHook<?>, Object>> entries = modules.getAllModules().entrySet().stream().filter(entry -> entry.getValue() instanceof IToolModule).toList();
    buffer.writeVarInt(entries.size());
    for (Entry<ModifierHook<?>, Object> entry : entries) {
      buffer.writeResourceLocation(entry.getKey().getName());
      LOADER.toNetwork((IToolModule) entry.getValue(), buffer);
    }
  }

  /** Logic to serialize and deserialize tool actions */
  enum Serializer implements JsonSerializer<ModifierHookMap>, JsonDeserializer<ModifierHookMap> {
    INSTANCE;

    @Override
    public ModifierHookMap deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject json = GsonHelper.convertToJsonObject(element, "modules");
      ModifierHookMap.Builder builder = new ModifierHookMap.Builder();
      for (Entry<String,JsonElement> entry : json.entrySet()) {
        ResourceLocation hookName = ResourceLocation.tryParse(entry.getKey());
        if (hookName == null) {
          throw new JsonSyntaxException("Invalid hook name " + entry.getKey());
        }
        ModifierHook<?> hook = ModifierHooks.getHook(hookName);
        if (hook == null) {
          throw new JsonSyntaxException("Unknown hook name " + hookName);
        }
        builder.addHookChecked(LOADER.deserialize(entry.getValue()), hook);
      }
      return builder.build();
    }

    @Override
    public JsonElement serialize(ModifierHookMap src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      for (Entry<ModifierHook<?>, Object> entry : src.getAllModules().entrySet()) {
        if (entry.getValue() instanceof IToolModule module) {
          json.add(entry.getKey().getName().toString(), LOADER.serialize(module));
        }
      }
      return json;
    }
  }
}
