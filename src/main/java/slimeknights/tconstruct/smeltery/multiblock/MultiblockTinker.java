package slimeknights.tconstruct.smeltery.multiblock;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import slimeknights.mantle.multiblock.MultiServantLogic;
import slimeknights.tconstruct.smeltery.tileentity.TileMultiblock;

public abstract class MultiblockTinker extends MultiblockCuboid {

  public final TileMultiblock<?> tile;

  public MultiblockTinker(TileMultiblock<?> tile, boolean hasFloor, boolean hasFrame, boolean hasCeiling) {
    super(hasFloor, hasFrame, hasCeiling);
    this.tile = tile;
  }

  protected boolean isValidSlave(World world, BlockPos pos) {
    if(!world.isBlockLoaded(pos)) {
      return false;
    }
    TileEntity te = world.getTileEntity(pos);

    // slave-blocks are only allowed if they already belong to this smeltery
    if(te instanceof MultiServantLogic) {
      MultiServantLogic slave = (MultiServantLogic) te;
      if(slave.hasValidMaster()) {
        if(!tile.getPos().equals(slave.getMasterPosition())) {
          return false;
        }
      }
    }

    return true;
  }
}
