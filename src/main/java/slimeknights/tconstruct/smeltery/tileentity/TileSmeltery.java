package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.IServantLogic;

public class TileSmeltery extends TileEntity implements IMasterLogic {

  @Override
  public void notifyChange(IServantLogic servant, BlockPos pos) {

  }
}
