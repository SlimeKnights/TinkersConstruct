package slimeknights.tconstruct.world.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlimeBlock;
import slimeknights.tconstruct.world.TinkerWorld;

/** Slime block that only sticks to other slime blocks */
public class BloodSlimeBlock extends SlimeBlock {
  public BloodSlimeBlock(Settings properties) {
    super(properties);
  }

//  @Override
//  public boolean canStickTo(BlockState state, BlockState other) {
//    return TinkerWorld.slime.contains(other.getBlock());
//  }
}
