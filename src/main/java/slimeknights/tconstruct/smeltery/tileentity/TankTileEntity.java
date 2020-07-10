package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.DistExecutor;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.client.model.TankModel;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.fluid.IFluidTankUpdater;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.tables.client.model.ModelProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TankTileEntity extends SmelteryComponentTileEntity implements IFluidTankUpdater, FluidUpdatePacket.IFluidPacketReceiver {

  public static final int CAPACITY = FluidAttributes.BUCKET_VOLUME * 4;
  private final ModelDataMap modelData;

  protected FluidTankAnimated tank;

  private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);
  private int lastStrength;

  public TankTileEntity() {
    this(TinkerSmeltery.tank.get());
    this.tank = new FluidTankAnimated(CAPACITY, this);
    this.lastStrength = -1;
    this.modelData.setData(ModelProperties.FLUID_TANK, tank);
  }

  public TankTileEntity(TileEntityType<?> tileEntityTypein) {
    super(tileEntityTypein);
    modelData = new ModelDataMap.Builder().withProperty(ModelProperties.FLUID_TANK).build();
  }

  @Override
  @Nonnull
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
      return holder.cast();
    return super.getCapability(capability, facing);
  }

  @Override
  public void read(CompoundNBT tag) {
    CompoundNBT fluid = tag.getCompound(Tags.TANK);
    tank.readFromNBT(fluid);
    super.read(tag);
  }

  public void readTank(CompoundNBT nbt) {
    tank.readFromNBT(nbt);
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT tag) {
    CompoundNBT fluid = new CompoundNBT();
    tank.writeToNBT(fluid);
    tag.put(Tags.TANK, fluid);
    return super.write(tag);
  }

  public void writeTank(CompoundNBT nbt) {
    tank.writeToNBT(nbt);
  }

  /**
   * @return The current comparator strength based on the tank's capicity
   */
  public int comparatorStrength() {
    return 15 * tank.getFluidAmount() / tank.getCapacity();
  }

  @Override
  public void onTankContentsChanged() {
    int newStrength = this.comparatorStrength();
    if (newStrength != lastStrength) {
      this.getWorld().notifyNeighborsOfStateChange(this.pos, this.getBlockState().getBlock());
      this.lastStrength = newStrength;
    }
    this.getWorld().getLightManager().checkBlock(this.pos);
  }

  public FluidTankAnimated getInternalTank() {
    return tank;
  }

  @Override
  public void updateFluidTo(FluidStack fluid) {
    int oldAmount = tank.getFluidAmount();
    int newAmount = fluid.getAmount();
    tank.setFluid(fluid);
    tank.setRenderOffset(tank.getRenderOffset() + newAmount - oldAmount);
    this.getWorld().getLightManager().checkBlock(this.pos);

    // update the block model
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> () -> {
      if (Config.CLIENT.tankFluidModel.get()) {
        // if the amount change is bigger than a single increment, or we changed whether we have a fluid, update the world renderer
        TankModel.BakedModel model = RenderUtil.getBakedModel(this.getBlockState(), TankModel.BakedModel.class);
        if (model != null && (Math.abs(newAmount - oldAmount) >= (tank.getCapacity() / model.getFluid().getIncrements()) || (oldAmount == 0) != (newAmount == 0))) {
          //this.requestModelDataUpdate();
          Minecraft.getInstance().worldRenderer.notifyBlockUpdate(null, pos, null, null, 3);
        }
      }
    });
  }

  @Override
  @Nonnull
  public IModelData getModelData() {
    return this.modelData;
  }
}
