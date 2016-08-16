package slimeknights.tconstruct.smeltery.multiblock;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import slimeknights.mantle.multiblock.MultiServantLogic;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSearedFurnace;

public class MultiblockSearedFurnace extends MultiblockCuboid {

  public final TileSearedFurnace furnace;
  public boolean hasTank;

  public MultiblockSearedFurnace(TileSearedFurnace furnace) {
    // perfect cubes only
    super(true, true, true);

    this.furnace = furnace;
  }

  // we need a tank bro
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
    if(pos.equals(furnace.getPos())) {
      return true;
    }

    // only seared blocks in the main structure
    return world.getBlockState(pos).getBlock() == TinkerSmeltery.searedBlock && isValidSlave(world, pos);
  }

  @Override
  public boolean isCeilingBlock(World world, BlockPos pos) {
    // controller always is valid
    if(pos.equals(furnace.getPos())) {
      return true;
    }

    if(!isValidSlave(world, pos)) {
      return false;
    }

    // allow stairs and slabs, but need to be the bottom side
    IBlockState state = world.getBlockState(pos);
    if(state.getBlock() instanceof BlockSlab && state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP) {
      return false;
    }
    if(state.getBlock() instanceof BlockStairs && state.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP) {
      return false;
    }

    return TinkerSmeltery.validSearedFurnaceBlocks.contains(state.getBlock());
  }

  @Override
  public boolean isFrameBlock(World world, BlockPos pos, EnumFrameType type) {
    // controller always is valid
    if(pos.equals(furnace.getPos())) {
      return true;
    }

    if(!isValidSlave(world, pos)) {
      return false;
    }

    // we need a tank, but they are only valid in the frame
    IBlockState state = world.getBlockState(pos);
    if(state.getBlock() == TinkerSmeltery.searedTank) {
      hasTank = true;
      return true;
    }

    // anything is allowed on the ceiling and floor of the frame, just the walls matter
    if(type != EnumFrameType.WALL) {
      return true;
    }

    // the above also allows slabs and stairs on the ceiling, so no need to add it here

    return state.getBlock() == TinkerSmeltery.searedBlock;
  }

  private boolean isValidSlave(World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);

    // slave-blocks are only allowed if they already belong to this smeltery
    if(te instanceof MultiServantLogic) {
      MultiServantLogic slave = (MultiServantLogic) te;
      if(slave.hasValidMaster()) {
        if(!furnace.getPos().equals(slave.getMasterPosition())) {
          return false;
        }
      }
    }
    return true;
  }

}
