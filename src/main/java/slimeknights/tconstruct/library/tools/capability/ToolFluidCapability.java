package slimeknights.tconstruct.library.tools.capability;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

/**
 * Logic to make a tool a fluid handler
 */
@RequiredArgsConstructor
public class ToolFluidCapability implements IFluidHandlerItem {
  /** Boolean key to set in volatile mod data to enable the fluid capability */
  public static final ResourceLocation HAS_CAPABILITY = TConstruct.getResource("has_fluid_capability");
  /** Boolean key to set in volatile mod data to enable the fluid capability */
  public static final ResourceLocation TOTAL_TANKS = TConstruct.getResource("total_tanks");

  @Getter
  private final ItemStack container;
  private final ToolStack tool;

  @Override
  public int getTanks() {
    return tool.getVolatileData().getInt(TOTAL_TANKS);
  }

  /**
   * Runs a fluid handler function for a tank index
   * @param tank          Tank index
   * @param function      Function to run
   * @param defaultValue  Default value if none of the modifiers have the proper tank index
   * @param <T>  Return type
   * @return  Value from the modifiers
   */
  private <T> T runForTank(int tank, T defaultValue, ITankCallback<T> function) {
    for (ModifierEntry entry : tool.getModifierList()) {
      IFluidModifier fluidModifier = entry.getModifier().getModule(IFluidModifier.class);
      if (fluidModifier != null) {
        int currentTanks = fluidModifier.getTanks(tool.getVolatileData());
        if (tank < currentTanks) {
          return function.run(fluidModifier, tool, entry.getLevel(), tank);
        }
        // subtract tanks in the current modifier, tank is 0 indexed from the modifier
        tank -= currentTanks;
      }
    }
    return defaultValue;
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return runForTank(tank, FluidStack.EMPTY, IFluidModifier::getFluidInTank);
  }

  @Override
  public int getTankCapacity(int tank) {
    return runForTank(tank, 0, IFluidModifier::getTankCapacity);
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return runForTank(tank, false, (module, tool, level, tank1) -> module.isFluidValid(tool, level, tank1, stack));
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    int totalFilled = 0;
    for (ModifierEntry entry : tool.getModifierList()) {
      IFluidModifier fluidModifier = entry.getModifier().getModule(IFluidModifier.class);
      if (fluidModifier != null) {
        // try filling each modifier
        int filled = fluidModifier.fill(tool, entry.getLevel(), resource, action);
        if (filled > 0) {
          // if we filled the entire stack, we are done
          if (filled >= resource.getAmount()) {
            return totalFilled + filled;
          }
          // if this is our first successful fill, copy the resource to prevent changing the original stack
          if (totalFilled == 0) {
            resource = resource.copy();
          }
          // increase total and shrink the resource for next time
          totalFilled += filled;
          resource.shrink(filled);
        }
      }
    }
    return totalFilled;
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    FluidStack drainedSoFar = FluidStack.EMPTY;
    for (ModifierEntry entry : tool.getModifierList()) {
      IFluidModifier fluidModifier = entry.getModifier().getModule(IFluidModifier.class);
      if (fluidModifier != null) {
        // try draining each modifier
        FluidStack drained = fluidModifier.drain(tool, entry.getLevel(), resource, action);
        if (!drained.isEmpty()) {
          // if we managed to drain something, add it into our current drained stack, and decrease the amount we still want to drain
          if (drainedSoFar.isEmpty()) {
            // if the first time, make a copy of the resource before changing it
            // though we can skip copying if the first one is all we need
            if (drained.getAmount() >= resource.getAmount()) {
              return drained;
            } else {
              drainedSoFar = drained;
              resource = resource.copy();
            }
          } else {
            drainedSoFar.grow(drained.getAmount());
          }
          // if we drained everything desired, return
          resource.shrink(drained.getAmount());
          if (resource.isEmpty()) {
            return drainedSoFar;
          }
        }
      }
    }
    return drainedSoFar;
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    FluidStack drainedSoFar = FluidStack.EMPTY;
    FluidStack toDrain = FluidStack.EMPTY;
    for (ModifierEntry entry : tool.getModifierList()) {
      IFluidModifier fluidModifier = entry.getModifier().getModule(IFluidModifier.class);
      if (fluidModifier != null) {
        // try draining each modifier
        // if we have no drained anything yet, use the type insensitive hook
        if (toDrain.isEmpty()) {
          FluidStack drained = fluidModifier.drain(tool, entry.getLevel(), maxDrain, action);
          if (!drained.isEmpty()) {
            // if we finished draining, we are done, otherwise try again later with the type senstive hooks
            maxDrain -= drained.getAmount();
            if (maxDrain > 0) {
              drainedSoFar = drained;
              toDrain = new FluidStack(drained, maxDrain);
            } else {
              return drained;
            }
          }
        } else {
          // if we already drained some fluid, type sensitive and increase our results
          FluidStack drained = fluidModifier.drain(tool, entry.getLevel(), toDrain, action);
          if (!drained.isEmpty()) {
            drainedSoFar.grow(drained.getAmount());
            toDrain.shrink(drained.getAmount());
            if (toDrain.isEmpty()) {
              return drainedSoFar;
            }
          }
        }
      }
    }
    return drainedSoFar;
  }

  /** Adds the tanks from the fluid modifier to the tool */
  public static void addTanks(ModDataNBT volatileData, IFluidModifier modifier) {
    volatileData.putBoolean(HAS_CAPABILITY, true);
    volatileData.putInt(TOTAL_TANKS, modifier.getTanks(volatileData) + volatileData.getInt(TOTAL_TANKS));
  }

  /** Interface for modifiers with fluid capabilities to return */
  public interface IFluidModifier {
    /**
     * Determines how many fluid tanks are used by this modifier
     * @param volatileData  Volatile data instance
     * @return  Number of tanks used
     */
    default int getTanks(IModDataReadOnly volatileData) {
      return 0;
    }

    /**
     * Gets the fluid in the given tank
     * @param tool   Tool instance
     * @param level  Modifier level
     * @param tank   Tank index
     * @return  Fluid in the given tank
     */
    default FluidStack getFluidInTank(IModifierToolStack tool, int level, int tank) {
      return FluidStack.EMPTY;
    }

    /**
     * Gets the max capacity for the given tank
     * @param tool   Tool instance
     * @param level  Modifier level
     * @param tank   Tank index
     * @return  Fluid in the given tank
     */
    default int getTankCapacity(IModifierToolStack tool, int level, int tank) {
      return 0;
    }

    /**
     * Checks if the fluid is valid for the given tank
     * @param tool   Tool instance
     * @param level  Modifier level
     * @param tank   Tank index
     * @param fluid  Fluid to insert
     * @return  True if the fluid is valid
     */
    default boolean isFluidValid(IModifierToolStack tool, int level, int tank, FluidStack fluid) {
      return true;
    }

    /**
     * Fills fluid into tanks
     * @param tool     Tool instance
     * @param level    Modifier level
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled. If you want to store this stack, make a copy
     * @param action   If SIMULATE, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    int fill(IModifierToolStack tool, int level, FluidStack resource, FluidAction action);

    /**
     * Drains fluid out of tanks, distribution is left entirely to the IFluidHandler.
     * @param tool     Tool instance
     * @param level    Modifier level
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    FluidStack drain(IModifierToolStack tool, int level, FluidStack resource, FluidAction action);

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     * @param tool     Tool instance
     * @param level    Modifier level
     * @param maxDrain Maximum amount of fluid to drain.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    FluidStack drain(IModifierToolStack tool, int level, int maxDrain, FluidAction action);
  }

  /** Helper to run a function from {@link IFluidModifier} */
  @FunctionalInterface
  private interface ITankCallback<T> {
    T run(IFluidModifier module, IModifierToolStack tool, int level, int tank);
  }
}
