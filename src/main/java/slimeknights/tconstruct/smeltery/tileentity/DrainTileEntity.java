package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.data.IModelData;
import slimeknights.mantle.client.model.data.SinglePropertyData;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryInputOutputTileEntity.SmelteryFluidIO;
import slimeknights.tconstruct.smeltery.tileentity.tank.IDisplayFluidListener;
import slimeknights.tconstruct.smeltery.tileentity.tank.ISmelteryTankHandler;

import javax.annotation.Nullable;

/**
 * Fluid IO extension to display controller fluid
 */
public class DrainTileEntity extends SmelteryFluidIO implements IDisplayFluidListener {
  @Getter
  private final IModelData modelData = new SinglePropertyData<>(IDisplayFluidListener.PROPERTY);
  @Getter
  private Fluid displayFluid = Fluids.EMPTY;

  public DrainTileEntity() {
    super(TinkerSmeltery.drain.get());
  }

  protected DrainTileEntity(TileEntityType<?> type) {
    super(type);
  }

  @Override
  public void notifyDisplayFluidUpdated(Fluid fluid) {
    if (fluid != displayFluid) {
      displayFluid = fluid;
      modelData.setData(IDisplayFluidListener.PROPERTY, fluid);
      requestModelDataUpdate();
      assert world != null;
      BlockState state = getBlockState();
      world.notifyBlockUpdate(pos, state, state, 48);
    }
  }

  @Override
  public BlockPos getListenerPos() {
    return getPos();
  }


  /* Updating */

  /** Attaches this TE to the master as a display fluid listener */
  private void attachFluidListener() {
    BlockPos masterPos = getMasterPos();
    if (masterPos != null && world != null && world.isRemote) {
      TileEntityHelper.getTile(ISmelteryTankHandler.class, world, masterPos).ifPresent(te -> te.addDisplayListener(this));
    }
  }

  @Override
  public CompoundNBT getUpdateTag() {
    CompoundNBT nbt = super.getUpdateTag();
    writeMaster(nbt);
    return nbt;
  }

  @Override
  public void handleUpdateTag(BlockState state, CompoundNBT tag) {
    super.handleUpdateTag(state, tag);
    attachFluidListener();
  }

  @Override
  @Nullable
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(pos, 0, writeMaster(new CompoundNBT()));
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    readMaster(pkt.getNbtCompound());
    attachFluidListener();
  }
}
