package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.model.IModelData;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.library.client.model.SinglePropertyData;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryInputOutputTileEntity.SmelteryFluidIO;
import slimeknights.tconstruct.smeltery.tileentity.tank.IDisplayFluidListener;
import slimeknights.tconstruct.smeltery.tileentity.tank.ISmelteryTankHandler;

/**
 * Fluid IO extension to display controller fluid
 */
public class DrainTileEntity extends SmelteryFluidIO implements IDisplayFluidListener {
  @Getter
  private final IModelData modelData = new SinglePropertyData<>(IDisplayFluidListener.PROPERTY);
  @Getter
  private Fluid displayFluid = Fluids.EMPTY;

  public DrainTileEntity() {
    super(TinkerSmeltery.drain);
  }

  protected DrainTileEntity(BlockEntityType<?> type) {
    super(type);
  }

  @Override
  public void notifyDisplayFluidUpdated(Fluid fluid) {
    if (fluid != displayFluid) {
      displayFluid = fluid;
      modelData.setData(IDisplayFluidListener.PROPERTY, fluid);
//      requestModelDataUpdate();
      assert world != null;
      BlockState state = getCachedState();
      world.updateListeners(pos, state, state, 48);
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
    if (masterPos != null && world != null && world.isClient) {
      TileEntityHelper.getTile(ISmelteryTankHandler.class, world, masterPos).ifPresent(te -> te.addDisplayListener(this));
    }
  }

  @Override
  public CompoundTag toInitialChunkDataTag() {
    CompoundTag nbt = super.toInitialChunkDataTag();
    writeMaster(nbt);
    return nbt;
  }

  @Override
  @Nullable
  public BlockEntityUpdateS2CPacket toUpdatePacket() {
    return new BlockEntityUpdateS2CPacket(pos, 0, writeMaster(new CompoundTag()));
  }

//  @Override
  public void onDataPacket(ClientConnection net, BlockEntityUpdateS2CPacket pkt) {
    readMaster(pkt.getCompoundTag());
    attachFluidListener();
  }
}
