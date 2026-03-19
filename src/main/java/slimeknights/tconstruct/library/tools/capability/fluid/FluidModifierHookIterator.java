package slimeknights.tconstruct.library.tools.capability.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.CompoundIndexHookIterator;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolFluidCapability.FluidModifierHook;
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
    return hook.getTanks(tool.getVolatileData(), indexEntry);
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
        // note resource's size has been shrunk which is why totalFilled is not considered here
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
        if (resource.isEmpty()) {
          break;
        }
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
    if (resource.isEmpty()) {
      return FluidStack.EMPTY;
    }
    FluidStack drainedSoFar = FluidStack.EMPTY;
    Iterator<I> iterator = getIterator(tool);
    while(iterator.hasNext()) {
      // try draining each modifier
      FluidStack drained = getHook(iterator.next()).drain(tool, indexEntry, resource, action);
      if (!drained.isEmpty()) {
        // if we managed to drain something, add it into our current drained stack, and decrease the amount we still want to drain
        if (drainedSoFar.isEmpty()) {
          drainedSoFar = drained;
          // if the first success, make a copy of the resource before shrinking it, need to shrink to prevent passing in too much to future hooks
          // though we can skip copying if the first one is all we need
          // note the >= part is just for redundancy, practically its always either = or less than
          if (drained.getAmount() >= resource.getAmount()) {
            break;
          }
          resource = new FluidStack(resource, resource.getAmount() - drained.getAmount());
        } else {
          // resource is guaranteed a copy, and drainedSoFar is a newly created stack, both safe to mutate
          drainedSoFar.grow(drained.getAmount());
          resource.shrink(drained.getAmount());
          // if we drained everything desired, we are done
          if (resource.isEmpty()) {
            break;
          }
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
    if (maxDrain <= 0) {
      return FluidStack.EMPTY;
    }
    FluidStack drainedSoFar = FluidStack.EMPTY;
    FluidStack toDrain = FluidStack.EMPTY;
    Iterator<I> iterator = getIterator(tool);
    while(iterator.hasNext()) {
      FluidModifierHook hook = getHook(iterator.next());
      // try draining each modifier
      // if we have not drained anything yet, use the type insensitive hook
      if (toDrain.isEmpty()) {
        FluidStack drained = hook.drain(tool, indexEntry, maxDrain, action);
        if (!drained.isEmpty()) {
          drainedSoFar = drained;
          // if we finished draining, we are done, otherwise we need to create a filter for future drain attempts
          // note the >= part is just for redundancy, practically its always either = or less than
          if (drained.getAmount() >= maxDrain) {
            break;
          }
          toDrain = new FluidStack(drained, maxDrain - drained.getAmount());
        }
      } else {
        // if we already drained some fluid, type sensitive and increase our results
        FluidStack drained = hook.drain(tool, indexEntry, toDrain, action);
        if (!drained.isEmpty()) {
          drainedSoFar.grow(drained.getAmount());
          toDrain.shrink(drained.getAmount());
          // if we drained everything desired, we are done
          if (toDrain.isEmpty()) {
            break;
          }
        }
      }
    }
    return drainedSoFar;
  }
}
