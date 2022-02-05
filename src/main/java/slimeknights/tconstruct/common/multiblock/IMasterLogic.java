package slimeknights.tconstruct.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Base interface for master block entities */
public interface IMasterLogic {
  private BlockEntity self() {
    return (BlockEntity) this;
  }

  /** Gets the block of the master tile entity */
  default BlockState getMasterBlock() {
    return self().getBlockState();
  }

  /** Gets the position of the master tile entity */
  default BlockPos getMasterPos() {
    return self().getBlockPos();
  }

  /**
   * Called when servants change their state
   *
   * @param servant  Servant tile instance
   * @param pos      Position that changed. May not be the servant position
   * @param state    State that changed. May not be the servant state
   */
  void notifyChange(IServantLogic servant, BlockPos pos, BlockState state);
}
