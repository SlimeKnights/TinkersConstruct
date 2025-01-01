package slimeknights.tconstruct.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.world.TinkerWorld;

import static net.minecraft.world.level.block.MangrovePropaguleBlock.AGE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HANGING;

/** Recreation of {@link net.minecraft.world.level.block.MangroveLeavesBlock} with slimy leaves behavior */
public class SlimePropaguleLeavesBlock extends SlimeLeavesBlock implements BonemealableBlock {
  public SlimePropaguleLeavesBlock(Properties properties, FoliageType foliageType) {
    super(properties, foliageType);
  }

  @Override
  public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
    return level.getBlockState(pos.below()).isAir();
  }

  @Override
  public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
    return true;
  }

  @Override
  public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
    level.setBlock(pos.below(), TinkerWorld.slimeSapling.get(FoliageType.ENDER).defaultBlockState().setValue(HANGING, Boolean.TRUE).setValue(AGE, 0), 2);
  }
}
