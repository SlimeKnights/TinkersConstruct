package slimeknights.tconstruct.common.multiblock;

import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.IServantLogic;

// TODO: move back to Mantle after smeltery is updated
public interface IMasterNode extends slimeknights.mantle.multiblock.IMasterLogic, IServantLogic {

  boolean isCurrentlyMaster();

  boolean isEquivalentMaster(IMasterLogic master);
}
