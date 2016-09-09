package slimeknights.tconstruct.smeltery.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;

public class MultiblockSmeltery extends MultiblockTinker {

  public boolean hasTank;

  public MultiblockSmeltery(TileSmeltery smeltery) {
    super(smeltery, true, false, false);

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
    if(pos.equals(tile.getPos())) {
      return true;
    }

    if(!isValidSlave(world, pos)) {
      return false;
    }

    IBlockState state = world.getBlockState(pos);

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
