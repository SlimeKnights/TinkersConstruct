package slimeknights.tconstruct.smeltery.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileTinkerTank;

public class MultiblockTinkerTank extends MultiblockTinker {

  public MultiblockTinkerTank(TileTinkerTank tank) {
    // ceiling, floor, and walls
    super(tank, true, true, true);
  }

  @Override
  public boolean isValidBlock(World world, BlockPos pos) {
    // controller always is valid
    if(pos.equals(tile.getPos())) {
      return true;
    }

    // main structure can use anything
    return TinkerSmeltery.validTinkerTankBlocks.contains(world.getBlockState(pos).getBlock()) && isValidSlave(world, pos);
  }

  @Override
  public boolean isFrameBlock(World world, BlockPos pos, EnumFrameType type) {
    // controller always is valid
    if(pos.equals(tile.getPos())) {
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
    if(pos.equals(tile.getPos())) {
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
    return TinkerSmeltery.validTinkerTankFloorBlocks.contains(world.getBlockState(pos).getBlock()) && isValidSlave(world, pos);
  }

}
