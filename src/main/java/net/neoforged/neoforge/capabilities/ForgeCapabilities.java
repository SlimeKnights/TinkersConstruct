package slimeknights.tconstruct.compat.neoforged.neoforge.capabilities;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import slimeknights.mantle.compat.neoforged.neoforge.capabilities.Capability;

/** Compatibility shim for old Forge capability constants. */
public final class ForgeCapabilities {
  private ForgeCapabilities() {}

  public static final Capability<IFluidHandler> FLUID_HANDLER = new Capability<>();
  public static final Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM = new Capability<>();
  public static final Capability<IItemHandler> ITEM_HANDLER = new Capability<>();
  public static final Capability<IEnergyStorage> ENERGY = new Capability<>();
}
