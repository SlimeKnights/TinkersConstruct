package slimeknights.tconstruct.library.modifiers.modules.build;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

/**
 * Module that sets a stat to a particular value
 * @param <T>  Stat type
 */
public record SetStatModule<T>(IToolStat<T> stat, T value, ModifierCondition<IToolContext> condition) implements ModifierModule, ToolStatsModifierHook, ConditionalModule<IToolContext> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SetStatModule<?>>defaultHooks(ModifierHooks.TOOL_STATS);

  @Override
  public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    if (condition.matches(context, modifier)) {
      stat.update(builder, value);
    }
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<SetStatModule<?>> getLoader() {
    return LOADER;
  }

  /** Loader instance, manually created as the value parsing another value is difficult with the builder */
  public static final RecordLoadable<SetStatModule<?>> LOADER = new RecordLoadable<>() {
    @Override
    public SetStatModule<?> deserialize(JsonObject json, TypedMap context) {
      return deserialize(json, ToolStats.LOADER.getIfPresent(json, "stat", context), context);
    }

    /** Handles generics for deserializing the value */
    private static <T> SetStatModule<T> deserialize(JsonObject json, IToolStat<T> stat, TypedMap context) {
      return new SetStatModule<>(
        stat,
        stat.deserialize(JsonHelper.getElement(json, "value")),
        ModifierCondition.CONTEXT_FIELD.get(json, context)
      );
    }

    @Override
    public void serialize(SetStatModule<?> object, JsonObject json) {
      ModifierCondition.CONTEXT_FIELD.serialize(object, json);
      json.add("stat", ToolStats.LOADER.serialize(object.stat));
      serializeValue(object, json);
    }

    /** Handles generics for serializing the value */
    private static <T> void serializeValue(SetStatModule<T> module, JsonObject json) {
      json.add("value", module.stat.serialize(module.value));
    }

    @Override
    public SetStatModule<?> decode(FriendlyByteBuf buffer, TypedMap context) {
      return decode(buffer, ToolStats.LOADER.decode(buffer, context), context);
    }

    /** Handles generics for reading the value from network */
    private static <T> SetStatModule<T> decode(FriendlyByteBuf buffer, IToolStat<T> stat, TypedMap context) {
      return new SetStatModule<>(
        stat, stat.fromNetwork(buffer),
        ModifierCondition.CONTEXT_FIELD.decode(buffer, context)
      );
    }

    @Override
    public void encode(FriendlyByteBuf buffer, SetStatModule<?> object) {
      ToolStats.LOADER.encode(buffer, object.stat);
      writeValue(object, buffer);
      ModifierCondition.CONTEXT_FIELD.encode(buffer, object);
    }

    /** Handles generics for writing the value to network */
    private static <T> void writeValue(SetStatModule<T> object, FriendlyByteBuf buffer) {
      object.stat.toNetwork(buffer, object.value);
    }
  };


  /* Builder */

  /** Creates a builder for the given stat */
  public static <T> Builder<T> set(IToolStat<T> stat) {
    return new Builder<>(stat);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder<T> extends ModuleBuilder.Context<Builder<T>> {
    private final IToolStat<T> stat;

    /** Creates the instance with the passed value */
    public SetStatModule<T> value(T value) {
      return new SetStatModule<>(stat, value, condition);
    }
  }
}
