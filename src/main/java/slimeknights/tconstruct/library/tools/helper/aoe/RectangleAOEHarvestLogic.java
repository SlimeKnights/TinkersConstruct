package slimeknights.tconstruct.library.tools.helper.aoe;

import com.google.common.collect.AbstractIterator;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.Collections;
import java.util.function.Predicate;

import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic.AOEMatchType;

/** AOE harvest logic that mines blocks in a rectangle */
@RequiredArgsConstructor
public class RectangleAOEHarvestLogic extends ToolHarvestLogic {
  public static final RectangleAOEHarvestLogic SMALL = new RectangleAOEHarvestLogic(0, 0, 0);
  public static final RectangleAOEHarvestLogic LARGE = new RectangleAOEHarvestLogic(1, 1, 0);

  protected final int extraWidth;
  protected final int extraHeight;
  protected final int extraDepth;

  @Override
  public Iterable<BlockPos> getAOEBlocks(IModifierToolStack tool, ItemStack stack, Player player, BlockState state, Level world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
    // expanded gives an extra width every odd level, and an extra height every even level
    int expanded = tool.getModifierLevel(TinkerModifiers.expanded.get());
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
  public static Iterable<BlockPos> calculate(ToolHarvestLogic self, IModifierToolStack tool, ItemStack stack, Level world, Player player, BlockPos origin, Direction sideHit,
                                        int extraWidth, int extraHeight, int extraDepth, AOEMatchType matchType) {
    // skip if no work
    if (extraDepth == 0 && extraWidth == 0 && extraHeight == 0) {
      return Collections.emptyList();
    }

    // depth is always direction into the block
    Direction depthDir = sideHit.getOpposite();
    Direction widthDir, heightDir;
    // for Y, direction is based on facing
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
    protected final BlockPos.MutableBlockPos mutablePos;
    /** Predicate to check before returning a position */
    protected final Predicate<BlockPos> posPredicate;
    /** Last returned values for the three coords */
    protected int lastX, lastY, lastZ;

    public RectangleIterator(BlockPos origin, Direction widthDir, int extraWidth, Direction heightDir, int extraHeight, Direction depthDir, int extraDepth, Predicate<BlockPos> posPredicate) {
      this(origin, widthDir, extraWidth, heightDir, extraHeight, true, depthDir, extraDepth, posPredicate);
    }

    /**
     * Iterates through a rectangular solid
     * @param origin         Center position
     * @param widthDir       Direction for width traversal
     * @param extraWidth     Radius in width direction
     * @param heightDir      Direction for height traversal
     * @param extraHeight    Amount in the height direction
     * @param traverseDown   If true, navigates extraHeight both up and down
     * @param depthDir       Direction to travel backwards
     * @param extraDepth     Extra amount to traverse in the backwards direction
     * @param posPredicate   Predicate to validate positions
     */
    public RectangleIterator(BlockPos origin, Direction widthDir, int extraWidth, Direction heightDir, int extraHeight, boolean traverseDown, Direction depthDir, int extraDepth, Predicate<BlockPos> posPredicate) {
      this.origin = origin;
      this.widthDir = widthDir;
      this.heightDir = heightDir;
      this.depthDir = depthDir;
      this.maxWidth = extraWidth * 2;
      this.maxHeight = traverseDown ? extraHeight * 2 : extraHeight;
      this.maxDepth = extraDepth;
      // start 1 block before start on the correct axis
      // computed values
      this.mutablePos = new MutableBlockPos(origin.getX(), origin.getY(), origin.getZ());
      this.posPredicate = posPredicate;
      // offset position back by 1 so we start at 0, 0, 0
      if (extraWidth > 0) {
        currentWidth--;
      } else if (extraHeight > 0) {
        currentHeight--;
      }
      // offset the mutable position back along the rectangle
      this.mutablePos.move(widthDir, -extraWidth + currentWidth);
      if (traverseDown) {
        this.mutablePos.move(heightDir, -extraHeight + currentHeight);
      } else if (currentHeight != 0) {
        this.mutablePos.move(heightDir, currentHeight);
      }
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
