package slimeknights.tconstruct.library.modifiers.modules.capacity;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/** Module that adds capacity whenever the tool breaks any number of blocks. */
public record MiningCapacityModule(LevelingInt grant, boolean before, @Nullable ModifierId owner, ModifierCondition<IToolStackView> condition) implements ModifierModule, BlockHarvestModifierHook, CapacitySourceModule, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MiningCapacityModule>defaultHooks(ModifierHooks.BLOCK_HARVEST);
  public static final RecordLoadable<MiningCapacityModule> LOADER = RecordLoadable.create(
    LevelingInt.LOADABLE.defaultField("grant", LevelingInt.ZERO, false, MiningCapacityModule::grant),
    BooleanLoadable.INSTANCE.defaultField("run_before", false, true, MiningCapacityModule::before),
    OWNER_FIELD, ModifierCondition.TOOL_FIELD,
    MiningCapacityModule::new);

  /** @apiNote use {@link #builder()} */
  @Internal
  public MiningCapacityModule {}

  @Override
  public RecordLoadable<MiningCapacityModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Applies the capacity boost to the modifier. */
  private void apply(IToolStackView tool, ModifierEntry modifier, int harvested) {
    if (condition.matches(tool, modifier)) {
      CapacitySourceModule.apply(tool, barModifier(tool, modifier), harvested, grant.compute(modifier.getEffectiveLevel()));
    }
  }

  @Override
  public void startHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
    if (before) {
      apply(tool, modifier, 1);
    }
  }

  @Override
  public void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, int harvested) {
    if (!before) {
      apply(tool, modifier, harvested);
    }
  }


  /* Builder */

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }

  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends CapacitySourceModule.Builder<Builder> implements LevelingInt.Builder<MiningCapacityModule>  {
    /** If true, runs in the before hook instead of the after */
    private boolean before = false;

    /** Sets the builder to run before mining. */
    public Builder before() {
      return before(true);
    }

    @Override
    public MiningCapacityModule amount(int flat, int eachLevel) {
      return new MiningCapacityModule(new LevelingInt(flat, eachLevel), before, owner, condition);
    }
  }
}
