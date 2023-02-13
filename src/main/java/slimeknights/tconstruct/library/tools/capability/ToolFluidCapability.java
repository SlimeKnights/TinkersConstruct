package slimeknights.tconstruct.library.tools.capability;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * Logic to make a tool a fluid handler
 */
@RequiredArgsConstructor
public class ToolFluidCapability extends FluidModifierHookIterator<ModifierEntry> implements IFluidHandlerItem {
  /** Boolean key to set in volatile mod data to enable the fluid capability */
  public static final ResourceLocation TOTAL_TANKS = TConstruct.getResource("total_tanks");

  /** Modifier hook instance to make an inventory modifier */
  @SuppressWarnings("deprecation")
  public static final ModifierHook<FluidModifierHook> HOOK = ModifierHooks.register(TConstruct.getResource("fluid"), FluidModifierHook.class, new FluidModifierHook() {
    @Override
    public int getTanks(IToolContext tool, Modifier modifier) {
      IFluidModifier hook = modifier.getModule(IFluidModifier.class);
      if (hook != null) {
        return hook.getTanks(tool.getVolatileData());
      }
      return 0;
    }

    @Override
    public FluidStack getFluidInTank(IToolStackView tool, ModifierEntry modifier, int tank) {
      IFluidModifier hook = modifier.getModifier().getModule(IFluidModifier.class);
      if (hook != null) {
        return hook.getFluidInTank(tool, modifier.getLevel(), tank);
      }
      return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(IToolStackView tool, ModifierEntry modifier, int tank) {
      IFluidModifier hook = modifier.getModifier().getModule(IFluidModifier.class);
      if (hook != null) {
        return hook.getTankCapacity(tool, modifier.getLevel(), tank);
      }
      return 0;
    }

    @Override
    public boolean isFluidValid(IToolStackView tool, ModifierEntry modifier, int tank, FluidStack fluid) {
      IFluidModifier hook = modifier.getModifier().getModule(IFluidModifier.class);
      if (hook != null) {
        return hook.isFluidValid(tool, modifier.getLevel(), tank, fluid);
      }
      return false;
    }

    @Override
    public int fill(IToolStackView tool, ModifierEntry modifier, FluidStack resource, FluidAction action) {
      IFluidModifier hook = modifier.getModifier().getModule(IFluidModifier.class);
      if (hook != null) {
        return hook.fill(tool, modifier.getLevel(), resource, action);
      }
      return 0;
    }

    @Override
    public FluidStack drain(IToolStackView tool, ModifierEntry modifier, FluidStack resource, FluidAction action) {
      IFluidModifier hook = modifier.getModifier().getModule(IFluidModifier.class);
      if (hook != null) {
        return hook.drain(tool, modifier.getLevel(), resource, action);
      }
      return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(IToolStackView tool, ModifierEntry modifier, int maxDrain, FluidAction action) {
      IFluidModifier hook = modifier.getModifier().getModule(IFluidModifier.class);
      if (hook != null) {
        return hook.drain(tool, modifier.getLevel(), maxDrain, action);
      }
      return FluidStack.EMPTY;
    }
  }, FluidModifierHookMerger::new);

  @Getter
  private final ItemStack container;
  private final Supplier<? extends IToolStackView> tool;

  /* Basic inventory */

  @Override
  public int getTanks() {
    return tool.get().getVolatileData().getInt(TOTAL_TANKS);
  }

  @Override
  protected Iterator<ModifierEntry> getIterator(IToolStackView tool) {
    return tool.getModifierList().iterator();
  }

  @Override
  protected FluidModifierHook getHook(ModifierEntry entry) {
    indexEntry = entry;
    return entry.getHook(HOOK);
  }

  @Nonnull
  @Override
  public FluidStack getFluidInTank(int tank) {
    IToolStackView tool = this.tool.get();
    FluidModifierHook hook = findHook(tool, tank);
    if (hook != null) {
      return hook.getFluidInTank(tool, indexEntry, tank - startIndex);
    }
    return FluidStack.EMPTY;
  }

  @Override
  public int getTankCapacity(int tank) {
    IToolStackView tool = this.tool.get();
    FluidModifierHook hook = findHook(tool, tank);
    if (hook != null) {
      return hook.getTankCapacity(tool, indexEntry, tank - startIndex);
    }
    return 0;
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    IToolStackView tool = this.tool.get();
    FluidModifierHook hook = findHook(tool, tank);
    if (hook != null) {
      return hook.isFluidValid(tool, indexEntry, tank - startIndex, stack);
    }
    return false;
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    return fill(tool.get(), resource, action);
  }

  @Nonnull
  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    return drain(tool.get(), resource, action);
  }

  @Nonnull
  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    return drain(tool.get(), maxDrain, action);
  }

  /** @deprecated use {@link #addTanks(IToolContext, Modifier, ModDataNBT, FluidModifierHook)} */
  @Deprecated
  public static void addTanks(ModDataNBT volatileData, IFluidModifier modifier) {
    volatileData.putInt(TOTAL_TANKS, modifier.getTanks(volatileData) + volatileData.getInt(TOTAL_TANKS));
  }

  /** Adds the tanks from the fluid modifier to the tool */
  public static void addTanks(IToolContext tool, Modifier modifier, ModDataNBT volatileData, FluidModifierHook hook) {
    volatileData.putInt(TOTAL_TANKS, hook.getTanks(tool, modifier) + volatileData.getInt(TOTAL_TANKS));
  }

  /** @deprecated use {@link FluidModifierHook} */
  @SuppressWarnings("unused")
  @Deprecated
  public interface IFluidModifier {
    /**
     * Determines how many fluid tanks are used by this modifier
     * @param volatileData  Volatile data instance
     * @return  Number of tanks used
     */
    default int getTanks(IModDataView volatileData) {
      return 0;
    }

    /**
     * Gets the fluid in the given tank
     * @param tool   Tool instance
     * @param level  Modifier level
     * @param tank   Tank index
     * @return  Fluid in the given tank
     */
    default FluidStack getFluidInTank(IToolStackView tool, int level, int tank) {
      return FluidStack.EMPTY;
    }

    /**
     * Gets the max capacity for the given tank
     * @param tool   Tool instance
     * @param level  Modifier level
     * @param tank   Tank index
     * @return  Fluid in the given tank
     */
    default int getTankCapacity(IToolStackView tool, int level, int tank) {
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
    default boolean isFluidValid(IToolStackView tool, int level, int tank, FluidStack fluid) {
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
    int fill(IToolStackView tool, int level, FluidStack resource, FluidAction action);

    /**
     * Drains fluid out of tanks, distribution is left entirely to the IFluidHandler.
     * @param tool     Tool instance
     * @param level    Modifier level
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    FluidStack drain(IToolStackView tool, int level, FluidStack resource, FluidAction action);

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     * @param tool     Tool instance
     * @param level    Modifier level
     * @param maxDrain Maximum amount of fluid to drain.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    FluidStack drain(IToolStackView tool, int level, int maxDrain, FluidAction action);
  }

  /** Interface for modifiers with fluid capabilities to return */
  @SuppressWarnings("unused")
  public interface FluidModifierHook {
    /**
     * Determines how many fluid tanks are used by this modifier
     * @param tool      Tool to check
     * @param modifier  Modifier to consider
     * @return  Number of tanks used
     */
    default int getTanks(IToolContext tool, Modifier modifier) {
      return 1;
    }

    /**
     * Gets the fluid in the given tank
     * @param tool      Tool instance
     * @param modifier  Entry instance
     * @param tank      Tank index
     * @return  Fluid in the given tank
     */
    default FluidStack getFluidInTank(IToolStackView tool, ModifierEntry modifier, int tank) {
      return FluidStack.EMPTY;
    }

    /**
     * Gets the max capacity for the given tank
     * @param tool      Tool instance
     * @param modifier  Entry instance
     * @param tank      Tank index
     * @return  Fluid in the given tank
     */
    default int getTankCapacity(IToolStackView tool, ModifierEntry modifier, int tank) {
      return 0;
    }

    /**
     * Checks if the fluid is valid for the given tank
     * @param tool      Tool instance
     * @param modifier  Entry instance
     * @param tank      Tank index
     * @param fluid  Fluid to insert
     * @return  True if the fluid is valid
     */
    default boolean isFluidValid(IToolStackView tool, ModifierEntry modifier, int tank, FluidStack fluid) {
      return true;
    }

    /**
     * Fills fluid into tanks
     * @param tool      Tool instance
     * @param modifier  Entry instance
     * @param resource  FluidStack representing the Fluid and maximum amount of fluid to be filled. If you want to store this stack, make a copy
     * @param action   If SIMULATE, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    int fill(IToolStackView tool, ModifierEntry modifier, FluidStack resource, FluidAction action);

    /**
     * Drains fluid out of tanks, distribution is left entirely to the IFluidHandler.
     * @param tool      Tool instance
     * @param modifier  Entry instance
     * @param resource  FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param action    If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    FluidStack drain(IToolStackView tool, ModifierEntry modifier, FluidStack resource, FluidAction action);

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     * @param tool      Tool instance
     * @param modifier  Entry instance
     * @param maxDrain  Maximum amount of fluid to drain.
     * @param action    If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    FluidStack drain(IToolStackView tool, ModifierEntry modifier, int maxDrain, FluidAction action);
  }

  /** Logic to merge multiple fluid hooks */
  @RequiredArgsConstructor
  private static class FluidModifierHookMerger extends FluidModifierHookIterator<FluidModifierHook> implements FluidModifierHook {
    private final Collection<FluidModifierHook> modules;

    @Override
    protected Iterator<FluidModifierHook> getIterator(IToolStackView tool) {
      return modules.iterator();
    }

    @Override
    protected FluidModifierHook getHook(FluidModifierHook entry) {
      return entry;
    }

    /** Gets the given hook */
    @Nullable
    private FluidModifierHook findHook(IToolStackView tool, ModifierEntry modifier, int tank) {
      indexEntry = modifier;
      return this.findHook(tool, tank);
    }

    @Override
    public int getTanks(IToolContext tool, Modifier modifier) {
      int sum = 0;
      for (FluidModifierHook module : modules) {
        sum += module.getTanks(tool, modifier);
      }
      return sum;
    }

    @Override
    public FluidStack getFluidInTank(IToolStackView tool, ModifierEntry modifier, int tank) {
      FluidModifierHook hook = findHook(tool, modifier, tank);
      if (hook != null) {
        return hook.getFluidInTank(tool, modifier, tank - startIndex);
      }
      return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(IToolStackView tool, ModifierEntry modifier, int tank) {
      FluidModifierHook hook = findHook(tool, modifier, tank);
      if (hook != null) {
        return hook.getTankCapacity(tool, modifier, tank - startIndex);
      }
      return 0;
    }

    @Override
    public boolean isFluidValid(IToolStackView tool, ModifierEntry modifier, int tank, FluidStack fluid) {
      FluidModifierHook hook = findHook(tool, modifier, tank);
      if (hook != null) {
        return hook.isFluidValid(tool, modifier, tank - startIndex, fluid);
      }
      return false;
    }

    @Override
    public int fill(IToolStackView tool, ModifierEntry modifier, FluidStack resource, FluidAction action) {
      indexEntry = modifier;
      return fill(tool, resource, action);
    }

    @Override
    public FluidStack drain(IToolStackView tool, ModifierEntry modifier, FluidStack resource, FluidAction action) {
      indexEntry = modifier;
      return drain(tool, resource, action);
    }

    @Override
    public FluidStack drain(IToolStackView tool, ModifierEntry modifier, int maxDrain, FluidAction action) {
      indexEntry = modifier;
      return drain(tool, maxDrain, action);
    }
  }

  /** Provider instance for a fluid cap */
  public static class Provider implements IToolCapabilityProvider {
    private final LazyOptional<IFluidHandlerItem> fluidCap;
    public Provider(ItemStack stack, Supplier<? extends IToolStackView> toolStack) {
      this.fluidCap = LazyOptional.of(() -> new ToolFluidCapability(stack, toolStack));
    }

    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> cap) {
      if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && tool.getVolatileData().getInt(TOTAL_TANKS) > 0) {
        return fluidCap.cast();
      }
      return LazyOptional.empty();
    }
  }
}
