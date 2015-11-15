package slimeknights.tconstruct.smeltery.multiblock;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class MultiblockSmeltery extends MultiblockCuboid {

  public MultiblockSmeltery() {
    super(true, false, false);
  }

  @Override
  public boolean isValidBlock(World world, BlockPos pos) {
    return false;
  }
}
