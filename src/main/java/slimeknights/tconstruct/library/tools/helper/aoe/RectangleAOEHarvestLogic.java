package slimeknights.tconstruct.library.tools.helper.aoe;

import com.google.common.collect.AbstractIterator;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.Collections;
import java.util.function.Predicate;

/** AOE harvest logic that mines blocks in a rectangle */
@RequiredArgsConstructor
public class RectangleAOEHarvestLogic extends ToolHarvestLogic {
  public static final RectangleAOEHarvestLogic SMALL = new RectangleAOEHarvestLogic(0, 0, 0);
  public static final RectangleAOEHarvestLogic LARGE = new RectangleAOEHarvestLogic(1, 1, 0);

  protected final int extraWidth;
  protected final int extraHeight;
  protected final int extraDepth;

  @Override
  public Iterable<BlockPos> getAOEBlocks(ToolStack tool, ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
    if (!canAOE(tool, stack, state, matchType)) {
      return Collections.emptyList();
    }
    // expanded gives an extra width every odd level, and an extra height every even level
    int expanded = tool.getModifierLevel(TinkerModifiers.expanded);
    return calculate(this, tool, stack, world, player, origin, sideHit, extraWidth + ((expanded + 1) / 2), extraHeight + (expanded / 2), extraDepth, matchType);
  }

  /**
   *
   * @param self          Harvest logic instance
   * @param tool          Tool used for harvest
   * @param stack         Item stack used for harvest (for vanilla hooks)
   * @param world         World containing the block
   * @param player        Player harvesting
   * @param origin        Center of harvest
   * @param sideHit       Block side hit
   * @param extraWidth    Bonus harvest width
   * @param extraHeight   Bonus harvest height
   * @param extraDepth    Bonus harvest depth
   * @param matchType     Type of harvest being performed
   * @return  List of block positions
   */
  public static Iterable<BlockPos> calculate(ToolHarvestLogic self, ToolStack tool, ItemStack stack, World world, PlayerEntity player, BlockPos origin, Direction sideHit,
                                        int extraWidth, int extraHeight, int extraDepth, AOEMatchType matchType) {
    // skip if no work
    if (extraDepth == 0 && extraWidth == 0 && extraHeight == 0) {
      return Collections.emptyList();
    }

    // depth is always direction into the block
    Direction depthDir = sideHit.getOpposite();
    Direction widthDir, heightDir;
    // for Y, direction is based on facing
    if (sideHit.getAxis() == Direction.Axis.Y) {
      heightDir = player.getHorizontalFacing();
      widthDir = heightDir.rotateYClockwise();
    } else {
      // for X and Z, just rotate from side hit
      widthDir = sideHit.rotateYCounterclockwise();
      heightDir = Direction.UP;
    }

    // finally, return the iterator
    Predicate<BlockPos> posPredicate = getDefaultBlockPredicate(self, tool, stack, world, origin, matchType);
    return () -> new RectangleIterator(origin, widthDir, extraWidth, heightDir, extraHeight, depthDir, extraDepth, posPredicate);
  }

  /** Iterator used for getting the blocks */
  public static class RectangleIterator extends AbstractIterator<BlockPos> {
    /** Primary direction of iteration */
    private final Direction widthDir;
    /** Secondary direction of iteration, mostly interchangable with primary */
    private final Direction heightDir;
    /** Direction of iteration away from the player */
    private final Direction depthDir;

    /* Bounding box size in the direction of width */
    private final int maxWidth;
    /* Bounding box size in the direction of height */
    private final int maxHeight;
    /* Bounding box size in the direction of depth */
    private final int maxDepth;

    /** Current position in the direction of width */
    private int currentWidth = 0;
    /** Current position in the direction of height */
    private int currentHeight = 0;
    /** Current position in the direction of depth */
    private int currentDepth = 0;

    /** Original position, skipped in iteration */
    protected final BlockPos origin;
    /** Position modified as we iterate */
    protected final BlockPos.Mutable mutablePos;
    /** Predicate to check before returning a position */
    protected final Predicate<BlockPos> posPredicate;
    /** Last returned values for the three coords */
    protected int lastX, lastY, lastZ;

    public RectangleIterator(BlockPos origin, Direction widthDir, int extraWidth, Direction heightDir, int extraHeight, Direction depthDir, int extraDepth, Predicate<BlockPos> posPredicate) {
      this.origin = origin;
      this.widthDir = widthDir;
      this.heightDir = heightDir;
      this.depthDir = depthDir;
      this.maxWidth = extraWidth * 2;
      this.maxHeight = extraHeight * 2;
      this.maxDepth = extraDepth;
      // start 1 block before start on the correct axis
      // computed values
      this.mutablePos = new Mutable(origin.getX(), origin.getY(), origin.getZ());
      this.posPredicate = posPredicate;
      if (extraWidth > 0) {
        currentWidth--;
      } else if (extraHeight > 0) {
        currentHeight--;
      }
      this.mutablePos.move(widthDir, -extraWidth + currentWidth).move(heightDir, -extraHeight + currentHeight);
      this.lastX = this.mutablePos.getX();
      this.lastY = this.mutablePos.getY();
      this.lastZ = this.mutablePos.getZ();
    }

    /**
     * Updates the mutable block position
     * @return False if at the end of data
     */
    protected boolean incrementPosition() {
      // first, increment values
      // if at the end of the width, increment height
      if (currentWidth == maxWidth) {
        // at the end of the height, increment depth
        if (currentHeight == maxHeight) {
          // at the end of depth, we are done
          if (currentDepth == maxDepth) {
            return false;
          }
          // increase depth
          currentDepth++;
          mutablePos.move(depthDir);
          // reset height
          currentHeight = 0;
          mutablePos.move(heightDir, -maxHeight);
        } else {
          currentHeight++;
          mutablePos.move(heightDir);
        }
        currentWidth = 0;
        mutablePos.move(widthDir, -maxWidth);
      } else {
        currentWidth++;
        mutablePos.move(widthDir);
      }
      return true;
    }

    @Override
    protected BlockPos computeNext() {
      // ensure the position did not get changed by the consumer last time
      mutablePos.set(lastX, lastY, lastZ);
      // as long as we have another position, try using it
      while (incrementPosition()) {
        // skip over the origin, ensure it matches the predicate
        if (!mutablePos.equals(origin) && posPredicate.test(mutablePos)) {
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
