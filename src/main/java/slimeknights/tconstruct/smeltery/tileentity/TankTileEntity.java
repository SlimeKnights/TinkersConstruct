package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
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
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TankTileEntity extends SmelteryComponentTileEntity implements ITankTileEntity {
  /** Max capacity for the tank */
  public static final int CAPACITY = FluidAttributes.BUCKET_VOLUME * 4;

  /** Internal fluid tank instance */
  @Getter
  protected final FluidTankAnimated tank = new FluidTankAnimated(CAPACITY, this);
  /** Capability holder for the tank */
  private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);
  /** Tank data for the model */
  private final IModelData modelData = new SinglePropertyData<>(ModelProperties.FLUID_TANK, tank);
  /** Last comparator strength to reduce block updates */
  @Getter @Setter
  private int lastStrength = -1;

  /** Main constructor */
  public TankTileEntity() {
    this(TinkerSmeltery.tank.get());
  }

  /** Extendable constructor */
  @SuppressWarnings("WeakerAccess")
  protected TankTileEntity(TileEntityType<?> tileEntityTypein) {
    super(tileEntityTypein);
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

  @Override
  public void read(BlockState state, CompoundNBT tag) {
    updateTank(tag.getCompound(Tags.TANK));
    super.read(state, tag);
  }

  /**
   * Updates the tank from an NBT tag, used in the block
   * @param nbt  tank NBT
   */
  public void updateTank(CompoundNBT nbt) {
    if (!nbt.isEmpty()) {
      tank.readFromNBT(nbt);
    }
  }

  @Override
  public void writeSynced(CompoundNBT tag) {
    // want tank on the client on world load
    tag.put(Tags.TANK, tank.writeToNBT(new CompoundNBT()));
  }
}
