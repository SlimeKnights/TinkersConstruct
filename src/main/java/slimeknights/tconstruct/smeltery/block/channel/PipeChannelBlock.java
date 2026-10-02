package slimeknights.tconstruct.smeltery.block.channel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;

import java.util.Arrays;


public class PipeChannelBlock extends AbstractChannelBlock {

  public PipeChannelBlock(Properties props) {
    super(props, ConnectionType.TWO_WAY, ConnectionType.TWO_WAY);
  }

  @Override
  protected int makeShapeKey(boolean up, boolean down, boolean north, boolean south, boolean west, boolean east) {
    int v = 0;
    if (up)    v |= 0b000001;
    if (down)  v |= 0b000010;
    if (north) v |= 0b000100;
    if (south) v |= 0b001000;
    if (east)  v |= 0b010000;
    if (west)  v |= 0b100000;
    return v;
  }

  @Override
  protected VoxelShape[] createShapes() {
    VoxelShape base =            box(4, 4, 4, 12, 12, 12);
    VoxelShape upConnection =    box(4, 8, 4, 12, 16, 12);
    VoxelShape downConnection =  box(4, 0, 4, 12,  8, 12);
    VoxelShape northConnection = box(4, 4, 0, 12, 12,  8);
    VoxelShape southConnection = box(4, 4, 8, 12, 12, 16);
    VoxelShape westConnection =  box(0, 4, 4,  8, 12, 12);
    VoxelShape eastConnection =  box(8, 4, 4, 16, 12, 12);

    var booleans = new boolean[] {false, true};
    var shapes = new VoxelShape[64];
    var builder = new VoxelShape[7];
    builder[0] = base;
    for (var u : booleans) {
      builder[1] = u ? upConnection : Shapes.empty();
      for (var d : booleans) {
        builder[2] = d ? downConnection : Shapes.empty();
        for (var n : booleans) {
          builder[3] = n ? northConnection : Shapes.empty();
          for (var s : booleans) {
            builder[4] = s ? southConnection : Shapes.empty();
            for (var w : booleans) {
              builder[5] = w ? westConnection : Shapes.empty();
              for (var e : booleans) {
                builder[6] = e ? eastConnection : Shapes.empty();
                shapes[makeShapeKey(u, d, n, s, w, e)] = Arrays.stream(builder).reduce((s1, s2) -> Shapes.joinUnoptimized(s1, s2, BooleanOp.OR)).get().optimize();
              }
            }
          }
        }
      }
    }
    return shapes;
  }

  @Override
  protected Direction getHitSide(Vec3 hitVec, Direction side) {
    boolean northish = hitVec.z < 0.5;
    boolean westish = hitVec.x < 0.5;
    boolean downish = hitVec.y < 0.5;

    var x = Math.abs(hitVec.x - 0.5);
    var y = Math.abs(hitVec.y - 0.5);
    var z = Math.abs(hitVec.z - 0.5);

    if (x > y && x > z)
      return westish ? Direction.WEST : Direction.EAST;
    if (z > y && z > x)
      return northish ? Direction.NORTH : Direction.SOUTH;
    if (y > x && y > z)
      return downish ? Direction.DOWN : Direction.UP;

    return side;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(UP_2WAY);
    builder.add(DOWN_2WAY);
    super.createBlockStateDefinition(builder);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new ChannelBlockEntity(false, pPos, pState);
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> givenType) {
    return BlockEntityHelper.serverTicker(pLevel, givenType, TinkerSmeltery.channelNoRender.get(), ChannelBlockEntity.SERVER_TICKER);
  }
}
