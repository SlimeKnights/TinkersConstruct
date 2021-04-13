package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.NotNullConsumer;
import slimeknights.mantle.util.WeakConsumerWrapper;
import slimeknights.tconstruct.library.EmptyItemHandler;
import slimeknights.tconstruct.misc.IItemHandler;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SmelteryIOBlock;
import slimeknights.tconstruct.smeltery.tileentity.module.IFluidHandler;
import slimeknights.tconstruct.smeltery.tileentity.tank.ISmelteryTankHandler;

import java.util.Objects;
import java.util.Optional;

/**
 * Shared logic between drains and ducts
 */
public abstract class SmelteryInputOutputTileEntity<T> extends SmelteryComponentTileEntity {
  /** Capability this TE watches */
//  private final Capability<T> capability;
  /** Empty capability for in case the valid capability becomes invalid without invalidating */
  protected final T emptyInstance;
  /** Listener to attach to consumed capabilities */
  protected final NotNullConsumer<Optional<T>> listener = new WeakConsumerWrapper<>(this, (te, cap) -> te.clearHandler());
  @Nullable
  private Optional<T> capabilityHolder = null;

  protected SmelteryInputOutputTileEntity(BlockEntityType<?> type, T emptyInstance) {
    super(type);
//    this.capability = capability;
    this.emptyInstance = emptyInstance;
  }

/*  *//** Clears all cached capabilities *//*
  private void clearHandler() {
    if (capabilityHolder != null) {
      capabilityHolder.invalidate();
      capabilityHolder = null;
    }
  }

  @Override
  protected void invalidateCaps() {
    super.invalidateCaps();
    clearHandler();
  }*/

  @Override
  protected void setMaster(@Nullable BlockPos master, @Nullable Block block) {
    assert world != null;

    // keep track of master before it changed
    boolean masterChanged = !Objects.equals(getMasterPos(), master);
    // update the master
    boolean hadMaster = getMasterPos() != null;
    super.setMaster(master, block);
    // update the active state
    boolean hasMaster = getMasterPos() != null;
    if (hadMaster != hasMaster) {
      world.setBlockState(pos, getCachedState().with(SmelteryIOBlock.ACTIVE, hasMaster));
    }
    // if we have a new master, invalidate handlers
    if (masterChanged) {
      clearHandler();
      world.updateNeighborsAlways(pos, getCachedState().getBlock());
    }
  }

  /**
   * Gets the capability to store in this IO block. Capability parent should have the proper listeners attached
   * @param parent  Parent tile entity
   * @return  Capability from parent, or empty if absent
   */
  protected Optional<T> getCapability(BlockEntity parent) {
//    Optional<T> handler = parent.getCapability(capability);
//    if (handler.isPresent()) {
//      handler.addListener(listener);
//
//      return Optional.of(() -> handler.orElse(emptyInstance));
//    }
    return Optional.empty();
  }

  /**
   * Fetches the capability handlers if missing
   */
  private Optional<T> getCachedCapability() {
    if (!capabilityHolder.isPresent()) {
      if (validateMaster()) {
        BlockPos master = getMasterPos();
        if (master != null && this.world != null) {
          BlockEntity te = world.getBlockEntity(master);
          if (te != null) {
            capabilityHolder = getCapability(te);
            return capabilityHolder;
          }
        }
      }
      capabilityHolder = Optional.empty();
    }
    return capabilityHolder;
  }

//  @Override
//  public <C> Optional<C> getCapability(Capability<C> capability, @Nullable Direction facing) {
//    if (capability == this.capability) {
//      return getCachedCapability().cast();
//    }
//    return super.getCapability(capability, facing);
//  }

  /** Fluid implementation of smeltery IO */
  public static abstract class SmelteryFluidIO extends SmelteryInputOutputTileEntity<IFluidHandler> {
    protected SmelteryFluidIO(BlockEntityType<?> type) {
      super(type, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EmptyFluidHandler.INSTANCE);
    }

    /** Wraps the given capability */
    protected Optional<IFluidHandler> makeWrapper(Optional<IFluidHandler> capability) {
      return Optional.of(() -> capability.orElse(emptyInstance));
    }

    @Override
    protected Optional<IFluidHandler> getCapability(BlockEntity parent) {
      // fluid capability is not exposed directly in the smeltery
      if (parent instanceof ISmelteryTankHandler) {
        Optional<IFluidHandler> capability = Optional.ofNullable(((ISmelteryTankHandler) parent).getFluidCapability());
        if (capability.isPresent()) {
          capability.addListener(listener);
          return makeWrapper(capability);
        }
      }
      return Optional.empty();
    }
  }

  /** Item implementation of smeltery IO */
  public static class ChuteTileEntity extends SmelteryInputOutputTileEntity<IItemHandler> {
    public ChuteTileEntity() {
      this(TinkerSmeltery.chute);
    }

    protected ChuteTileEntity(BlockEntityType<?> type) {
      super(type, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EmptyItemHandler.INSTANCE);
    }
  }

}
