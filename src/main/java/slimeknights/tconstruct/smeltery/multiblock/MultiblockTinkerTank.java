package slimeknights.tconstruct.smeltery.multiblock;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.multiblock.MultiServantLogic;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileTinkerTank;

public class MultiblockTinkerTank extends MultiblockCuboid {

  public final TileTinkerTank tank;

  public MultiblockTinkerTank(TileTinkerTank tank) {
    // ceiling, floor, and walls
    super(true, true, true);

    this.tank = tank;
  }

  @Override
  public boolean isValidBlock(World world, BlockPos pos) {
    // controller always is valid
    if(pos.equals(tank.getPos())) {
      return true;
    }

    // main structure can use anything
    return TinkerSmeltery.validTinkerTankBlocks.contains(world.getBlockState(pos).getBlock()) && isValidSlave(world, pos);
  }

  @Override
  public boolean isFrameBlock(World world, BlockPos pos, EnumFrameType type) {
    // controller always is valid
    if(pos.equals(tank.getPos())) {
      return true;
    }

    if(!isValidSlave(world, pos)) {
      return false;
    }

    // the side frames are fine to be anything like normal blocks
    Block block = world.getBlockState(pos).getBlock();
    if(type == EnumFrameType.WALL) {
      return TinkerSmeltery.validTinkerTankBlocks.contains(block);
    }

    // the bottom and top frames have to be seared blocks or drains, for structure
    return block == TinkerSmeltery.searedBlock || block == TinkerSmeltery.smelteryIO;
  }

  @Override
  public boolean isFloorBlock(World world, BlockPos pos) {
    // only bricks for the floor
    return world.getBlockState(pos).getBlock() == TinkerSmeltery.searedBlock && isValidSlave(world, pos);
  }

  private boolean isValidSlave(World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);

    // slave-blocks are only allowed if they already belong to this tank
    if(te instanceof MultiServantLogic) {
      MultiServantLogic slave = (MultiServantLogic) te;
      if(slave.hasValidMaster()) {
        if(!tank.getPos().equals(slave.getMasterPosition())) {
          return false;
        }
      }
    }
    return true;
  }
}
