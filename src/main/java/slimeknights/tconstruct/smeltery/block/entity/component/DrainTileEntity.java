package slimeknights.tconstruct.smeltery.block.entity.component;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.model.data.SinglePropertyData;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryInputOutputTileEntity.SmelteryFluidIO;
import slimeknights.tconstruct.smeltery.block.entity.tank.IDisplayFluidListener;
import slimeknights.tconstruct.smeltery.block.entity.tank.ISmelteryTankHandler;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Fluid IO extension to display controller fluid
 */
public class DrainTileEntity extends SmelteryFluidIO implements IDisplayFluidListener {
  @Getter
  private final IModelData modelData = new SinglePropertyData<>(IDisplayFluidListener.PROPERTY);
  @Getter
  private FluidStack displayFluid = FluidStack.EMPTY;

  public DrainTileEntity(BlockPos pos, BlockState state) {
    this(TinkerSmeltery.drain.get(), pos, state);
  }

  protected DrainTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void notifyDisplayFluidUpdated(FluidStack fluid) {
    if (!fluid.isFluidEqual(displayFluid)) {
      // no need to copy as the fluid was copied by the caller
      displayFluid = fluid;
      modelData.setData(IDisplayFluidListener.PROPERTY, displayFluid);
      requestModelDataUpdate();
      assert level != null;
      BlockState state = getBlockState();
      level.sendBlockUpdated(worldPosition, state, state, 48);
    }
  }

  @Override
  public BlockPos getListenerPos() {
    return getBlockPos();
  }


  /* Updating */

  /** Attaches this TE to the master as a display fluid listener */
  private void attachFluidListener() {
    BlockPos masterPos = getMasterPos();
    if (masterPos != null && level != null && level.isClientSide) {
      BlockEntityHelper.get(ISmelteryTankHandler.class, level, masterPos).ifPresent(te -> te.addDisplayListener(this));
    }
  }

  // override instead of writeSynced to avoid writing master to the main tag twice
  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag nbt = super.getUpdateTag();
    writeMaster(nbt);
    return nbt;
  }

  @Override
  public void handleUpdateTag(CompoundTag tag) {
    BlockPos oldMaster = getMasterPos();
    super.handleUpdateTag(tag);
    if (!Objects.equals(oldMaster, getMasterPos())) {
      attachFluidListener();
    }
  }

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  @Nullable
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return Util.createBEPacket(this, be -> be.writeMaster(new CompoundTag()));
  }

  @Override
  public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
    CompoundTag tag = pkt.getTag();
    if (tag != null) {
      BlockPos oldMaster = getMasterPos();
      readMaster(tag);
      if (!Objects.equals(oldMaster, getMasterPos())) {
        attachFluidListener();
      }
    }
  }
}
