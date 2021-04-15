package slimeknights.tconstruct.smeltery.tileentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import slimeknights.mantle.model.IModelData;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.client.model.SinglePropertyData;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;

public class TankTileEntity extends SmelteryComponentTileEntity implements ITankTileEntity {
  /** Max capacity for the tank */
  public static final FluidAmount DEFAULT_CAPACITY = FluidAmount.BUCKET.mul(4);

  /**
   * Gets the capacity for the given block
   * @param block  block
   * @return  Capacity
   */
  public static FluidAmount getCapacity(Block block) {
    if (block instanceof SearedTankBlock) {
      return ((SearedTankBlock) block).getCapacity();
    }
    return DEFAULT_CAPACITY;
  }

  /**
   * Gets the capacity for the given item
   * @param item  item
   * @return  Capacity
   */
  public static FluidAmount getCapacity(Item item) {
    if (item instanceof BlockItem) {
      return getCapacity(((BlockItem)item).getBlock());
    }
    return DEFAULT_CAPACITY;
  }

  /** Internal fluid tank instance */
  protected final FluidTankAnimated tank;
  /** Last comparator strength to reduce block updates */
  private int lastStrength = -1;

  public TankTileEntity() {
    this(TinkerSmeltery.searedTank.get(TankType.TANK));
  }

  /** Main constructor */
  public TankTileEntity(SearedTankBlock block) {
    this(TinkerSmeltery.tank, block);
  }

  /** Extendable constructor */
  @SuppressWarnings("WeakerAccess")
  protected TankTileEntity(BlockEntityType<?> type, SearedTankBlock block) {
    super(type);
    tank = new FluidTankAnimated(block.getCapacity(), this);
  }


  /*
   * Tank methods
   */
/*
  @Override
  @NotNull
  public <T> Optional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return holder.cast();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  protected void invalidateCaps() {
    super.invalidateCaps();
    holder.invalidate();
  }

  @Override
  public IModelData getModelData() {
    return modelData;
  }*/

  @Override
  public void onTankContentsChanged() {
    ITankTileEntity.super.onTankContentsChanged();
    if (this.world != null) {
      world.getLightingProvider().checkBlock(this.pos);
    }
  }

  @Override
  public void updateFluidTo(FluidVolume fluid) {
    ITankTileEntity.super.updateFluidTo(fluid);
    // update light if the fluid changes
    if (this.world != null) {
      world.getLightingProvider().checkBlock(this.pos);
    }
  }


  /*
   * NBT
   */

  @Override
  public void fromTag(BlockState state, CompoundTag tag) {
    updateTank(tag.getCompound(Tags.TANK));
    super.fromTag(state, tag);
  }

  /**
   * Updates the tank from an NBT tag, used in the block
   * @param nbt  tank NBT
   */
  public void updateTank(CompoundTag nbt) {
    if (nbt.isEmpty()) {
      tank.setFluid(TinkerFluids.EMPTY);
    } else {
      tank.readFromNBT(nbt);
    }
  }

  @Override
  public void writeSynced(CompoundTag tag) {
    // want tank on the client on world load
    if (!tank.isEmpty()) {
      tag.put(Tags.TANK, tank.writeToNBT(new CompoundTag()));
    }
  }

  public FluidTankAnimated getTank() {
    return this.tank;
  }

  public int getLastStrength() {
    return this.lastStrength;
  }

  public void setLastStrength(int lastStrength) {
    this.lastStrength = lastStrength;
  }
}
