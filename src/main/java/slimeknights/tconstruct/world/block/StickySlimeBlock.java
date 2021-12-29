package slimeknights.tconstruct.world.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SlimeBlock;

import java.util.function.BiPredicate;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class StickySlimeBlock extends SlimeBlock {

  private final BiPredicate<BlockState, BlockState> stickyPredicate;
  public StickySlimeBlock(Properties properties, BiPredicate<BlockState, BlockState> stickyPredicate) {
    super(properties);
    this.stickyPredicate = stickyPredicate;
  }

  @Override
  public boolean isSlimeBlock(BlockState state) {
    return true;
  }

  @Override
  public boolean isStickyBlock(BlockState state) {
    return true;
  }

  @Override
  public boolean canStickTo(BlockState state, BlockState other) {
    return stickyPredicate.test(state, other);
  }
}
