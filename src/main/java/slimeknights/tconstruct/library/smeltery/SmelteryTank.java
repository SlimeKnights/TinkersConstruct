package slimeknights.tconstruct.library.smeltery;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.ListIterator;

public class SmelteryTank {

  protected final ISmelteryTankHandler parent;
  protected List<FluidStack> liquids; // currently contained liquids in the smeltery
  protected int maxCapacity;

  public SmelteryTank(ISmelteryTankHandler parent) {
    liquids = Lists.newArrayList();
    maxCapacity = 0;
    this.parent = parent;
  }

  public void setCapacity(int maxCapacity) {
    this.maxCapacity = maxCapacity;
  }

  public List<FluidStack> getFluids() {
    return liquids;
  }

  public void setFluids(List<FluidStack> fluids) {
    this.liquids = fluids;
    parent.onTankChanged(liquids, null);
  }

  public int getMaxCapacity() {
    return maxCapacity;
  }

  public int getUsedCapacity() {
    int cap = 0;
    for(FluidStack liquid : liquids) {
      cap += liquid.amount;
    }

    return cap;
  }

  public int fill(FluidStack resource, boolean doFill) {
    // check how much space is left in the smeltery
    int used = getUsedCapacity();

    int usable = Math.min(maxCapacity - used, resource.amount);

    if(!doFill) {
      return usable;
    }

    // check if we already have the given liquid
    for(FluidStack liquid : liquids) {
      if(liquid.isFluidEqual(resource)) {
        // yup. add it
        liquid.amount += usable;
        parent.onTankChanged(liquids, liquid);
        return usable;
      }
    }

    // not present yet, add it
    resource = resource.copy();
    resource.amount = usable;
    liquids.add(resource);
    parent.onTankChanged(liquids, resource);
    return usable;
  }

  public FluidStack drain(int maxDrain, boolean doDrain) {
    if(liquids.isEmpty()) {
      return null;
    }

    FluidStack liquid = new FluidStack(liquids.get(0), maxDrain);
    return drain(liquid, doDrain);
  }

  public FluidStack drain(FluidStack resource, boolean doDrain) {
    // search for the resource
    ListIterator<FluidStack> iter = liquids.listIterator();
    while(iter.hasNext()) {
      FluidStack liquid = iter.next();
      if(liquid.isFluidEqual(resource)) {
        int drainable = Math.min(resource.amount, liquid.amount);
        if(doDrain) {
          liquid.amount -= drainable;
          if(liquid.amount <= 0) {
            iter.remove();
          }
          parent.onTankChanged(liquids, liquid);
        }

        // return drained amount
        resource = resource.copy();
        resource.amount = drainable;
        return resource;
      }
    }

    // nothing drained
    return null;
  }

  /* Saving and loading */

  public void writeToNBT(NBTTagCompound tag) {
    NBTTagList taglist = new NBTTagList();

    for(FluidStack liquid : liquids) {
      NBTTagCompound fluidTag = new NBTTagCompound();
      liquid.writeToNBT(fluidTag);
      taglist.appendTag(fluidTag);
    }

    tag.setTag("Liquids", taglist);
    tag.setInteger("LiquidCapacity", maxCapacity);
  }

  public void readFromNBT(NBTTagCompound tag) {
    NBTTagList taglist = tag.getTagList("Liquids", 10);

    liquids.clear();
    for(int i = 0; i < taglist.tagCount(); i++) {
      NBTTagCompound fluidTag = taglist.getCompoundTagAt(i);
      FluidStack liquid = FluidStack.loadFluidStackFromNBT(fluidTag);
      if(liquid != null) {
        liquids.add(liquid);
      }
    }

    maxCapacity = tag.getInteger("LiquidCapacity");
  }

  /** Moves the fluid with the passed index to the beginning/bottom of the fluid tank stack */
  public void moveFluidToBottom(int index) {
    if(index < liquids.size()) {
      FluidStack fluid = liquids.get(index);
      liquids.remove(index);
      liquids.add(0, fluid);
    }
  }
}
