package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.network.FaucetActivationPacket;

import javax.annotation.Nullable;

import static slimeknights.tconstruct.smeltery.block.FaucetBlock.FACING;

public class FaucetTileEntity extends TileEntity implements ITickableTileEntity {
  @Getter
  private boolean isPouring = false;
  private boolean stopPouring = false;
  @Getter
  private FluidStack drained = FluidStack.EMPTY;
  private boolean lastRedstoneState;

  public FaucetTileEntity() {
    this(TinkerSmeltery.faucet.get());
  }

  protected FaucetTileEntity(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  /**
   * Toggles pouring state and initiates transfer if appropriate. Called on right click and from redstone
   */
  public void activate() {
    // don't run on client
    if (world == null || world.isRemote) {
      return;
    }
    // already pouring? we want to stop then
    if (isPouring) {
      stopPouring = true;
    } else {
      doTransfer();
    }
  }

  /**
   * Flips hasSignal and schedules a tick if appropriate.
   * @param hasSignal  New signal state
   */
  public void handleRedstone(boolean hasSignal) {
    if (hasSignal != lastRedstoneState ) {
      lastRedstoneState = hasSignal;
      if (hasSignal && world != null) {
        world.getPendingBlockTicks().scheduleTick(pos, this.getBlockState().getBlock(), 2);
      }
    }
  }

  @Override
  public void tick() {
    if (world == null || world.isRemote) {
      return;
    }

    // nothing to do if not pouring
    if (!isPouring) {
      return;
    }

    // done draining
    if (drained.isEmpty()) {
      // pour me another, if we want to
      if (!stopPouring) {
        doTransfer();
      }
      else {
        reset();
      }
    }
    else {
      // reduce amount (cooldown)
      pour();
    }
  }

  /**
   * Initiate fluid transfer
   */
  private void doTransfer() {
    // still got content left
    Direction direction = this.getBlockState().get(FACING);
    IFluidHandler toDrain = getFluidHandler(pos.offset(direction), direction.getOpposite());
    IFluidHandler toFill = getFluidHandler(pos.down(), Direction.UP);
    if (toDrain != null && toFill != null) {
      // can we drain?
      FluidStack drained = toDrain.drain(144, IFluidHandler.FluidAction.SIMULATE);
      if (!drained.isEmpty()) {
        // can we fill
        int filled = toFill.fill(drained, IFluidHandler.FluidAction.SIMULATE);
        if (filled > 0) {
          // drain the liquid and transfer it, buffer the amount for delay
          this.drained = toDrain.drain(filled, IFluidHandler.FluidAction.EXECUTE);

          // sync to clients
          if (world instanceof ServerWorld) {
            TinkerNetwork.getInstance().sendToClientsAround(new FaucetActivationPacket(pos, drained), (ServerWorld) world, getPos());
          }

          // should never be a problem, but since pour can send a reset pack, pour after we send the start packet
          this.isPouring = true;
          pour();
          return;
        }
      }
    }
    // draining unsuccessful
    reset();
  }

  /**
   * Takes the liquid inside and executes one pouring step.
   */
  private void pour() {
    if (drained.isEmpty()) {
      return;
    }

    IFluidHandler toFill = getFluidHandler(pos.down(), Direction.UP);
    if (toFill != null) {
      FluidStack fillStack = drained.copy();
      fillStack.setAmount(Math.min(drained.getAmount(), 6));

      // can we fill?
      int filled = toFill.fill(fillStack, IFluidHandler.FluidAction.SIMULATE);
      if (filled > 0) {
        // transfer it
        this.drained.shrink(filled);
        fillStack.setAmount(filled);
        toFill.fill(fillStack, IFluidHandler.FluidAction.EXECUTE);
      }
    }
    else {
      // filling TE got lost. all liquid buffered is lost.
      reset();
    }

  }

  /**
   * Resets TE to default state.
   */
  private void reset() {
    isPouring = false;
    stopPouring = false;
    drained = FluidStack.EMPTY;
    if (world instanceof ServerWorld) {
      TinkerNetwork.getInstance().sendToClientsAround(new FaucetActivationPacket(pos, drained), (ServerWorld) world, getPos());
    }
  }

  /**
   * Gets IFluidHandler of TE
   * @param pos       TE position
   * @param direction Side of TE to check for fluid handler
   * @return  IFluidHandler of TE or null.
   */
  @Nullable
  private IFluidHandler getFluidHandler(BlockPos pos, Direction direction) {
    if (world == null) {
      return null;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te != null) {
      return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction).orElse(null);
    }
    return null;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public AxisAlignedBB getRenderBoundingBox() {
    return new AxisAlignedBB(pos.getX(), pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    compound = super.write(compound);
    if (!drained.isEmpty()) {
      drained.writeToNBT(compound);
      compound.putBoolean("stop", stopPouring);
    }
    return compound;
  }

  @Override
  public void read(CompoundNBT compound) {
    super.read(compound);
    drained = FluidStack.loadFluidStackFromNBT(compound);
    if (!drained.isEmpty()) {
      isPouring = true;
      stopPouring = compound.getBoolean("stop");
    }
    else {
      reset();
    }
  }

  /**
   * Sets draining fluid to specified stack.
   *
   * @param fluid new FluidStack
   */
  public void onActivationPacket(FluidStack fluid) {
    // empty stack received, reset te state.
    if (fluid.isEmpty()) {
      reset();
    }
    // non-empty stack received, begin pouring it.
    else {
      drained = fluid;
      isPouring = true;
    }
  }

  @Nullable
  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    CompoundNBT compound = new CompoundNBT();
    write(compound);
    return new SUpdateTileEntityPacket(this.getPos(), -1, compound);
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    super.onDataPacket(net, pkt);
    read(pkt.getNbtCompound());
  }

  @Override
  public CompoundNBT getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return write(new CompoundNBT());
  }

  @Override
  public void handleUpdateTag(CompoundNBT tag) {
    read(tag);
  }
}
