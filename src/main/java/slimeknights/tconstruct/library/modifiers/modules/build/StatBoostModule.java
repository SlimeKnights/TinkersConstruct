package slimeknights.tconstruct.library.modifiers.modules.build;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;
import java.util.Locale;

/** Module that boosts a tool stat */
public record StatBoostModule(INumericToolStat<?> stat, StatOperation operation, LevelingValue amount, ModifierModuleCondition condition) implements ToolStatsModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.TOOL_STATS);

  @Override
  public void addToolStats(ToolRebuildContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    if (condition.matches(context, modifier)) {
      operation.apply(builder, stat, amount.compute(context, modifier));
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

  public static IGenericLoader<StatBoostModule> LOADER = new IGenericLoader<>() {
    @Override
    public StatBoostModule deserialize(JsonObject json) {
      INumericToolStat<?> stat = ToolStats.numericFromJson(GsonHelper.getAsString(json, "stat"));
      StatOperation statOperation = JsonHelper.getAsEnum(json, "operation", StatOperation.class);
      LevelingValue amount = LevelingValue.deserialize(json);
      ModifierModuleCondition condition = ModifierModuleCondition.deserializeFrom(json);
      return new StatBoostModule(stat, statOperation, amount, condition);
    }

    @Override
    public void serialize(StatBoostModule object, JsonObject json) {
      object.condition.serializeInto(json);
      json.addProperty("stat", object.stat.getName().toString());
      json.addProperty("operation", object.operation.getName());
      object.amount.serialize(json);
    }

    @Override
    public StatBoostModule fromNetwork(FriendlyByteBuf buffer) {
      INumericToolStat<?> stat = ToolStats.numericFromNetwork(buffer);
      StatOperation operation = buffer.readEnum(StatOperation.class);
      LevelingValue amount = LevelingValue.fromNetwork(buffer);
      ModifierModuleCondition condition = ModifierModuleCondition.fromNetwork(buffer);
      return new StatBoostModule(stat, operation, amount, condition);
    }

    @Override
    public void toNetwork(StatBoostModule object, FriendlyByteBuf buffer) {
      buffer.writeUtf(object.stat.getName().toString());
      buffer.writeEnum(object.operation);
      object.amount.toNetwork(buffer);
      object.condition.toNetwork(buffer);
    }
  };


  /* Builder */

  /** Creates a builder for adding stats */
  public static Builder add(INumericToolStat<?> stat) {
    return new Builder(stat, StatOperation.ADD);
  }

  /** Creates a builder for adding stats */
  public static Builder multiplyBase(INumericToolStat<?> stat) {
    return new Builder(stat, StatOperation.MULTIPLY_BASE);
  }

  /** Creates a builder for adding stats */
  public static Builder multiplyConditional(INumericToolStat<?> stat) {
    return new Builder(stat, StatOperation.MULTIPLY_CONDITIONAL);
  }

  /** Creates a builder for adding stats */
  public static Builder multiplyAll(INumericToolStat<?> stat) {
    return new Builder(stat, StatOperation.MULTIPLY_ALL);
  }

  /** enum representing a single stat boost */
  public enum StatOperation {
    ADD {
      @Override
      public void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value) {
        stat.add(builder, value);
      }
    },
    MULTIPLY_BASE {
      @Override
      public void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value) {
        stat.multiply(builder, 1 + value);
      }
    },
    MULTIPLY_CONDITIONAL {
      @Override
      public void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value) {
        builder.multiplier(stat, 1 + value);
      }
    },
    MULTIPLY_ALL {
      @Override
      public void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value) {
        stat.multiplyAll(builder, 1 + value);
      }
    };

    @Getter
    private final String name = name().toLowerCase(Locale.ROOT);

    /** Applies this boost type for the given values. */
    public abstract void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModifierModuleCondition.Builder<Builder> implements LevelingValue.Builder<StatBoostModule> {
    private final INumericToolStat<?> stat;
    private final StatOperation operation;

    @Override
    public StatBoostModule amount(float flat, float eachLevel) {
      return new StatBoostModule(stat, operation, new LevelingValue(flat, eachLevel), condition);
    }
  }
}
