package slimeknights.tconstruct.library.modifiers.modules.build;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

/**
 * Module that sets a stat to a particular value
 * @param <T>  Stat type
 */
public record SetStatModule<T>(IToolStat<T> stat, T value, ModifierModuleCondition condition) implements ModifierModule, ToolStatsModifierHook {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.TOOL_STATS);

  @Override
  public void addToolStats(ToolRebuildContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    if (condition.matches(context, modifier)) {
      stat.update(builder, value);
    }
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<SetStatModule<?>> LOADER = new IGenericLoader<>() {
    @Override
    public SetStatModule<?> deserialize(JsonObject json) {
      IToolStat<?> stat = ToolStats.fromJson(GsonHelper.getAsString(json, "stat"));
      return deserialize(json, stat);
    }

    /** Handles generics for deserializing the value */
    private static <T> SetStatModule<T> deserialize(JsonObject json, IToolStat<T> stat) {
      ModifierModuleCondition condition = ModifierModuleCondition.deserializeFrom(json);
      T value = stat.deserialize(JsonHelper.getElement(json, "value"));
      return new SetStatModule<>(stat, value, condition);
    }

    @Override
    public void serialize(SetStatModule<?> object, JsonObject json) {
      object.condition.serializeInto(json);
      json.addProperty("stat", object.stat.getName().toString());
      serializeValue(object, json);
    }

    /** Handles generics for serializing the value */
    private static <T> void serializeValue(SetStatModule<T> module, JsonObject json) {
      json.add("value", module.stat.serialize(module.value));
    }

    @Override
    public SetStatModule<?> fromNetwork(FriendlyByteBuf buffer) {
      IToolStat<?> stat = ToolStats.fromNetwork(buffer);
      return fromNetwork(buffer, stat);
    }

    /** Handles generics for reading the value from network */
    private static <T> SetStatModule<T> fromNetwork(FriendlyByteBuf buffer, IToolStat<T> stat) {
      T value = stat.fromNetwork(buffer);
      ModifierModuleCondition condition = ModifierModuleCondition.fromNetwork(buffer);
      return new SetStatModule<>(stat, value, condition);
    }

    @Override
    public void toNetwork(SetStatModule<?> object, FriendlyByteBuf buffer) {
      buffer.writeUtf(object.stat.getName().toString());
      writeValue(object, buffer);
      object.condition.toNetwork(buffer);
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
  public static class Builder<T> extends ModifierModuleCondition.Builder<Builder<T>> {
    private final IToolStat<T> stat;

    /** Creates the instance with the passed value */
    public SetStatModule<T> value(T value) {
      return new SetStatModule<>(stat, value, condition);
    }
  }
}
