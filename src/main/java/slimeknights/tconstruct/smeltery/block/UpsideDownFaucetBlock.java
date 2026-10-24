package slimeknights.tconstruct.smeltery.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.FaucetBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.UpsideDownFaucetBlockEntity;

import javax.annotation.Nullable;
import java.util.EnumMap;

public class UpsideDownFaucetBlock extends FaucetBlock {

  private static final EnumMap<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(
    // Despite reusing the DOWN property, this one in fact points up.
    Direction.DOWN,  Shapes.join(box( 4, 0,  4, 12,  6, 12), box( 6, 0,  6, 10,  6, 10), BooleanOp.ONLY_FIRST),
    Direction.NORTH, Shapes.join(box( 4, 6, 10, 12, 12, 16), box( 6, 6, 10, 10, 10, 16), BooleanOp.ONLY_FIRST),
    Direction.SOUTH, Shapes.join(box( 4, 6,  0, 12, 12,  6), box( 6, 6,  0, 10, 10,  6), BooleanOp.ONLY_FIRST),
    Direction.WEST,  Shapes.join(box(10, 6,  4, 16, 12, 12), box(10, 6,  6, 16, 10, 10), BooleanOp.ONLY_FIRST),
    Direction.EAST,  Shapes.join(box( 0, 6,  4,  6, 12, 12), box( 0, 6,  6,  6, 10, 10), BooleanOp.ONLY_FIRST)));


  public UpsideDownFaucetBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected EnumMap<Direction, VoxelShape> getPrecomputedShapes() {
    return SHAPES;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new UpsideDownFaucetBlockEntity(pPos, pState);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type) {
    return BlockEntityHelper.serverTicker(pLevel, type, TinkerSmeltery.upsideDownFaucetEntity.get(), FaucetBlockEntity.SERVER_TICKER);
  }
}
