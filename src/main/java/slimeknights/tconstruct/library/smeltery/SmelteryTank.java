package slimeknights.tconstruct.library.smeltery;

public class SmelteryTank {/*implements IFluidTank, IFluidHandler {

  protected final ISmelteryTankHandler parent;
  protected List<FluidStack> liquids; // currently, contained liquids in the smeltery
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

  @Override
  public FluidStack getFluid() {
    return liquids.size() > 0 ? liquids.get(0) : FluidStack.EMPTY;
  }

  @Override
  public int getFluidAmount() {
    int cap = 0;
    for (FluidStack liquid : liquids) {
      cap += liquid.getAmount();
    }

    return cap;
  }

  @Override
  public int getCapacity() {
    return maxCapacity;
  }

  @Override
  public boolean isFluidValid(FluidStack stack) {
    return false;
  }

  @Override
  public IFluidTankProperties[] getTankProperties() {
    // if the size is 0 (no fluids) simply return an empty properties
    // some other mods expect having at least 1 value here
    if (liquids.size() == 0) {
      return new IFluidTankProperties[]{new FluidTankProperties(null, maxCapacity, true, true)};
    }

    IFluidTankProperties[] properties = new IFluidTankProperties[liquids.size()];
    for (int i = 0; i < liquids.size(); i++) {
      boolean first = i == 0;
      int capacity = liquids.get(i).amount;
      if (first) {
        capacity += getCapacity() - getFluidAmount();
      }
      properties[i] = new FluidTankProperties(liquids.get(i), capacity, first, first);
    }

    return properties;
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    // Safety check, it sometimes seems it can happen that something creates an invalid fluidstack?
    // does some mod register a fluid with an empty string as name..?
    if (StringUtils.isNullOrEmpty(resource.getFluid().getRegistryName().toString())) {
      return 0;
    }

    // check how much space is left in the smeltery
    int used = getFluidAmount();

    int usable = Math.min(maxCapacity - used, resource.getAmount());
    // could be negative if the smeltery size changes then you try filling it
    if (usable <= 0) {
      return 0;
    }
    if (action.simulate()) {
      return usable;
    }

    // check if we already have the given liquid
    for (FluidStack liquid : liquids) {
      if (liquid.isFluidEqual(resource)) {
        // yup. add it
        liquid.grow(usable);
        parent.onTankChanged(liquids, liquid);
        return usable;
      }
    }

    // not present yet, add it
    resource = resource.copy();
    resource.setAmount(usable);
    liquids.add(resource);
    parent.onTankChanged(liquids, resource);
    return usable;
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    if (liquids.isEmpty()) {
      return FluidStack.EMPTY;
    }

    FluidStack liquid = new FluidStack(liquids.get(0), maxDrain);
    return drain(liquid, action);
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    // search for the resource
    ListIterator<FluidStack> iter = liquids.listIterator();

    while (iter.hasNext()) {
      FluidStack liquid = iter.next();

      if (liquid.isFluidEqual(resource)) {
        int drainable = Math.min(resource.getAmount(), liquid.getAmount());

        if (action.execute()) {
          liquid.shrink(drainable);

          if (liquid.getAmount() <= 0) {
            iter.remove();
          }

          parent.onTankChanged(liquids, liquid);
        }

        // return drained amount
        resource = resource.copy();
        resource.setAmount(drainable);
        return resource;
      }
    }

    // nothing drained
    return FluidStack.EMPTY;
  }

  /* Saving and loading *

  public CompoundNBT writeToNBT(CompoundNBT nbt) {
    ListNBT taglist = new ListNBT();

    for (FluidStack liquid : liquids) {
      if (liquid.getFluid().getRegistryName() == null) {
        TinkerSmeltery.log.error("Error trying to save fluids inside smeltery! Invalid Liquid found! Smeltery contents:");
        for (FluidStack liquid2 : liquids) {
          TinkerSmeltery.log.error("  " + liquid2.getTranslationKey() + "/" + liquid2.getAmount() + "mb");
        }
        continue;
      }

      CompoundNBT fluidTag = new CompoundNBT();
      liquid.writeToNBT(fluidTag);
      taglist.add(fluidTag);
    }

    nbt.put("Liquids", taglist);
    nbt.putInt("LiquidCapacity", maxCapacity);

    return nbt;
  }

  public void readFromNBT(CompoundNBT tag) {
    ListNBT taglist = tag.getList("Liquids", 10);

    liquids.clear();
    for (int i = 0; i < taglist.size(); i++) {
      CompoundNBT fluidTag = taglist.getCompound(i);

      FluidStack liquid = FluidStack.loadFluidStackFromNBT(fluidTag);

      if (liquid != null) {
        liquids.add(liquid);
      }
    }

    maxCapacity = tag.getInt("LiquidCapacity");
  }

  /**
   * Moves the fluid with the passed index to the beginning/bottom of the fluid tank stack
   *
  public void moveFluidToBottom(int index) {
    if (index < liquids.size()) {
      FluidStack fluid = liquids.get(index);
      liquids.remove(index);
      liquids.add(0, fluid);
    }
  }*/
}
