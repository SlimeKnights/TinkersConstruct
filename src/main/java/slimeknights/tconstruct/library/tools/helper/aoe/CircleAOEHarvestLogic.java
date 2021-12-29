package slimeknights.tconstruct.library.tools.helper.aoe;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.RectangleAOEHarvestLogic.RectangleIterator;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.Collections;
import java.util.function.Predicate;

import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic.AOEMatchType;

/** AOE harvest logic that mines blocks in a circle */
@RequiredArgsConstructor
public class CircleAOEHarvestLogic extends ToolHarvestLogic {
  public static final CircleAOEHarvestLogic SMALL_2D = new CircleAOEHarvestLogic(1, false);

  protected final int diameter;
  protected final boolean is3D;

  @Override
  public Iterable<BlockPos> getAOEBlocks(IModifierToolStack tool, ItemStack stack, Player player, BlockState state, Level world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
    // expanded gives an extra width every odd level, and an extra height every even level
    int expanded = tool.getModifierLevel(TinkerModifiers.expanded.get());
    return calculate(this, tool, stack, world, player, origin, sideHit, diameter + expanded, is3D, matchType);
  }

  /**
   *
   * @param self       Harvest logic instance
   * @param tool       Tool used for harvest
   * @param stack      Item stack used for harvest (for vanilla hooks)
   * @param world      World containing the block
   * @param player     Player harvesting
   * @param origin     Center of harvest
   * @param sideHit    Block side hit
   * @param diameter   Circle diameter
   * @param matchType  Type of harvest being performed
   * @return  List of block positions
   */
  public static Iterable<BlockPos> calculate(ToolHarvestLogic self, IModifierToolStack tool, ItemStack stack, Level world, Player player, BlockPos origin, Direction sideHit, int diameter, boolean is3D, AOEMatchType matchType) {
    // skip if no work
    if (diameter == 1) {
      return Collections.emptyList();
    }

    Direction depthDir = sideHit.getOpposite();
    // for Y, direction is based on facing
    Direction widthDir, heightDir;
    if (sideHit.getAxis() == Axis.Y) {
      heightDir = player.getDirection();
      widthDir = heightDir.getClockWise();
    } else {
      // for X and Z, just rotate from side hit
      widthDir = sideHit.getCounterClockWise();
      heightDir = Direction.UP;
    }

    // finally, return the iterator
    Predicate<BlockPos> posPredicate = getDefaultBlockPredicate(self, tool, stack, world, origin, matchType);

    // math works out that we can leave this an integer and get the radius working still
    int radiusSq = diameter * diameter / 4;
    // max needs to be an odd number
    return () -> new CircleIterator(origin, widthDir, heightDir, depthDir, radiusSq, diameter / 2, is3D, posPredicate);
  }

  /** Iterator used for getting the blocks, secret is a circle is a rectangle */
  private static class CircleIterator extends RectangleIterator {
    /* Diameter of the area to mine, circular */
    private final int radiusSq;
    private CircleIterator(BlockPos origin, Direction widthDir, Direction heightDir, Direction depthDir, int radiusSq, int extra, boolean is3D, Predicate<BlockPos> posPredicate) {
      super(origin, widthDir, extra, heightDir, extra, depthDir, is3D ? extra : 0, posPredicate);
      this.radiusSq = radiusSq;
    }

    /** Gets the squared distance between the origin and the mutable position */
    private int distanceSq() {
      // built in method returns a double, thats overkill
      int dx = origin.getX() - mutablePos.getX();
      int dy = origin.getY() - mutablePos.getY();
      int dz = origin.getZ() - mutablePos.getZ();
      return dx*dx + dy*dy + dz*dz;
    }

    @Override
    protected BlockPos computeNext() {
      // ensure the position did not get changed by the consumer last time
      mutablePos.set(lastX, lastY, lastZ);
      // as long as we have another position, try using it
      while (incrementPosition()) {
        // skip over the origin
        // ensure it matches the predicate
        if (!mutablePos.equals(origin) && distanceSq() <= radiusSq && posPredicate.test(mutablePos)) {
          // store position in case the consumer changes it
          lastX = mutablePos.getX();
          lastY = mutablePos.getY();
          lastZ = mutablePos.getZ();
          return mutablePos;
        }
      }
      return endOfData();
    }
  }
}
