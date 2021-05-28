package slimeknights.tconstruct.library.modifiers;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.capability.ToolFluidCapability;
import slimeknights.tconstruct.library.modifiers.capability.ToolFluidCapability.IFluidModifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;

/** Modifier containing the standard tank, extend if you want to share this tank */
public class TankModifier extends Modifier {
  private static final String FILLED_KEY = Util.makeTranslationKey("modifier", "tank.filled");
  private static final String CAPACITY_KEY = Util.makeTranslationKey("modifier", "tank.capacity");

  /** Volatile NBT string indicating which modifier is in charge of logic for the one tank */
  private static final ResourceLocation OWNER = Util.getResource("tank_owner");
  /** Volatile NBT integer indicating the tank's max capacity */
  private static final ResourceLocation CAPACITY = Util.getResource("tank_capacity");
  /** Persistent NBT compound containing the fluid in the tank */
  private static final ResourceLocation FLUID = Util.getResource("tank_fluid");

  /** Helper function to parse a fluid from NBT */
  public static final BiFunction<CompoundNBT, String, FluidStack> PARSE_FLUID = (nbt, key) -> FluidStack.loadFluidStackFromNBT(nbt.getCompound(key));

  private final ModifierTank tank = new ModifierTank();
  private final int capacity;
  public TankModifier(int color, int capacity) {
    super(color);
    this.capacity = capacity;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    if (type == IFluidModifier.class) {
      return (T) tank;
    }
    return super.getModule(type);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    // set owner first
    ResourceLocation ownerKey = getOwnerKey();
    if (ownerKey != null && !volatileData.contains(ownerKey, NBT.TAG_STRING)) {
      volatileData.putString(ownerKey, getId().toString());
    }
    ToolFluidCapability.addTanks(volatileData, tank);
    if (capacity > 0) {
      addCapacity(volatileData, capacity * level);
    }
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    if (isOwner(tool)) {
      FluidStack current = getFluid(tool);
      if (!current.isEmpty()) {
        tooltip.add(new TranslationTextComponent(FILLED_KEY, current.getAmount(), current.getDisplayName()));
      }
      tooltip.add(new TranslationTextComponent(CAPACITY_KEY, getCapacity(tool)));
    }
  }


  /* Resource location keys */

  /** Overridable method to change the owner key */
  @Nullable
  public ResourceLocation getOwnerKey() {
    return OWNER;
  }

  /** Overridable method to change the capacity key */
  public ResourceLocation getCapacityKey() {
    return CAPACITY;
  }

  /** Overridable method to change the fluid key */
  public ResourceLocation getFluidKey() {
    return FLUID;
  }


  /* Helpers */

  /** Checks if the given modifier is the owner of the tank */
  public boolean isOwner(IModDataReadOnly volatileData) {
    ResourceLocation key = getOwnerKey();
    if (key == null) {
      return true;
    }
    return getId().toString().equals(volatileData.getString(key));
  }

  /** Checks if the given modifier is the owner of the tank */
  public boolean isOwner(IModifierToolStack tool) {
    return isOwner(tool.getVolatileData());
  }

  /** Gets the capacity of the tank */
  public int getCapacity(IModDataReadOnly volatileData) {
    return volatileData.getInt(CAPACITY);
  }

  /** Gets the capacity of the tank */
  public int getCapacity(IModifierToolStack tool) {
    return tool.getVolatileData().getInt(CAPACITY);
  }

  /** Adds the given capacity into volatile NBT */
  public void addCapacity(ModDataNBT volatileNBT, int amount) {
    if (volatileNBT.contains(CAPACITY, NBT.TAG_ANY_NUMERIC)) {
      amount += volatileNBT.getInt(CAPACITY);
    }
    volatileNBT.putInt(CAPACITY, amount);
  }

  /** Gets the fluid in the tank */
  public FluidStack getFluid(IModifierToolStack tool) {
    return tool.getPersistentData().get(FLUID, PARSE_FLUID);
  }

  /** Sets the fluid in the tank */
  public FluidStack setFluid(IModifierToolStack tool, FluidStack fluid) {
    int capacity = getCapacity(tool);
    if (fluid.getAmount() > capacity) {
      fluid.setAmount(capacity);
    }
    tool.getPersistentData().put(FLUID, fluid.writeToNBT(new CompoundNBT()));
    return fluid;
  }

  /**
   * Fills the tool with the given resource
   * @param tool       Tool stack
   * @param current    Current tank contents
   * @param resource   Resource to insert
   * @param amount     Amount to insert, overrides resource amount
   * @return  Fluid after filling, or empty if nothing changed
   */
  public FluidStack fill(IModifierToolStack tool, FluidStack current, FluidStack resource, int amount) {
    int capacity = getCapacity(tool);
    if (current.isEmpty()) {
      // cap fluid at capacity, store in tool
      resource.setAmount(Math.min(amount, capacity));
      return setFluid(tool, resource);
    } else if (current.isFluidEqual(resource)) {
      // boost fluid by amount and store
      current.setAmount(Math.min(current.getAmount() + amount, capacity));
      return setFluid(tool, current);
    }
    return FluidStack.EMPTY;
  }

  /**
   * Drains the given amount from the tool
   * @param tool     Tool
   * @param current  Existing fluid
   * @param amount   Amount to drain
   * @return  New fluid
   */
  public FluidStack drain(IModifierToolStack tool, FluidStack current, int amount) {
    if (current.getAmount() < amount) {
      return setFluid(tool, FluidStack.EMPTY);
    } else {
      current.shrink(amount);
      return setFluid(tool, current);
    }
  }

  /** Shared tank implementation of the fluid modifier */
  public class ModifierTank implements IFluidModifier {
    @Override
    public int getTanks(IModDataReadOnly volatileData) {
      return isOwner(volatileData) ? 1 : 0;
    }

    @Override
    public FluidStack getFluidInTank(IModifierToolStack tool, int level, int tank) {
      return isOwner(tool) ? getFluid(tool) : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(IModifierToolStack tool, int level, int tank) {
      return isOwner(tool) ? getCapacity(tool) : 0;
    }

    @Override
    public int fill(IModifierToolStack tool, int level, FluidStack resource, FluidAction action) {
      if (!resource.isEmpty() && isOwner(tool)) {
        // must not be too full
        FluidStack current = getFluid(tool);
        int remaining = getCapacity(tool) - current.getAmount();
        if (remaining <= 0) {
          return 0;
        }
        // must match existing fluid
        if (!current.isEmpty() && !current.isFluidEqual(resource)) {
          return 0;
        }
        // actual filling logic
        int filled = Math.min(remaining, resource.getAmount());
        if (filled > 0 && action.execute()) {
          TankModifier.this.fill(tool, current, resource, filled);
        }
        return filled;
      }
      return 0;
    }

    @Override
    public FluidStack drain(IModifierToolStack tool, int level, FluidStack resource, FluidAction action) {
      if (!resource.isEmpty() && isOwner(tool)) {
        // fluid type mismatches
        FluidStack current = getFluid(tool);
        if (current.isEmpty() || !current.isFluidEqual(resource)) {
          return FluidStack.EMPTY;
        }
        // actual draining
        int drainedAmount = Math.min(current.getAmount(), resource.getAmount());
        FluidStack drained = new FluidStack(current, drainedAmount);
        if (action.execute()) {
          TankModifier.this.drain(tool, current, drainedAmount);
        }
        return drained;
      }
      return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(IModifierToolStack tool, int level, int maxDrain, FluidAction action) {
      if (maxDrain > 0 && isOwner(tool)) {
        // fluid type mismatches
        FluidStack current = getFluid(tool);
        if (current.isEmpty()) {
          return FluidStack.EMPTY;
        }
        // actual draining
        int drainedAmount = Math.min(current.getAmount(), maxDrain);
        FluidStack drained = new FluidStack(current, drainedAmount);
        if (action.execute()) {
          TankModifier.this.drain(tool, current, drainedAmount);
        }
        return drained;
      }
      return FluidStack.EMPTY;
    }
  }
}
