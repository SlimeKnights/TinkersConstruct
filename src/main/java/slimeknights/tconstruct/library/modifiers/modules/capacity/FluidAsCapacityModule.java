package slimeknights.tconstruct.library.modifiers.modules.capacity;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.special.CapacityBarHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module connecting a specific fluid to {@link CapacityBarHook}. Meant to be used on the specific modifier rather than an internal modifier.
 * @param helper  Tank helper to locate the fluid
 * @param fluid   Fluid to match. If the fluid does not match it will be treated as 0.
 */
public record FluidAsCapacityModule(ToolTankHelper helper, Fluid fluid) implements ModifierModule, CapacityBarHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<FluidAsCapacityModule>defaultHooks(ModifierHooks.CAPACITY_BAR);
  public static final RecordLoadable<FluidAsCapacityModule> LOADER = RecordLoadable.create(
    ToolTankHelper.LOADABLE.defaultField("tank_helper", ToolTankHelper.TANK_HELPER, FluidAsCapacityModule::helper),
    Loadables.FLUID.requiredField("fluid", FluidAsCapacityModule::fluid),
    FluidAsCapacityModule::new);

  public FluidAsCapacityModule(Fluid fluid) {
    this(ToolTankHelper.TANK_HELPER, fluid);
  }

  @Override
  public RecordLoadable<FluidAsCapacityModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
  @Override
  public int getAmount(IToolStackView tool) {
    FluidStack fluid = helper.getFluid(tool);
    return !fluid.isEmpty() && fluid.getFluid() == this.fluid ? fluid.getAmount() : 0;
  }

  @Override
  public int getCapacity(IToolStackView tool, ModifierEntry entry) {
    // if another fluid is present, act like the tank is full
    FluidStack fluid = helper.getFluid(tool);
    return fluid.isEmpty() || fluid.getFluid() == this.fluid ? helper.getCapacity(tool) : 0;
  }

  @Override
  public void setAmount(IToolStackView tool, ModifierEntry entry, int amount) {
    // don't allow setting amount if another fluid is present
    FluidStack fluid = helper.getFluid(tool);
    if (fluid.isEmpty() || fluid.getFluid() == this.fluid) {
      helper.setFluid(tool, new FluidStack(fluid, amount));
    }
  }
}
