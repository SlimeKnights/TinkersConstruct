package slimeknights.tconstruct.library.modifiers.modules.capacity;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.special.CapacityBarHook;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.OvergrowthModule;

/** Module with a chance to fill the capacity bar each second */
public class TimeToCapacityModule extends OvergrowthModule {
  public static final RecordLoadable<TimeToCapacityModule> LOADER = RecordLoadable.create(CHANCE_FIELD, ModifierCondition.TOOL_FIELD, TimeToCapacityModule::new);

  public TimeToCapacityModule(LevelingValue chance, ModifierCondition<IToolStackView> condition) {
    super(chance, condition);
  }

  @Override
  public RecordLoadable<TimeToCapacityModule> getLoader() {
    return LOADER;
  }

  @Override
  protected CapacityBarHook getBar(ModifierEntry modifier) {
    return modifier.getHook(ModifierHooks.CAPACITY_BAR);
  }
}
