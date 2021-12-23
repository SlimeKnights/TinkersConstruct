package slimeknights.tconstruct.smeltery.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockSearedFurnaceController;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;
import slimeknights.tconstruct.smeltery.network.HeatingStructureFuelUpdatePacket;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class TileHeatingStructureFuelTank<T extends MultiblockDetection> extends TileHeatingStructure<T> {

  // NBT Tags
  public static final String TAG_TANKS = "tanks";
  public static final String TAG_FUEL_QUALITY = "fuelQuality";
  public static final String TAG_CURRENT_FUEL = "currentFuel";
  public static final String TAG_CURRENT_TANK = "currentTank";

  // amount of fuel gotten from a single consumption of the fluid, used for GUI fuel percentage
  public int fuelQuality;

  // Fuel tank information
  public List<BlockPos> tanks;
  public BlockPos currentTank;
  public FluidStack currentFuel; // the fuel that was last consumed

  public TileHeatingStructureFuelTank(String name, int inventorySize, int maxStackSize) {
    super(name, inventorySize, maxStackSize);

    tanks = Lists.newLinkedList();
  }

  @Override
  protected void consumeFuel() {
    // no need to consume fuel
    if(hasFuel()) {
      return;
    }

    // get current tank
    searchForFuel();

    // got a tank?
    if(currentTank != null) {
      // consume fuel!
      TileEntity te = getWorld().getTileEntity(currentTank);
      if(te instanceof TileTank) {
        IFluidTank tank = ((TileTank) te).getInternalTank();

        FluidStack liquid = tank.getFluid();
        if(liquid != null) {
          FluidStack in = liquid.copy();
          int bonusFuel = TinkerRegistry.consumeSmelteryFuel(in);
          int amount = liquid.amount - in.amount;
          FluidStack drained = tank.drain(amount, false);

          // we can drain. actually drain and add the fuel
          if(drained != null && drained.amount == amount) {
            tank.drain(amount, true);
            currentFuel = drained.copy();
            fuelQuality = bonusFuel;
            addFuel(bonusFuel, drained.getFluid().getTemperature(drained) - 300); // convert to degree celcius

            // notify client of fuel/temperature changes
            if(isServerWorld()) {
              TinkerNetwork.sendToAll(new HeatingStructureFuelUpdatePacket(pos, currentTank, temperature, currentFuel));
            }

            return;
          }
        }

        fuelQuality = 0;
      }
    }
  }

  /**
   * Locates a tank containing fuel, if one exists
   *
   * @return true if successful
   */
  private void searchForFuel() {
    // is the current tank still up to date?
    if(currentTank != null && hasTankWithFuel(currentTank, currentFuel)) {
      return;
    }

    // nope, current tank is empty, check others for same fuel
    for(BlockPos pos : tanks) {
      if(hasTankWithFuel(pos, currentFuel)) {
        currentTank = pos;
        return;
      }
    }

    // nothing found, try again with new fuel
    for(BlockPos pos : tanks) {
      if(hasTankWithFuel(pos, null)) {
        currentTank = pos;
        return;
      }
    }

    currentTank = null;
  }

  // checks if the given location has a fluid tank that contains fuel
  private boolean hasTankWithFuel(BlockPos pos, FluidStack preference) {
    IFluidTank tank = getTankAt(pos);
    if(tank != null && tank.getFluid() != null) {
      if(tank.getFluidAmount() > 0 && TinkerRegistry.isSmelteryFuel(tank.getFluid())) {
        // if we have a preference, only use that
        if(preference != null && tank.getFluid().isFluidEqual(preference)) {
          return true;
        }
        else if(preference == null) {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  protected void updateStructureInfo(MultiblockDetection.MultiblockStructure structure) {
    // find all tanks for input
    tanks.clear();
    for(BlockPos pos : structure.blocks) {
      if(getWorld().getBlockState(pos).getBlock() == TinkerSmeltery.searedTank) {
        tanks.add(pos);
      }
    }

    int inventorySize = getUpdatedInventorySize(structure.xd, structure.yd, structure.zd);

    // if the new multiblock is smaller we pop out all items that don't fit in anymore
    if(!world.isRemote && this.getSizeInventory() > inventorySize) {
      for(int i = inventorySize; i < getSizeInventory(); i++) {
        if(!getStackInSlot(i).isEmpty()) {
          dropItem(getStackInSlot(i));
        }
      }
    }

    // adjust inventory sizes
    this.resize(inventorySize);
  }

  /** When the multiblock forms the inventory size is readjusted. Return the inventory size from the (total) structure size */
  protected abstract int getUpdatedInventorySize(int width, int height, int depth);

  protected void dropItem(ItemStack stack) {
    EnumFacing direction = getWorld().getBlockState(pos).getValue(BlockSearedFurnaceController.FACING);
    BlockPos pos = this.getPos().offset(direction);

    EntityItem entityitem = new EntityItem(getWorld(), pos.getX(), pos.getY(), pos.getZ(), stack);
    getWorld().spawnEntity(entityitem);
  }

  /**
   * Grabs the tank at the given location (if present)
   */
  private IFluidTank getTankAt(BlockPos pos) {
    TileEntity te = getWorld().getTileEntity(pos);
    if(te instanceof TileTank) {
      return ((TileTank) te).getInternalTank();
    }

    return null;
  }

  /* GUI */
  public float getHeatingProgress(int index) {
    if(index < 0 || index > getSizeInventory() - 1) {
      return -1f;
    }

    if(!canHeat(index)) {
      return -1f;
    }

    return getProgress(index);
  }

  /**
   * Can be used by the GUI to determine fuel percentage
   */
  @SideOnly(Side.CLIENT)
  public float getFuelPercentage() {
    return (float) fuel / (float) fuelQuality;
  }

  @SideOnly(Side.CLIENT)
  public FuelInfo getFuelDisplay() {
    FuelInfo info = new FuelInfo();

    // we still have leftover fuel
    if(hasFuel()) {
      // if the current fuel is null, something in the fluid registry changed
      // just replace it with lava and ignore for now, it will fix next time we consume fuel
      if(currentFuel == null) {
        info.fluid = new FluidStack(FluidRegistry.LAVA, 0);
        info.maxCap = 1;
      } else {
        info.fluid = currentFuel.copy();
        info.fluid.amount = 0;
        info.maxCap = currentFuel.amount;
      }
      info.heat = this.temperature + 300;
    }
    else if(currentTank != null) {
      // we need to consume fuel, check the current tank
      if(hasTankWithFuel(currentTank, currentFuel)) {
        IFluidTank tank = getTankAt(currentTank);
        assert tank != null;
        FluidStack tankFluid = tank.getFluid();
        assert tankFluid != null;
        info.fluid = tankFluid.copy();
        info.heat = temperature + 300;
        info.maxCap = tank.getCapacity();
      }
    }

    // check all other tanks (except the current one that we already checked) for more fuel
    for(BlockPos pos : tanks) {
      if(pos == currentTank) {
        continue;
      }

      IFluidTank tank = getTankAt(pos);
      // tank exists and has something in it
      if(tank != null && tank.getFluidAmount() > 0) {
        assert tank.getFluid() != null;
        // we don't have fuel yet, use this
        if(info.fluid == null) {
          info.fluid = tank.getFluid().copy();
          info.heat = info.fluid.getFluid().getTemperature(info.fluid);
          info.maxCap = tank.getCapacity();
        }
        // otherwise add the same together
        else if(tank.getFluid().isFluidEqual(info.fluid)) {
          info.fluid.amount += tank.getFluidAmount();
          info.maxCap += tank.getCapacity();
        }
      }
    }

    return info;
  }

  /* Networking and saving */
  @SideOnly(Side.CLIENT)
  public void updateFuelFromPacket(int index, int fuel) {
    if(index == 0) {
      this.fuel = fuel;
    }
    else if(index == 1) {
      this.fuelQuality = fuel;
    }
  }

  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    compound = super.writeToNBT(compound);

    compound.setInteger(TAG_FUEL_QUALITY, fuelQuality);

    compound.setTag(TAG_CURRENT_TANK, TagUtil.writePos(currentTank));
    NBTTagList tankList = new NBTTagList();
    for(BlockPos pos : tanks) {
      tankList.appendTag(TagUtil.writePos(pos));
    }
    compound.setTag(TAG_TANKS, tankList);

    NBTTagCompound fuelTag = new NBTTagCompound();
    if(currentFuel != null) {
      currentFuel.writeToNBT(fuelTag);
    }
    compound.setTag(TAG_CURRENT_FUEL, fuelTag);

    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);

    fuelQuality = compound.getInteger(TAG_FUEL_QUALITY);

    NBTTagList tankList = compound.getTagList(TAG_TANKS, 10);
    tanks.clear();
    for(int i = 0; i < tankList.tagCount(); i++) {
      tanks.add(TagUtil.readPos(tankList.getCompoundTagAt(i)));
    }

    NBTTagCompound fuelTag = compound.getCompoundTag(TAG_CURRENT_FUEL);
    currentFuel = FluidStack.loadFluidStackFromNBT(fuelTag);
  }

  public static class FuelInfo {

    public int heat;
    public int maxCap;
    public FluidStack fluid;
  }

}
