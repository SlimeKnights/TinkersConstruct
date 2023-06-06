package slimeknights.tconstruct.library.modifiers.modules;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierStatBoost;
import slimeknights.tconstruct.library.modifiers.util.ModifierStatBoost.BoostType;
import slimeknights.tconstruct.library.modifiers.util.ModifierStatBoost.StatBoost;
import slimeknights.tconstruct.library.modifiers.util.ModifierStatBoost.StatUpdate;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;

/**
 * Module that applies a stat boost on a tool
 */
public record ToolStatModule(ModifierStatBoost boost) implements ToolStatsModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.TOOL_STATS);

  @Override
  public void addToolStats(ToolRebuildContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    boost.apply(context, modifier.getEffectiveLevel(context), builder);
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<ToolStatModule> LOADER = new IGenericLoader<>() {
    @Override
    public ToolStatModule deserialize(JsonObject json) {
      return new ToolStatModule(ModifierStatBoost.fromJson(json));
    }

    @Override
    public ToolStatModule fromNetwork(FriendlyByteBuf buffer) {
      return new ToolStatModule(ModifierStatBoost.fromNetwork(buffer));
    }

    @Override
    public void serialize(ToolStatModule object, JsonObject json) {
      object.boost.toJson(json);
    }

    @Override
    public void toNetwork(ToolStatModule object, FriendlyByteBuf buffer) {
      object.boost.toNetwork(buffer);
    }
  };


  /* Helpers */

  /** Updates a stat in the builder */
  @SafeVarargs
  public static <T> ToolStatModule update(IToolStat<T> stat, T value, TagKey<Item>... tagRequirements) {
    return new ToolStatModule(new StatUpdate<>(stat, value, List.of(tagRequirements)));
  }

  /** Adds a general boost */
  private static ToolStatModule boost(INumericToolStat<?> stat, BoostType type, float amount, TagKey<Item>[] tagRequirements) {
    return new ToolStatModule(new StatBoost(stat, type, amount, List.of(tagRequirements)));
  }

  /** Adds a numeric boost */
  @SafeVarargs
  public static ToolStatModule add(INumericToolStat<?> stat, float amount, TagKey<Item>... tagRequirements) {
    return boost(stat, ModifierStatBoost.BoostType.ADD, amount, tagRequirements);
  }

  /** Multiplies the base value of a stat */
  @SafeVarargs
  public static ToolStatModule multiplyBase(INumericToolStat<?> stat, float amount, TagKey<Item>... tagRequirements) {
    return boost(stat, ModifierStatBoost.BoostType.MULTIPLY_BASE, amount, tagRequirements);
  }

  /** Multiplies conditional boosts */
  @SafeVarargs
  public static ToolStatModule multiplyConditional(INumericToolStat<?> stat, float amount, TagKey<Item>... tagRequirements) {
    return boost(stat, ModifierStatBoost.BoostType.MULTIPLY_CONDITIONAL, amount, tagRequirements);
  }

  /** Multiplies both base and conditional boosts */
  @SafeVarargs
  public static ToolStatModule multiplyAll(INumericToolStat<?> stat, float amount, TagKey<Item>... tagRequirements) {
    return boost(stat, ModifierStatBoost.BoostType.MULTIPLY_ALL, amount, tagRequirements);
  }
}
