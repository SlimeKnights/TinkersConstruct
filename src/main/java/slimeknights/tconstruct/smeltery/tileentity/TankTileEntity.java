package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.mantle.client.model.data.SinglePropertyData;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.smeltery.item.TankItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TankTileEntity extends SmelteryComponentTileEntity implements ITankTileEntity {
  /** Max capacity for the tank */
  public static final int DEFAULT_CAPACITY = FluidAttributes.BUCKET_VOLUME * 4;

  /**
   * Gets the capacity for the given block
   * @param block  block
   * @return  Capacity
   */
  public static int getCapacity(Block block) {
    if (block instanceof ITankBlock) {
      return ((ITankBlock) block).getCapacity();
    }
    return DEFAULT_CAPACITY;
  }

  /**
   * Gets the capacity for the given item
   * @param item  item
   * @return  Capacity
   */
  public static int getCapacity(Item item) {
    if (item instanceof BlockItem) {
      return getCapacity(((BlockItem)item).getBlock());
    }
    return DEFAULT_CAPACITY;
  }

  /** Internal fluid tank instance */
  @Getter
  protected final FluidTankAnimated tank;
  /** Capability holder for the tank */
  private final LazyOptional<IFluidHandler> holder;
  /** Tank data for the model */
  private final IModelData modelData;
  /** Last comparator strength to reduce block updates */
  @Getter @Setter
  private int lastStrength = -1;

  public TankTileEntity() {
    this(TinkerSmeltery.searedTank.get(TankType.FUEL_TANK));
  }

  /** Main constructor */
  public TankTileEntity(ITankBlock block) {
    this(TinkerSmeltery.tank.get(), block);
  }

  /** Extendable constructor */
  @SuppressWarnings("WeakerAccess")
  protected TankTileEntity(TileEntityType<?> type, ITankBlock block) {
    super(type);
    tank = new FluidTankAnimated(block.getCapacity(), this);
    holder = LazyOptional.of(() -> tank);
    modelData = new SinglePropertyData<>(ModelProperties.FLUID_TANK, tank);
  }


  /*
   * Tank methods
   */

  @Override
  @Nonnull
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
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
  }

  @Override
  public void onTankContentsChanged() {
    ITankTileEntity.super.onTankContentsChanged();
    if (this.world != null) {
      world.getLightManager().checkBlock(this.pos);
    }
  }

  @Override
  public void updateFluidTo(FluidStack fluid) {
    ITankTileEntity.super.updateFluidTo(fluid);
    // update light if the fluid changes
    if (this.world != null) {
      world.getLightManager().checkBlock(this.pos);
    }
  }


  /*
   * NBT
   */

  /**
   * Sets the tag on the stack based on the contained tank
   * @param stack  Stack
   */
  public void setTankTag(ItemStack stack) {
    TankItem.setTank(stack, tank);
  }

  /**
   * Updates the tank from an NBT tag, used in the block
   * @param nbt  tank NBT
   */
  public void updateTank(CompoundNBT nbt) {
    if (nbt.isEmpty()) {
      tank.setFluid(FluidStack.EMPTY);
    } else {
      tank.readFromNBT(nbt);
      if (world != null) {
        world.getLightManager().checkBlock(pos);
      }
    }
  }

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  public void read(BlockState state, CompoundNBT tag) {
    tank.setCapacity(getCapacity(state.getBlock()));
    updateTank(tag.getCompound(NBTTags.TANK));
    super.read(state, tag);
  }

  @Override
  public void writeSynced(CompoundNBT tag) {
    super.writeSynced(tag);
    // want tank on the client on world load
    if (!tank.isEmpty()) {
      tag.put(NBTTags.TANK, tank.writeToNBT(new CompoundNBT()));
    }
  }

  /** Interface for blocks to return their capacity */
  public interface ITankBlock {
    /** Gets the capacity for this tank */
    int getCapacity();
  }
}
