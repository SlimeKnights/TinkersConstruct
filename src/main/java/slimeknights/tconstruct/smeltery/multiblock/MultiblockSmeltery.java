package slimeknights.tconstruct.smeltery.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import slimeknights.mantle.multiblock.IServantLogic;
import slimeknights.mantle.multiblock.MultiServantLogic;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;

public class MultiblockSmeltery extends MultiblockCuboid {

  public final TileSmeltery smeltery;
  public boolean hasTank;

  public MultiblockSmeltery(TileSmeltery smeltery) {
    super(true, false, false);

    this.smeltery = smeltery;
    this.hasTank = false;
  }

  @Override
  public MultiblockStructure detectMultiblock(World world, BlockPos center, int limit) {
    hasTank = false;
    MultiblockStructure ret = super.detectMultiblock(world, center, limit);
    if(!hasTank) {
      return null;
    }
    return ret;
  }

  @Override
  public boolean isValidBlock(World world, BlockPos pos) {
    // controller always is valid
    if(pos.equals(smeltery.getPos())) {
      return true;
    }

    IBlockState state = world.getBlockState(pos);
    TileEntity te = world.getTileEntity(pos);

    // slave-blocks are only allowed if they already belong to this smeltery
    if(te instanceof MultiServantLogic) {
      MultiServantLogic slave = (MultiServantLogic) te;
      if(slave.hasValidMaster()) {
        if(!smeltery.getPos().equals(slave.getMasterPosition())) {
          return false;
        }
      }
    }

    // we need a tank
    if(state.getBlock() == TinkerSmeltery.searedTank) {
      hasTank = true;
      return true;
    }

    return TinkerSmeltery.validSmelteryBlocks.contains(state.getBlock());
  }

  @Override
  public boolean isFloorBlock(World world, BlockPos pos) {
    // only bricks for the floor
    return world.getBlockState(pos).getBlock() == TinkerSmeltery.searedBlock && isValidBlock(world, pos);
  }
}
