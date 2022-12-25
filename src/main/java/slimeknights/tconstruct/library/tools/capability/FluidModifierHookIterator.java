package slimeknights.tconstruct.library.tools.capability;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.ToolFluidCapability.FluidModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Iterator;

/**
 * Shared logic to iterate fluid capabilities for {@link ToolFluidCapability}
 */
abstract class FluidModifierHookIterator<I> extends CompoundIndexHookIterator<FluidModifierHook,I> {
  /** Entry from {@link #findHook(IToolStackView, int)}, will be set during or before iteration */
  protected ModifierEntry indexEntry = null;

  @Override
  protected int getSize(IToolStackView tool, FluidModifierHook hook) {
    return hook.getTanks(tool, indexEntry.getModifier());
  }

  /**
   * Fills the tank with the given resource
   * @param tool     Tool to fill
   * @param resource Resource to fill with
   * @param action   Whether to simulate or execute
   * @return Amount filled
   */
  protected int fill(IToolStackView tool, FluidStack resource, FluidAction action) {
    int totalFilled = 0;
    Iterator<I> iterator = getIterator(tool);
    while(iterator.hasNext()) {
      // try filling each modifier
      int filled = getHook(iterator.next()).fill(tool, indexEntry, resource, action);
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
    return totalFilled;
  }

  /**
   * Drains the tool of the specified resource
   * @param tool     Tool to drain
   * @param resource Resource to drain
   * @param action   Whether to simulate or execute
   * @return Drained resource
   */
  public FluidStack drain(IToolStackView tool, FluidStack resource, FluidAction action) {
    FluidStack drainedSoFar = FluidStack.EMPTY;
    Iterator<I> iterator = getIterator(tool);
    while(iterator.hasNext()) {
      // try draining each modifier
      FluidStack drained = getHook(iterator.next()).drain(tool, indexEntry, resource, action);
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
    return drainedSoFar;
  }

  /**
   * Drains the tool of the given amount
   * @param tool     Tool to drain
   * @param maxDrain Amount to drain
   * @param action   Whether to simulate or execute
   * @return Drained resource
   */
  public FluidStack drain(IToolStackView tool, int maxDrain, FluidAction action) {
    FluidStack drainedSoFar = FluidStack.EMPTY;
    FluidStack toDrain = FluidStack.EMPTY;
    Iterator<I> iterator = getIterator(tool);
    while(iterator.hasNext()) {
      I next = iterator.next();
      // try draining each modifier
      // if we have no drained anything yet, use the type insensitive hook
      if (toDrain.isEmpty()) {
        FluidStack drained = getHook(next).drain(tool, indexEntry, maxDrain, action);
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
        FluidStack drained = getHook(next).drain(tool, indexEntry, toDrain, action);
        if (!drained.isEmpty()) {
          drainedSoFar.grow(drained.getAmount());
          toDrain.shrink(drained.getAmount());
          if (toDrain.isEmpty()) {
            return drainedSoFar;
          }
        }
      }
    }
    return drainedSoFar;
  }
}
