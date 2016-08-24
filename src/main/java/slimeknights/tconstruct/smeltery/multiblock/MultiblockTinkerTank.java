package slimeknights.tconstruct.smeltery.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
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
    IBlockState state = world.getBlockState(pos);
    Block block = state.getBlock();
    if(type == EnumFrameType.WALL) {
      return TinkerSmeltery.validTinkerTankBlocks.contains(block);
    }
    // allow stairs and slabs on the ceiling, but they need to be the bottom side
    else if(type == EnumFrameType.CEILING) {
      if(block instanceof BlockSlab && state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP) {
        return false;
      }
      if(block instanceof BlockStairs && state.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP) {
        return false;
      }

      if(TinkerSmeltery.searedStairsSlabs.contains(block)) {
        return true;
      }
    }

    // the bottom and top frames have to be seared blocks or drains, for structure
    return block == TinkerSmeltery.searedBlock || block == TinkerSmeltery.smelteryIO;
  }


  @Override
  public boolean isCeilingBlock(World world, BlockPos pos) {
    // controller always is valid
    if(pos.equals(tank.getPos())) {
      return true;
    }

    if(!isValidSlave(world, pos)) {
      return false;
    }

    // allow stairs and slabs, but they must be upside down
    IBlockState state = world.getBlockState(pos);
    Block block = state.getBlock();
    if(block instanceof BlockSlab && state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP) {
      return false;
    }
    if(block instanceof BlockStairs && state.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP) {
      return false;
    }

    return TinkerSmeltery.searedStairsSlabs.contains(block) || TinkerSmeltery.validTinkerTankBlocks.contains(block);
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
