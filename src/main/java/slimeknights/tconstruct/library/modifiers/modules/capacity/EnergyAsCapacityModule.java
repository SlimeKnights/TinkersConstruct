package slimeknights.tconstruct.library.modifiers.modules.capacity;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.special.CapacityBarHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.ToolEnergyCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Module connecting Forge Energy to {@link CapacityBarHook}. Meant to be used on the specific modifier rather than an internal modifier. */
public enum EnergyAsCapacityModule implements ModifierModule, CapacityBarHook {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<EnergyAsCapacityModule>defaultHooks(ModifierHooks.CAPACITY_BAR);
  public static final RecordLoadable<EnergyAsCapacityModule> LOADER = new SingletonLoader<>(INSTANCE);

  @Override
  public RecordLoadable<EnergyAsCapacityModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public int getAmount(IToolStackView tool) {
    return ToolEnergyCapability.getEnergy(tool);
  }

  @Override
  public int getCapacity(IToolStackView tool, ModifierEntry entry) {
    return ToolEnergyCapability.getMaxEnergy(tool);
  }

  @Override
  public void setAmount(IToolStackView tool, ModifierEntry entry, int amount) {
    ToolEnergyCapability.setEnergy(tool, amount);
  }

  @Override
  public void addAmount(IToolStackView tool, ModifierEntry modifier, int amount) {
    ToolEnergyCapability.addEnergy(tool, amount);
  }

  @Override
  public void removeAmount(IToolStackView tool, ModifierEntry modifier, int amount) {
    ToolEnergyCapability.addEnergy(tool, -amount);
  }
}
