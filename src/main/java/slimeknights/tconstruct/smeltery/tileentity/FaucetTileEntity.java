package slimeknights.tconstruct.smeltery.tileentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.Optional;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.mantle.util.NotNullConsumer;
import slimeknights.mantle.util.WeakConsumerWrapper;
import slimeknights.tconstruct.fluids.EmptyFluidHandler;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.network.FaucetActivationPacket;
import slimeknights.tconstruct.fluids.IFluidHandler;

import static slimeknights.tconstruct.smeltery.block.FaucetBlock.FACING;

public class FaucetTileEntity extends BlockEntity implements Tickable {
  /** Transfer rate of the faucet */
  public static final int MB_PER_TICK = 12;
  /** amount of MB to extract from the input at a time */
  public static final int PACKET_SIZE = 144;

  private static final String TAG_DRAINED = "drained";
  private static final String TAG_RENDER_FLUID = "render_fluid";
  private static final String TAG_STOP = "stop";
  private static final String TAG_STATE = "state";
  private static final String TAG_LAST_REDSTONE = "lastRedstone";

  /** If true, faucet is currently pouring */
  private FaucetState faucetState = FaucetState.OFF;
  /** If true, redstone told this faucet to stop, so stop when ready */
  private boolean stopPouring = false;
  /** Current fluid in the faucet */
  private FluidVolume drained = TinkerFluids.EMPTY;
  /** Fluid for rendering, used to reduce the number of packets. There is a brief moment where {@link this#drained} is empty but we should be rendering something */
  private FluidVolume renderFluid = TinkerFluids.EMPTY;
  /** Used for pulse detection */
  private boolean lastRedstoneState = false;

  /** Fluid handler of the input to the faucet */
  private Optional<IFluidHandler> inputHandler;
  /** Fluid handler of the output from the faucet */
  private Optional<IFluidHandler> outputHandler;
  /** Listener for when the input handler is invalidated */
  private final NotNullConsumer<Optional<IFluidHandler>> inputListener = new WeakConsumerWrapper<>(this, (self, handler) -> self.inputHandler = null);
  /** Listener for when the output handler is invalidated */
  private final NotNullConsumer<Optional<IFluidHandler>> outputListener = new WeakConsumerWrapper<>(this, (self, handler) -> self.outputHandler = null);

  public FaucetTileEntity() {
    this(TinkerSmeltery.faucet);
  }

  @SuppressWarnings("WeakerAccess")
  protected FaucetTileEntity(BlockEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }


  /* Fluid handler */

  /**
   * Finds the fluid handler on the given side
   * @param side  Side to check
   * @return  Fluid handler
   */
  private Optional<IFluidHandler> findFluidHandler(Direction side) {
    assert world != null;
    BlockEntity te = world.getBlockEntity(pos.offset(side));
    if (te != null) {
      throw new RuntimeException("CRAB!"); // FIXME: PORT
//      Optional<IFluidHandler> handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
//      if (handler.isPresent()) {
//        return handler;
//      }
    }
    return Optional.empty();
  }

  /**
   * Gets the input fluid handler
   * @return  Input fluid handler
   */
  private Optional<IFluidHandler> getInputHandler() {
    if (inputHandler == null) {
      inputHandler = findFluidHandler(getCachedState().get(FACING).getOpposite());
      if (inputHandler.isPresent()) {
        throw new RuntimeException("CRAB!"); // FIXME: PORT
//        inputHandler.get().addListener(inputListener);
      }
    }
    return inputHandler;
  }

  /**
   * Gets the output fluid handler
   * @return  Output fluid handler
   */
  private Optional<IFluidHandler> getOutputHandler() {
    if (outputHandler == null) {
      outputHandler = findFluidHandler(Direction.DOWN);
      if (outputHandler.isPresent()) {
        throw new RuntimeException("CRAB!"); // FIXME: PORT
//        outputHandler.get().addListener(outputListener);
      }
    }
    return outputHandler;
  }

  /**
   * Called when a neighbor changes to invalidate the cached fluid handler
   * @param neighbor  Neighbor position that changed
   */
  public void neighborChanged(BlockPos neighbor) {
    // if the neighbor was below us, remove output
    if (pos.equals(neighbor.up())) {
      outputHandler = null;
      // neighbor behind us
    } else if (pos.equals(neighbor.offset(getCachedState().get(FACING)))) {
      inputHandler = null;
    }
  }


  /* Data */

  /**
   * Gets whether the faucet is pouring
   * @return True if pouring
   */
  public boolean isPouring() {
    return faucetState != FaucetState.OFF;
  }

  /**
   * Gets the fluid currently being drained, mainly used for rendering
   * @return  Fluid being drained
   */
  public FluidVolume getRenderFluid() {
    return renderFluid;
  }

  /* Activation */

  /**
   * Toggles pouring state and initiates transfer if appropriate. Called on right click and from redstone
   */
  public void activate() {
    // don't run on client
    if (world == null || world.isClient) {
      return;
    }
    // already pouring? we want to start
    switch (faucetState) {
      // off activates the faucet
      case OFF:
        stopPouring = false;
        doTransfer(true);
        break;
        // powered deactivates the faucet, sync to client
      case POWERED:
        faucetState = FaucetState.OFF;
        syncToClient(TinkerFluids.EMPTY, false);
        break;
        // pouring means we stop pouring as soon as possible
      case POURING:
        stopPouring = true;
        break;
    }
  }

  /**
   * Flips hasSignal and schedules a tick if appropriate.
   * @param hasSignal  New signal state
   */
  public void handleRedstone(boolean hasSignal) {
    if (hasSignal != lastRedstoneState) {
      lastRedstoneState = hasSignal;
      if (hasSignal) {
        if (world != null){
          world.getBlockTickScheduler().schedule(pos, this.getCachedState().getBlock(), 2);
        }
      } else if (faucetState == FaucetState.POWERED) {
        faucetState = FaucetState.OFF;
        syncToClient(TinkerFluids.EMPTY, false);
      }
    }
  }


  /* Pouring */

  @Override
  public void tick() {
    if (world == null || world.isClient) {
      return;
    }

    // nothing to do if not pouring
    if (faucetState == FaucetState.OFF) {
      return;
      // if powered and we can transfer, schedule transfer for next tick
    } else if (faucetState == FaucetState.POWERED && doTransfer(false)) {
      faucetState = FaucetState.POURING;
      return;
    }

    // continue current stack
    if (!drained.isEmpty()) {
      pour();
      // stop if told to stop once done
    } else if (stopPouring) {
      reset();
      // otherwise keep going
    } else {
      doTransfer(true);
    }
  }

  /**
   * Initiate fluid transfer
   */
//  private boolean doTransfer(boolean execute) {
//    // still got content left
//    Optional<IFluidHandler> inputOptional = getInputHandler();
//    Optional<IFluidHandler> outputOptional = getOutputHandler();
//    if (inputOptional.isPresent() && outputOptional.isPresent()) {
//      // can we drain?
//      IFluidHandler input = inputOptional.orElse(EmptyFluidHandler.INSTANCE);
//      FluidVolume drained = input.drain(PACKET_SIZE, Simulation.SIMULATE);
//      if (!drained.isEmpty() && !drained.getFluidKey().getAttributes().isGaseous(drained)) {
//        // can we fill
//        IFluidHandler output = outputOptional.orElse(EmptyFluidHandler.INSTANCE);
//        int filled = output.fill(drained, Simulation.SIMULATE);
//        if (filled > 0) {
//          // fill if requested
//          if (execute) {
//            // drain the liquid and transfer it, buffer the amount for delay
//            this.drained = input.drain(filled, Simulation.ACTION);
//
//            // sync to clients if we have changes
//            if (faucetState == FaucetState.OFF || !renderFluid.equals(drained)) {
//              syncToClient(this.drained, true);
//            }
//            faucetState = FaucetState.POURING;
//            // pour after initial packet, in case we end up resetting later
//            pour();
//          }
//          return true;
//        }
//      }
//
//      // if powered, keep faucet running
//      if (lastRedstoneState) {
//        // sync if either we were not pouring before (particle effects), or if the client thinks we have fluid
//        if (execute && (faucetState == FaucetState.OFF || !renderFluid.equals(TinkerFluids.EMPTY))) {
//          syncToClient(TinkerFluids.EMPTY, true);
//        }
//        faucetState = FaucetState.POWERED;
//        return false;
//      }
//    }
//    // reset if not powered, or if nothing to do
//    if (execute) {
//      reset();
//    }
//    return false;
//  }

  private boolean doTransfer(boolean execute) {
    throw new RuntimeException("CRAB!"); // FIXME: PORT
  }

  /**
   * Takes the liquid inside and executes one pouring step.
   */
  private void pour() {
    if (drained.isEmpty()) {
      return;
    }

    // ensure we have an output
    Optional<IFluidHandler> outputOptional = getOutputHandler();
    if (outputOptional.isPresent()) {
      FluidVolume fillStack = drained.copy();
      fillStack.withAmount(FluidAmount.of(Math.min(drained.getAmount_F().asInt(1000), MB_PER_TICK), 1000));

      // can we fill?
      IFluidHandler output = outputOptional.orElse(EmptyFluidHandler.INSTANCE);
      int filled = output.fill(fillStack, Simulation.SIMULATE).getAmount_F().asInt(1000);
      if (filled > 0) {
        // update client if they do not think we have fluid
        if (!renderFluid.equals(drained)) {
          syncToClient(drained, true);
        }

        // transfer it
        this.drained = this.drained.withAmount(this.drained.amount().min(FluidAmount.of(filled, 1000)));
        fillStack = fillStack.withAmount(FluidAmount.of(filled, 1000));
        output.fill(fillStack, Simulation.ACTION);
      }
    }
    else {
      // output got lost. all liquid buffered is lost.
      reset();
    }
  }

  /**
   * Resets TE to default state.
   */
  private void reset() {
    stopPouring = false;
    drained = TinkerFluids.EMPTY;
    if (faucetState != FaucetState.OFF || !renderFluid.equals(drained)) {
      faucetState = FaucetState.OFF;
      syncToClient(TinkerFluids.EMPTY, false);
    }
  }

//  @Override
  @Environment(EnvType.CLIENT)
  public Box getRenderBoundingBox() {
    return new Box(pos.getX(), pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
  }


  /* NBT and networking */

  /**
   * Sends an update to the client with the most recent
   * @param fluid       New fluid
   * @param isPouring   New isPouring status
   */
  private void syncToClient(FluidVolume fluid, boolean isPouring) {
    renderFluid = fluid.copy();
    if (world instanceof ServerWorld) {
      TinkerNetwork.getInstance().sendToClientsAround(new FaucetActivationPacket(pos, fluid, isPouring), (ServerWorld) world, getPos());
    }
  }

  /**
   * Sets draining fluid to specified stack.
   * @param fluid new FluidVolume
   */
  public void onActivationPacket(FluidVolume fluid, boolean isPouring) {
    // pouring and powered are interchangable on the client
    this.faucetState = isPouring ? FaucetState.POURING : FaucetState.OFF;
    this.renderFluid = fluid;
  }

  @Override
  public CompoundTag toInitialChunkDataTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return toTag(new CompoundTag());
  }

  @Override
  public CompoundTag toTag(CompoundTag compound) {
    compound = super.toTag(compound);
    compound.putByte(TAG_STATE, (byte)faucetState.ordinal());
    compound.putBoolean(TAG_STOP, stopPouring);
    compound.putBoolean(TAG_LAST_REDSTONE, lastRedstoneState);
    if (!drained.isEmpty()) {
      compound.put(TAG_DRAINED, drained.toTag(new CompoundTag()));
    }
    if (!renderFluid.isEmpty()) {
      compound.put(TAG_RENDER_FLUID, renderFluid.toTag(new CompoundTag()));
    }
    return compound;
  }

  @Override
  public void fromTag(BlockState state, CompoundTag compound) {
    super.fromTag(state, compound);

    faucetState = FaucetState.fromIndex(compound.getByte(TAG_STATE));
    stopPouring = compound.getBoolean(TAG_STOP);
    lastRedstoneState = compound.getBoolean(TAG_LAST_REDSTONE);
    // fluids
    if (compound.contains(TAG_DRAINED, NbtType.COMPOUND)) {
      drained = FluidVolume.fromTag(compound.getCompound(TAG_DRAINED));
    } else {
      drained = TinkerFluids.EMPTY;
    }
    if (compound.contains(TAG_RENDER_FLUID, NbtType.COMPOUND)) {
      renderFluid = FluidVolume.fromTag(compound.getCompound(TAG_RENDER_FLUID));
    } else {
      renderFluid = TinkerFluids.EMPTY;
    }
  }

  private enum FaucetState {
    OFF,
    POURING,
    POWERED;

    /** Gets the state for the given index */
    public static FaucetState fromIndex(int index) {
      switch (index) {
        case 1: return POURING;
        case 2: return POWERED;
      }
      return OFF;
    }
  }
}
