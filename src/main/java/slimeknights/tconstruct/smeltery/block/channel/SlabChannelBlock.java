package slimeknights.tconstruct.smeltery.block.channel;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlabChannelBlock extends AbstractChannelBlock {
  public SlabChannelBlock(Properties props) {
    super(props, ConnectionType.NONE, ConnectionType.NONE);
  }

  @Override
  protected int makeShapeKey(boolean _up, boolean _down, boolean north, boolean south, boolean west, boolean east) {
    int v = 0;
    if (north) v |= 0b0001;
    if (south) v |= 0b0010;
    if (east)  v |= 0b0100;
    if (west)  v |= 0b1000;
    return v;
  }

  @Override
  protected VoxelShape[] createShapes() {
    VoxelShape base = box(0, 0, 0, 16, 8, 16);
    VoxelShape northCutout = box( 6, 6,  0, 10, 9,  4);
    VoxelShape southCutout = box( 6, 6, 12, 10, 9, 16);
    VoxelShape westCutout = box( 0, 6,  6,  4, 9, 10);
    VoxelShape eastCutout = box(12, 6,  6, 16, 9, 10);

    var booleans = new boolean[] { false, true };
    var shapes = new VoxelShape[16];
    for (var north : booleans) {
      for (var south : booleans) {
        for (var west : booleans) {
          for (var east : booleans) {
            var shape = base;
            if (north) shape = Shapes.joinUnoptimized(shape, northCutout, BooleanOp.ONLY_FIRST);
            if (south) shape = Shapes.joinUnoptimized(shape, southCutout, BooleanOp.ONLY_FIRST);
            if (west) shape = Shapes.joinUnoptimized(shape, westCutout, BooleanOp.ONLY_FIRST);
            if (east) shape = Shapes.joinUnoptimized(shape, eastCutout, BooleanOp.ONLY_FIRST);
            shapes[makeShapeKey(false, false, north, south, west, east)] = shape.optimize();
          }
        }
      }
    }
    return shapes;
  }

  @Override
  protected Direction getHitSide(Vec3 hitVec, Direction side) {
    // map X and Z coords to a direction
    boolean northish = hitVec.z < 0.5;
    boolean westish = hitVec.x < 0.5;

    var x = Math.abs(hitVec.x - 0.5);
    var z = Math.abs(hitVec.z - 0.5);

    if (x > z)
      return westish ? Direction.WEST : Direction.EAST;
    else
      return northish ? Direction.NORTH : Direction.SOUTH;
  }
}
