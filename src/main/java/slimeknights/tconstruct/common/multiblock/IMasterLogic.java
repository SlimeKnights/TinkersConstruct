package slimeknights.tconstruct.common.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeTileEntity;

// TODO: move back to Mantle after smeltery is updated
public interface IMasterLogic extends IForgeTileEntity {

  /**
   * Called when servants change their state
   *
   * @param servant  Servant tile instance
   * @param pos      Position that changed. May not be the servant position
   * @param state    State that changed. May not be the servant state
   */
  void notifyChange(IServantLogic servant, BlockPos pos, BlockState state);
}
