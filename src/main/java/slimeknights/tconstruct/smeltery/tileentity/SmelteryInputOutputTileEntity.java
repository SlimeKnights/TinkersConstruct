package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.fluids.EmptyFluidHandler;
import slimeknights.tconstruct.library.EmptyItemHandler;
import slimeknights.tconstruct.misc.IItemHandler;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SmelteryIOBlock;
import slimeknights.tconstruct.fluids.IFluidHandler;

import java.util.Objects;
import java.util.Optional;

/**
 * Shared logic between drains and ducts
 */
public abstract class SmelteryInputOutputTileEntity<T> extends SmelteryComponentTileEntity {
  /** Capability this TE watches */
//  private final Capability<T> capability;
  /**
   * Empty capability for in case the valid capability becomes invalid without invalidating
   */
  protected final T emptyInstance;
  @Nullable
  private Optional<T> capabilityHolder = Optional.empty();

  protected SmelteryInputOutputTileEntity(BlockEntityType<?> type, T emptyInstance) {
    super(type);
    this.emptyInstance = emptyInstance;
  }

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
      world.updateNeighborsAlways(pos, getCachedState().getBlock());
    }
  }

  /**
   * Gets the capability to store in this IO block. Capability parent should have the proper listeners attached
   *
   * @param parent Parent tile entity
   * @return Capability from parent, or empty if absent
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

  /**
   * Fluid implementation of smeltery IO
   */
  public static abstract class SmelteryFluidIO extends SmelteryInputOutputTileEntity<IFluidHandler> {

    protected SmelteryFluidIO(BlockEntityType<?> type) {
      super(type, EmptyFluidHandler.INSTANCE);
    }

    /**
     * Item implementation of smeltery IO
     */
    public static class ChuteTileEntity extends SmelteryInputOutputTileEntity<IItemHandler> {

      public ChuteTileEntity() {
        this(TinkerSmeltery.chute);
      }

      protected ChuteTileEntity(BlockEntityType<?> type) {
        super(type, EmptyItemHandler.INSTANCE);
      }
    }
  }
}
