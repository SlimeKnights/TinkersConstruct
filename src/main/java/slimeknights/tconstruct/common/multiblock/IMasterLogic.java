package slimeknights.tconstruct.common.multiblock;

import net.minecraft.util.math.BlockPos;
import slimeknights.mantle.multiblock.IServantLogic;

// TODO: move back to Mantle after smeltery is updated
public interface IMasterLogic {

  /**
   * Called when servants change their state
   *
   * @param pos servant position
   */
  void notifyChange(IServantLogic servant, BlockPos pos);
}
