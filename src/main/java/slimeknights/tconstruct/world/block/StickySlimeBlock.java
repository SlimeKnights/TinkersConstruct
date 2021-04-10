package slimeknights.tconstruct.world.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlimeBlock;

import java.util.function.BiPredicate;

public class StickySlimeBlock extends SlimeBlock {

  private final BiPredicate<BlockState, BlockState> stickyPredicate;
  public StickySlimeBlock(Settings properties, BiPredicate<BlockState, BlockState> stickyPredicate) {
    super(properties);
    this.stickyPredicate = stickyPredicate;
  }

//  @Override
//  public boolean isSlimeBlock(BlockState state) {
//    return true;
//  }
//
//  @Override
//  public boolean isStickyBlock(BlockState state) {
//    return true;
//  }
//
//  @Override
//  public boolean canStickTo(BlockState state, BlockState other) {
//    return stickyPredicate.test(state, other);
//  }
}
