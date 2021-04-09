package slimeknights.tconstruct.smeltery.tileentity.tank;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.network.SmelteryTankUpdatePacket;
import slimeknights.tconstruct.smeltery.tileentity.tank.ISmelteryTankHandler.FluidChange;

import java.util.List;
import java.util.ListIterator;

/**
 * Fluid handler implementation for the smeltery
 */
public class SmelteryTank implements IFluidHandler {
  private final ISmelteryTankHandler parent;
  /** Fluids actually contained in the tank */
  @Getter
  private final List<FluidVolume> fluids;
  /** Maximum capacity of the smeltery */
  private int capacity;
  /** Current amount of fluid in the tank */
  @Getter
  private int contained;

  public SmelteryTank(ISmelteryTankHandler parent) {
    fluids = Lists.newArrayList();
    capacity = 0;
    contained = 0;
    this.parent = parent;
  }

  /**
   * Called when the fluids change to sync to client
   */
  public void syncFluids() {
    BlockEntity te = parent.getTileEntity();
    World world = te.getWorld();
    if (world != null && !world.isClient) {
      BlockPos pos = te.getPos();
      TinkerNetwork.getInstance().sendToClientsAround(new SmelteryTankUpdatePacket(pos, fluids), world, pos);
    }
  }


  /* Capacity and space */

  /**
   * Updates the maximum tank capacity
   * @param maxCapacity  New max capacity
   */
  public void setCapacity(int maxCapacity) {
    this.capacity = maxCapacity;
  }

  /**
   * Gets the maximum amount of space in the smeltery tank
   * @return  Tank capacity
   */
  public int getCapacity() {
    return capacity;
  }

  /**
   * Gets the amount of empty space in the tank
   * @return  Remaining space in the tank
   */
  public int getRemainingSpace() {
    if (contained >= capacity) {
      return 0;
    }
    return capacity - contained;
  }


  /* Fluids */

  @Override
  public boolean isFluidValid(int tank, FluidVolume stack) {
    return true;
  }

  @Override
  public int getTanks() {
    if (contained < capacity) {
      return fluids.size() + 1;
    }
    return fluids.size();
  }

  @Override
  public FluidVolume getFluidInTank(int tank) {
    if (tank < 0 || tank >= fluids.size()) {
      return TinkerFluids.EMPTY;
    }
    return fluids.get(tank);
  }

  @Override
  public int getTankCapacity(int tank) {
    if (tank < 0) {
      return 0;
    }
    // index of the tank size means the "empty" segment
    int remaining = capacity - contained;
    if (tank == fluids.size()) {
      return remaining;
    }
    // any valid index, return the amount contained and the extra space
    return fluids.get(tank).getAmount() + remaining;
  }

  /**
   * Moves the fluid with the passed index to the beginning/bottom of the fluid tank stack
   * @param index  Index to move
   */
  public void moveFluidToBottom(int index) {
    if (index < fluids.size()) {
      FluidVolume fluid = fluids.get(index);
      fluids.remove(index);
      fluids.add(0, fluid);
      parent.notifyFluidsChanged(FluidChange.CHANGED, Fluids.EMPTY);
    }
  }


  /* Filling and draining */

  @Override
  public int fill(FluidVolume resource, FluidAction action) {
    // if full or nothing being filled, do nothing
    if (contained >= capacity || resource.isEmpty()) {
      return 0;
    }

    // determine how much we can fill
    int usable = Math.min(capacity - contained, resource.getAmount());
    // could be negative if the smeltery size changes then you try filling it
    if (usable <= 0) {
      return 0;
    }

    // done here if just simulating
    if (action.simulate()) {
      return usable;
    }

    // add contained fluid amount
    contained += usable;

    // check if we already have the given liquid
    for (FluidVolume fluid : fluids) {
      if (fluid.isFluidEqual(resource)) {
        // yup. add it
        fluid.grow(usable);
        parent.notifyFluidsChanged(FluidChange.CHANGED, fluid.getFluid());
        return usable;
      }
    }

    // not present yet, add it
    resource = resource.copy();
    resource.setAmount(usable);
    fluids.add(resource);
    parent.notifyFluidsChanged(FluidChange.ADDED, resource.getFluid());
    return usable;
  }

  @Override
  public FluidVolume drain(int maxDrain, FluidAction action) {
    if (fluids.isEmpty()) {
      return TinkerFluids.EMPTY;
    }

    // simply drain the first one
    FluidVolume fluid = fluids.get(0);
    int drainable = Math.min(maxDrain, fluid.getAmount());

    // copy contained fluid to return for accuracy
    FluidVolume ret = fluid.copy();
    ret.setAmount(drainable);

    // remove the fluid from the tank
    if (action.execute()) {
      fluid.shrink(drainable);
      contained -= drainable;
      // if now empty, remove from the list
      if (fluid.getAmount() <= 0) {
        fluids.remove(fluid);
        parent.notifyFluidsChanged(FluidChange.REMOVED, fluid.getFluid());
      } else {
        parent.notifyFluidsChanged(FluidChange.CHANGED, fluid.getFluid());
      }
    }

    // return drained fluid
    return ret;
  }

  @Override
  public FluidVolume drain(FluidVolume toDrain, FluidAction action) {
    // search for the resource
    ListIterator<FluidVolume> iter = fluids.listIterator();
    while (iter.hasNext()) {
      FluidVolume fluid = iter.next();
      if (fluid.isFluidEqual(toDrain)) {
        // if found, determine how much we can drain
        int drainable = Math.min(toDrain.getAmount(), fluid.getAmount());

        // copy contained fluid to return for accuracy
        FluidVolume ret = fluid.copy();
        ret.setAmount(drainable);

        // update tank if executing
        if (action.execute()) {
          fluid.shrink(drainable);
          contained -= drainable;
          // if now empty, remove from the list
          if (fluid.getAmount() <= 0) {
            iter.remove();
            parent.notifyFluidsChanged(FluidChange.REMOVED, fluid.getFluid());
          } else {
            parent.notifyFluidsChanged(FluidChange.CHANGED, fluid.getFluid());
          }
        }

        return ret;
      }
    }

    // nothing drained
    return TinkerFluids.EMPTY;
  }

  /* Saving and loading */

  private static final String TAG_FLUIDS = "fluids";
  private static final String TAG_CAPACITY = "capacity";

  /**
   * Updates fluids in the tank, typically from a packet
   * @param fluids  List of fluids
   */
  public void setFluids(List<FluidVolume> fluids) {
    Fluid oldFirst = getFluidInTank(0).getFluid();
    this.fluids.clear();
    this.fluids.addAll(fluids);
    contained = fluids.stream().mapToInt(FluidVolume::getAmount).reduce(0, Integer::sum);
    Fluid newFirst = getFluidInTank(0).getFluid();
    if (oldFirst != newFirst) {
      parent.notifyFluidsChanged(FluidChange.ORDER_CHANGED, newFirst);
    }
  }

  /** Writes the tank to NBT */
  public CompoundTag write(CompoundTag nbt) {
    ListTag list = new ListTag();
    for (FluidVolume liquid : fluids) {
      CompoundTag fluidTag = new CompoundTag();
      liquid.writeToNBT(fluidTag);
      list.add(fluidTag);
    }
    nbt.put(TAG_FLUIDS, list);
    nbt.putInt(TAG_CAPACITY, capacity);
    return nbt;
  }

  /** Reads the tank from NBT */
  public void read(CompoundTag tag) {
    ListTag list = tag.getList(TAG_FLUIDS, NBT.TAG_COMPOUND);
    fluids.clear();
    contained = 0;
    for (int i = 0; i < list.size(); i++) {
      CompoundTag fluidTag = list.getCompound(i);
      FluidVolume fluid = FluidVolume.loadFluidVolumeFromNBT(fluidTag);
      if (!fluid.isEmpty()) {
        fluids.add(fluid);
        contained += fluid.getAmount();
      }
    }
    capacity = tag.getInt(TAG_CAPACITY);
  }
}
