package slimeknights.tconstruct.smeltery.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;

public class MultiblockSmeltery extends MultiblockTinker {

  public boolean hasTank;

  public MultiblockSmeltery(SmelteryTileEntity smeltery) {
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

    BlockState state = world.getBlockState(pos);

    // we need a tank
    if(TinkerSmeltery.searedTank.contains(state.getBlock())) {
      System.out.println("hasTank = true");
      hasTank = true;
      return true;
    }

    return TinkerSmeltery.validSmelteryBlocks.contains(state.getBlock());
  }

  @Override
  public boolean isFloorBlock(World world, BlockPos pos) {
    // only bricks for the floor
    return world.getBlockState(pos).getBlock() == TinkerSmeltery.searedBricks.get() && isValidBlock(world, pos);
  }
}
