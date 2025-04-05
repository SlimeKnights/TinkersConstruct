package slimeknights.tconstruct.library.modifiers.modules.build;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
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
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

/** Module that copies from one stat to another */
public record StatCopyModule(INumericToolStat<?> target, INumericToolStat<?> source, LevelingValue percentage, ModifierCondition<IToolContext> condition) implements ToolStatsModifierHook, ModifierModule, ConditionalModule<IToolContext> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<StatBoostModule>defaultHooks(ModifierHooks.TOOL_STATS);
  public static RecordLoadable<StatCopyModule> LOADER = RecordLoadable.create(
    ToolStats.NUMERIC_LOADER.requiredField("target", StatCopyModule::target),
    ToolStats.NUMERIC_LOADER.requiredField("source", StatCopyModule::source),
    LevelingValue.LOADABLE.directField(StatCopyModule::percentage),
    ModifierCondition.CONTEXT_FIELD,
    StatCopyModule::new);

  /** @apiNote Internal constructor, use {@link #builder(INumericToolStat, INumericToolStat)} */
  @Internal
  public StatCopyModule {}

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<StatCopyModule> getLoader() {
    return LOADER;
  }

  @Override
  public Integer getPriority() {
    // needs to run late to ensure it gets most stats
    return 50;
  }

  @Override
  public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    if (condition.matches(context, modifier)) {
      target.add(builder, builder.getStat(source).floatValue() * percentage.compute(modifier.getEffectiveLevel()) / builder.getMultiplier(source));
    }
  }

  /** Creates a builder copying from source to target */
  public static Builder builder(INumericToolStat<?> target, INumericToolStat<?> source) {
    return new Builder(target, source);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModuleBuilder.Context<Builder> implements LevelingValue.Builder<StatCopyModule> {
    private final INumericToolStat<?> target;
    private final INumericToolStat<?> source;

    @Override
    public StatCopyModule amount(float flat, float eachLevel) {
      return new StatCopyModule(target, source, new LevelingValue(flat, eachLevel), condition);
    }
  }
}
