package slimeknights.tconstruct.smeltery.tileentity.tank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.tileentity.CastingTileEntity;

import java.util.Objects;

@RequiredArgsConstructor
public class CastingFluidHandler implements IFluidHandler {
  private final CastingTileEntity tile;
  @Getter @Setter
  private FluidStack fluid = FluidStack.EMPTY;
  @Setter
  private int capacity = 0;
  private Fluid filter = Fluids.EMPTY;

  /** Checks if the given fluid is valid */
  public boolean isFluidValid(FluidStack stack) {
    return !stack.isEmpty() && (filter == Fluids.EMPTY || stack.getFluid() == filter);
  }

  /** Checks if the fluid is empty */
  public boolean isEmpty() {
    return fluid.isEmpty();
  }

  /** Gets the current capacity of this fluid handler */
  public int getCapacity() {
    if (capacity == 0) {
      return fluid.getAmount();
    }
    return capacity;
  }

  /** Resets the tanks filter */
  public void reset() {
    capacity = 0;
    fluid = FluidStack.EMPTY;
    filter = Fluids.EMPTY;
    onContentsChanged();
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    if (resource.isEmpty() || !isFluidValid(resource)) {
      return 0;
    }

    // update filter and capacity
    int capacity = this.capacity;
    if (filter == null || this.capacity == 0) {
      Fluid fluid = resource.getFluid();
      capacity = tile.initNewCasting(fluid, action);
      if (capacity <= 0) {
        return 0;
      }
      if (action.execute()) {
        this.capacity = capacity;
        this.filter = fluid;
      }
    }

    // if no fluid yet, copy it in
    if (fluid.isEmpty()) {
      int amount = Math.min(capacity, resource.getAmount());
      if (action.execute()) {
        fluid = new FluidStack(resource, amount);
        onContentsChanged();
      }
      return amount;
    }

    // safety: should never be false, but good to check
    if (!resource.isFluidEqual(fluid)) {
      return 0;
    }

    // if full, nothing to do
    int space = capacity - fluid.getAmount();
    if (space <= 0) {
      return 0;
    }
    // if it fits, it grows
    int amount = resource.getAmount();
    if (amount < space) {
      if (action.execute()) {
        fluid.grow(amount);
        onContentsChanged();
      }
      return amount;
    } else {
      // too much? set to max
      if (action.execute()) {
        fluid.setAmount(capacity);
        onContentsChanged();
      }
      return space;
    }
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
      return FluidStack.EMPTY;
    }
    return this.drain(resource.getAmount(), action);
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    int drained = Math.min(fluid.getAmount(), maxDrain);
    if (drained <= 0) {
      return FluidStack.EMPTY;
    }

    FluidStack stack = new FluidStack(fluid, drained);
    if (action.execute()) {
      fluid.shrink(drained);
      if (fluid.isEmpty()) {
        // since empty, assume the current recipe is invalid now
        // fixes some odd behavior with capacity and recipes going out of sync
        tile.reset();
      } else {
        // called in reset
        onContentsChanged();
      }
    }
    return stack;
  }

  /* Required */

  @Override
  public FluidStack getFluidInTank(int tank) {
    if (tank == 0) {
      return fluid;
    }
    return FluidStack.EMPTY;
  }

  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public int getTankCapacity(int tank) {
    return getCapacity();
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return tank == 0 && isFluidValid(stack);
  }

  /* NBT */
  private static final String TAG_FLUID = "fluid";
  private static final String TAG_FILTER = "filter";
  private static final String TAG_CAPACITY = "capacity";

  /** Reads the tank from NBT */
  public void readFromNBT(CompoundNBT nbt) {
    capacity = nbt.getInt(TAG_CAPACITY);
    if (nbt.contains(TAG_FLUID, NBT.TAG_COMPOUND)) {
      setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound(TAG_FLUID)));
    }
    if (nbt.contains(TAG_FILTER, NBT.TAG_STRING)) {
      Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(nbt.getString(TAG_FILTER)));
      if (fluid != null) {
        filter = fluid;
      }
    }
  }

  /** Write the tank from NBT */
  public CompoundNBT writeToNBT(CompoundNBT nbt) {
    nbt.putInt(TAG_CAPACITY, capacity);
    if (!fluid.isEmpty()) {
      nbt.put(TAG_FLUID, fluid.writeToNBT(new CompoundNBT()));
    }
    if (filter != Fluids.EMPTY) {
      nbt.putString(TAG_FILTER, Objects.requireNonNull(filter.getRegistryName()).toString());
    }
    return nbt;
  }

  protected void onContentsChanged() {
    tile.markDirty();
    World world = tile.getWorld();
    if (world != null && !world.isRemote) {
      TinkerNetwork.getInstance().sendToClientsAround(new FluidUpdatePacket(tile.getPos(), this.getFluid()), world, tile.getPos());
    }
  }
}
