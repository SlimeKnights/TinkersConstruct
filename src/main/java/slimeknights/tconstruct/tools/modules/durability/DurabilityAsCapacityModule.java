package slimeknights.tconstruct.tools.modules.durability;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.special.CapacityBarHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

/** Module connecting normal tool durability to {@link CapacityBarHook}. Meant to be used on the specific modifier rather than an internal modifier. */
public enum DurabilityAsCapacityModule implements ModifierModule, CapacityBarHook {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<DurabilityAsCapacityModule>defaultHooks(ModifierHooks.CAPACITY_BAR);
  public static final RecordLoadable<DurabilityAsCapacityModule> LOADER = new SingletonLoader<>(INSTANCE);

  @Override
  public RecordLoadable<DurabilityAsCapacityModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public int getAmount(IToolStackView tool) {
    return tool.getCurrentDurability();
  }

  @Override
  public int getCapacity(IToolStackView tool, ModifierEntry entry) {
    return tool.getStats().getInt(ToolStats.DURABILITY);
  }

  @Override
  public void setAmount(IToolStackView tool, ModifierEntry entry, int amount) {
    tool.setDamage(tool.getStats().getInt(ToolStats.DURABILITY) - amount);
  }

  @Override
  public void addAmount(IToolStackView tool, ModifierEntry modifier, int amount) {
    tool.setDamage(tool.getDamage() - amount);
  }

  @Override
  public void removeAmount(IToolStackView tool, ModifierEntry modifier, int amount) {
    tool.setDamage(tool.getDamage() + amount);
  }
}
