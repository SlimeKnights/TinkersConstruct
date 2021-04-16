package slimeknights.tconstruct.smeltery.tileentity.tank;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.tileentity.CastingTileEntity;
import slimeknights.tconstruct.fluids.IFluidHandler;

import java.util.Objects;

@RequiredArgsConstructor
public class CastingFluidHandler implements IFluidHandler {
  private final CastingTileEntity tile;
  @Getter @Setter
  private FluidVolume fluid = TinkerFluids.EMPTY;
  @Setter
  private int capacity = 0;
  private Fluid filter = Fluids.EMPTY;

  /** Checks if the given fluid is valid */
  public boolean isFluidValid(FluidVolume stack) {
    return !stack.isEmpty() && (filter == Fluids.EMPTY || stack.getRawFluid() == filter);
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
    fluid = TinkerFluids.EMPTY;
    filter = Fluids.EMPTY;
    onContentsChanged();
  }

  @Override
  public FluidVolume fill(FluidVolume resource, Simulation action) {
    if (resource.isEmpty() || tile.isStackInSlot(CastingTileEntity.OUTPUT) || !isFluidValid(resource)) {
      return resource.withAmount(FluidAmount.ZERO);
    }

    // update filter and capacity
    int capacity = this.capacity;
    if (filter == null || this.capacity == 0) {
      Fluid fluid = resource.getRawFluid();
      capacity = tile.initNewCasting(fluid, action);
      if (capacity <= 0) {
        return resource.withAmount(FluidAmount.ZERO);
      }
      if (action.isAction()) {
        this.capacity = capacity;
        this.filter = fluid;
      }
    }

    // if no fluid yet, copy it in
    if (fluid.isEmpty()) {
      int amount = Math.min(capacity, resource.getAmount_F().asInt(1000));
      if (action.isAction()) {
        fluid = FluidVolume.create(resource.getRawFluid(), amount);
        onContentsChanged();
      }
      return resource.withAmount(FluidAmount.of(amount, 1000));
    }

    // safety: should never be false, but good to check
    if (!resource.equals(fluid)) {
      return resource.withAmount(FluidAmount.ZERO);
    }

    // if full, nothing to do
    int space = capacity - fluid.getAmount_F().asInt(1000);
    if (space <= 0) {
      return resource.withAmount(FluidAmount.ZERO);
    }
    // if it fits, it grows
    int amount = resource.getAmount_F().asInt(1000);
    if (amount < space) {
      if (action.isAction()) {
        fluid = fluid.withAmount(fluid.amount().add(amount));
        onContentsChanged();
      }
      return resource.withAmount(FluidAmount.of(amount, 1000));
    } else {
      // too much? set to max
      if (action.isAction()) {
        fluid = fluid.withAmount(FluidAmount.of(capacity, 1000));
        onContentsChanged();
      }
      return resource.withAmount(FluidAmount.of(space, 1000));
    }
  }

  @Override
  public FluidVolume drain(FluidVolume resource, Simulation action) {
    if (resource.isEmpty() || !resource.equals(fluid)) {
      return TinkerFluids.EMPTY;
    }
    return this.drain(resource, action);
  }

  @Override
  public FluidVolume drain(FluidAmount maxDrain, Simulation action) {
    int drained = Math.min(fluid.getAmount_F().asInt(1000), maxDrain.asInt(1000));
    if (drained <= 0) {
      return TinkerFluids.EMPTY;
    }

    FluidVolume stack = fluid.withAmount(FluidAmount.ofWhole(drained));
    if (action.isAction()) {
      fluid = fluid.withAmount(FluidAmount.of(fluid.getAmount_F().asInt(1000) + drained, 1000));
      onContentsChanged();
    }
    return stack;
  }

  /* Required */

  @Override
  public FluidVolume getFluidInTank(int tank) {
    if (tank == 0) {
      return fluid;
    }
    return TinkerFluids.EMPTY;
  }

  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public FluidAmount getTankCapacity(int tank) {
    return FluidAmount.of(getCapacity(), 1000);
  }

  @Override
  public boolean isFluidValid(int tank, FluidVolume stack) {
    return tank == 0 && isFluidValid(stack);
  }

  /* NBT */
  private static final String TAG_FLUID = "fluid";
  private static final String TAG_FILTER = "filter";
  private static final String TAG_CAPACITY = "capacity";

  /** Reads the tank from NBT */
  public void readFromNBT(CompoundTag nbt) {
    capacity = nbt.getInt(TAG_CAPACITY);
    if (nbt.contains(TAG_FLUID, NbtType.COMPOUND)) {
      setFluid(FluidVolume.fromTag(nbt.getCompound(TAG_FLUID)));
    }
    if (nbt.contains(TAG_FILTER, NbtType.STRING)) {
      Fluid fluid = Registry.FLUID.get(new Identifier(nbt.getString(TAG_FILTER)));
      if (fluid != null) {
        filter = fluid;
      }
    }
  }

  /** Write the tank from NBT */
  public CompoundTag writeToNBT(CompoundTag nbt) {
    nbt.putInt(TAG_CAPACITY, capacity);
    if (!fluid.isEmpty()) {
      nbt.put(TAG_FLUID, fluid.toTag(new CompoundTag()));
    }
    if (filter != Fluids.EMPTY) {
      nbt.putString(TAG_FILTER, Objects.requireNonNull(Registry.FLUID.getId(filter)).toString());
    }
    return nbt;
  }

  protected void onContentsChanged() {
    tile.markDirty();
    World world = tile.getWorld();
    if (world != null && !world.isClient) {
      TinkerNetwork.getInstance().sendToClientsAround(new FluidUpdatePacket(tile.getPos(), this.getFluid()), world, tile.getPos());
    }
  }
}
