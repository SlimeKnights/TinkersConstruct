package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.network.FaucetActivationPacket;

import javax.annotation.Nullable;

import static slimeknights.tconstruct.smeltery.block.FaucetBlock.FACING;

public class FaucetTileEntity extends TileEntity implements ITickableTileEntity {
  private static final String TAG_DRAINED = "drained";
  private static final String TAG_STOP = "stop";
  private static final String TAG_POURING = "pouring";
  private static final String TAG_LAST_REDSTONE = "lastRedstone";

  @Getter
  private boolean isPouring = false;
  private boolean stopPouring = false;
  /** Last fluid the client was sent, used to reduce number of packets we need to send */
  private Fluid clientFluid = Fluids.EMPTY;
  @Getter
  private FluidStack drained = FluidStack.EMPTY;
  private boolean lastRedstoneState;

  public FaucetTileEntity() {
    this(TinkerSmeltery.faucet.get());
  }

  @SuppressWarnings("WeakerAccess")
  protected FaucetTileEntity(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }


  /* Activation */

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
      stopPouring = false;
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


  /* Pouring */

  @Override
  public void tick() {
    if (world == null || world.isRemote) {
      return;
    }
    // nothing to do if not pouring
    if (!isPouring) {
      return;
    }
    // if done, try draining another
    if (drained.isEmpty()) {
      // unless told to stop
      if (stopPouring) {
        reset();
      }
      else {
        doTransfer();
      }
    }
    else {
      // continue current stack
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

          // sync to clients if we have changes
          if (!isPouring || clientFluid != drained.getFluid()) {
            isPouring = true;
            syncToClient(this.drained, true);
          }

          // pour after initial packet, in case we end up resetting later
          pour();
          return;
        }
      }

      // if powered, keep faucet running
      if (lastRedstoneState) {
        // sync if either we were not pouring before (particle effects), or if the client thinks we have fluid
        if (!isPouring || clientFluid != Fluids.EMPTY) {
          isPouring = true;
          syncToClient(FluidStack.EMPTY, true);
        }
        return;
      }
    }
    // reset if not powered, or if nothing to do
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
        // update client if they do not think we have fluid
        if (clientFluid != drained.getFluid()) {
          syncToClient(drained, true);
        }

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
    stopPouring = false;
    drained = FluidStack.EMPTY;
    if (isPouring || clientFluid != Fluids.EMPTY) {
      isPouring = false;
      syncToClient(FluidStack.EMPTY, false);
    }
  }

  /**
   * Gets IFluidHandler of TE
   * @param pos       TE position
   * @param direction Side of TE to check for fluid handler
   * @return  IFluidHandler of TE or null.
   */
  @SuppressWarnings("ConstantConditions")
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


  /* NBT and networking */

  /**
   * Sends an update to the client with the most recent
   * @param fluid       New fluid
   * @param isPouring   New isPouring status
   */
  private void syncToClient(FluidStack fluid, boolean isPouring) {
    clientFluid = fluid.getFluid();
    if (world instanceof ServerWorld) {
      TinkerNetwork.getInstance().sendToClientsAround(new FaucetActivationPacket(pos, fluid, isPouring), (ServerWorld) world, getPos());
    }
  }

  /**
   * Sets draining fluid to specified stack.
   * @param fluid new FluidStack
   */
  public void onActivationPacket(FluidStack fluid, boolean isPouring) {
    this.isPouring = isPouring;
    this.drained = fluid;
  }

  @Override
  public CompoundNBT getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return write(new CompoundNBT());
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    compound = super.write(compound);
    compound.putBoolean(TAG_POURING, isPouring);
    compound.putBoolean(TAG_STOP, stopPouring);
    compound.putBoolean(TAG_LAST_REDSTONE, lastRedstoneState);
    if (!drained.isEmpty()) {
      compound.put(TAG_DRAINED, drained.writeToNBT(new CompoundNBT()));
    }
    return compound;
  }

  @Override
  public void read(BlockState state, CompoundNBT compound) {
    super.read(state, compound);
    isPouring = compound.getBoolean(TAG_POURING);
    stopPouring = compound.getBoolean(TAG_STOP);
    lastRedstoneState = compound.getBoolean(TAG_LAST_REDSTONE);
    if (compound.contains(TAG_DRAINED, NBT.TAG_COMPOUND)) {
      drained = FluidStack.loadFluidStackFromNBT(compound.getCompound(TAG_DRAINED));
      clientFluid = drained.getFluid();
    } else {
      drained = FluidStack.EMPTY;
      clientFluid = Fluids.EMPTY;
    }
  }
}
