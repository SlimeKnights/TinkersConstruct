package slimeknights.tconstruct.common.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

// TODO: move back to Mantle after smeltery is updated
public interface IServantLogic {
  /**
   * Gets the position of the master block
   * @return  Master position, null if none is set
   */
  @Nullable
  BlockPos getMasterPos();

  /**
   * Notifies the master that something changed
   * @param pos    Position that changed. May or may not be this servant
   * @param state  State that changed. May or may not be this servant
   */
  void notifyMasterOfChange(BlockPos pos, BlockState state);

  /**
   * Checks if the given master is valid for this servant. Should consider the servants current state
   * @param master  Master to check
   * @return  True if the master is a valid master
   */
  boolean isValidMaster(IMasterLogic master);

  /**
   * Sets a master to this slave, assuming it is valid
   * @param master  Master to set
   */
  void setPotentialMaster(IMasterLogic master);

  /**
   * Removes this master from the given servant
   * @param master  Master to remove
   */
	void removeMaster(IMasterLogic master);
}
