package slimeknights.tconstruct.library.tools.definition.module.aoe;

import com.google.common.collect.AbstractIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Tree harvest logic that destroys a tree
 * @param width  Absolute distance to the left or right to mine, 0 or more
 * @param depth  How far back to mine into the tree beyond the first block, 0 or more
 */
public record TreeAOEIterator(int width, int depth) implements AreaOfEffectIterator.Loadable {
  public static final RecordLoadable<TreeAOEIterator> LOADER = RecordLoadable.create(
    IntLoadable.FROM_ZERO.defaultField("width_bonus", 0, true, TreeAOEIterator::width),
    IntLoadable.FROM_ZERO.defaultField("depth_bonus", 0, true, TreeAOEIterator::depth),
    TreeAOEIterator::new);

  /** Max distance between the branch and the trunk */
  private static final int MAX_BRANCH_DISTANCE = 10;

  @Override
  public RecordLoadable<TreeAOEIterator> getLoader() {
    return LOADER;
  }

  @Override
  public Iterable<BlockPos> getBlocks(IToolStackView tool, UseOnContext context, BlockState state, AOEMatchType matchType) {
    int expanded = tool.getModifierLevel(TinkerModifiers.expanded.getId());
    return calculate(context.getLevel(), context.getPlayer(), context.getClickedPos(), context.getClickedFace(), state, width + (expanded + 1) / 2, depth + expanded / 2);
  }

  /**
   * Gets an iterator, either for a tree, or falling back to a cube
   * @param level           Level
   * @param player          Player interacting, for direction choice
   * @param origin          Origin position
   * @param sideHit         Direction to iterate
   * @param state           State being mined
   * @param extraWidth      Mining width
   * @param extraDepth      Mining depth
   * @return  Correct iterator for the targeted block
   */
  public static Iterable<BlockPos> calculate(Level level, @Nullable Player player, BlockPos origin, Direction sideHit, BlockState state, int extraWidth, int extraDepth) {
    Direction depthDir;
    Direction widthDir;
    // if we have expanders, add them in
    if (extraDepth > 0 || extraWidth > 0) {
      // if hit the top or bottom, use facing direction
      if (sideHit.getAxis().isVertical()) {
        depthDir = player != null ? player.getDirection() : Direction.NORTH;
      } else {
        depthDir = sideHit.getOpposite();
      }
      widthDir = depthDir.getClockWise();
    } else {
      depthDir = Direction.UP;
      widthDir = Direction.UP;
    }
    // TODO: would be nice to allow the stipped logs here as well as the logs
    return () -> new TreeIterator(level, state.getBlock(), origin, widthDir, extraWidth, depthDir, extraDepth);
  }

  /**
   * Iterator that continues up until the block does not match.
   * The way this works is it starts with the given dimensions to form the trunk. The trunk can then extend off into branches in the vertical or in horizontal directions.
   *
   * Horizontal branches detect in a 3x2x2 area from the stored direction and upwards and don't care about whether the logs have a block below. They also can only split into up to 3 pieces
   * Vertical branches check a 3x3x1 cross shape above, requiring nothing to be below the block. They can split into up to 5 pieces
   * The trunk can start new branches within a 3x3x1 square area, again requiring nothing to be below the block
   */
  public static class TreeIterator extends AbstractIterator<BlockPos> {
    /** Queue of upcoming positions to try */
    private final Queue<TreePos> upcomingPositions = new ArrayDeque<>();
    /** Position for returns, saves some object allocation */
    private final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
    /** Branches that have been visited already */
    private final Set<BlockPos> branchVisited = new HashSet<>();

    private final Level world;
    private final Block filter;
    /** Bounds for branch detection */
    private final int minX, maxX, minZ, maxZ;
    public TreeIterator(Level world, Block filter, BlockPos origin, Direction widthDir, int extraWidth, Direction depthDir, int extraDepth) {
      this.world = world;
      this.filter = filter;

      // first, enqueue the origin
      upcomingPositions.add(new TreePos(origin.above(), false));

      // next, start adding AOE
      int minX = origin.getX();
      int maxX = minX;
      int minZ = origin.getZ();
      int maxZ = minZ;
      if (extraDepth > 0 || extraWidth > 0) {
        // add extra blocks
        for (int d = 0; d <= extraDepth; d++) {
          for (int w = -extraWidth; w <= extraWidth; w++) {
            if (d != 0 || w != 0) {
              // if its valid, queue
              mutable.set(origin).move(depthDir, d).move(widthDir, w);
              if (isValidBlock(mutable)) {
                upcomingPositions.add(new TreePos(mutable, true));
                // update bounds
                if (mutable.getX() < minX) minX = mutable.getX();
                if (mutable.getX() > maxX) maxX = mutable.getX();
                if (mutable.getZ() < minZ) minZ = mutable.getZ();
                if (mutable.getZ() > maxZ) maxZ = mutable.getZ();
              }
            }
          }
        }
      }
      this.minX = minX;
      this.maxX = maxX;
      this.minZ = minZ;
      this.maxZ = maxZ;
    }

    /** Checks if the position matches the filter block */
    private boolean isValidBlock(BlockPos pos) {
      return world.getBlockState(pos).getBlock() == filter;
    }

    /** Checks if the block position outside the original tree */
    private boolean outsideTrunk(BlockPos pos) {
      return (pos.getX() < minX || pos.getX() > maxX || pos.getZ() < minZ || pos.getZ() > maxZ);
    }

    /** Checks if the block position is a branch position, meaning outside the original tree */
    private boolean isBranch(BlockPos pos) {
      if (!outsideTrunk(pos)) {
        return false;
      }
      // find the distance to the nearest corner
      int deltaX = Math.min(Math.abs(pos.getX() - minX), Math.abs(pos.getX() - maxX));
      int deltaZ = Math.min(Math.abs(pos.getZ() - minZ), Math.abs(pos.getZ() - maxZ));
      if ((deltaX + deltaZ) > MAX_BRANCH_DISTANCE || branchVisited.contains(pos)) {
        return false;
      }
      branchVisited.add(pos.immutable());
      return isValidBlock(pos);
    }

    /** Adds a branch to the queue at the current mutable position */
    private void addBranch(Direction direction) {
      upcomingPositions.add(new TreePos(mutable, direction));
    }

    /** Tries to find a branch at the current mutable position */
    private void tryBranch(Direction direction) {
      // block must not have log both above and below it to count
      if (isBranch(mutable)) {
        // copies position, so safe to change after
        TreePos branchPos = new TreePos(mutable, direction);
        // must have a non-solid block below, and must be a corner or be 1-2 blocks tall (dark oak support/jungle sapling thick branches)
        if (!world.getBlockState(mutable.move(0, -1, 0)).canOcclude()) {
          upcomingPositions.add(branchPos);
        }
      }
    }

    @Override
    protected BlockPos computeNext() {
      while (!upcomingPositions.isEmpty()) {
        // if the position matches the block, add it. Some positions are already added
        TreePos treePos = upcomingPositions.remove();

        // trunk logic
        if (treePos.direction == Direction.UP) {
          boolean isTreeUp = treePos.isChecked || isValidBlock(treePos.pos);

          // find branches in all 4 directions if going up, assuming we are in the
          for (Direction direction : Plane.HORIZONTAL) {
            // if the position is a branch, meaning its a log with no log above it, queue it
            mutable.set(treePos.pos).move(direction);
            // if we did not find a log at the current position, treat the position as our new tree, for acacia
            tryBranch(!isTreeUp ? Direction.UP : direction);
          }

          if (isTreeUp) {
            // corner case, only care if we have a tree at current position
            boolean isMinX = treePos.pos.getX() == minX;
            boolean isMaxX = treePos.pos.getX() == maxX;
            boolean isMinZ = treePos.pos.getZ() == minZ;
            boolean isMaxZ = treePos.pos.getZ() == maxZ;
            // if either min or max on both axis, but not both (1x1), we are a corner, do corner case
            if (isMinX) {
              if (isMinZ) {
                mutable.set(treePos.pos).move(-1, 0, -1);
                tryBranch(Direction.WEST);
              }
              if (isMaxZ) {
                mutable.set(treePos.pos).move(-1, 0, 1);
                tryBranch(Direction.WEST);
              }
            }
            if (isMaxX) {
              if (isMinZ) {
                mutable.set(treePos.pos).move(1, 0, -1);
                tryBranch(Direction.EAST);
              }
              if (isMaxZ) {
                mutable.set(treePos.pos).move(1, 0, 1);
                tryBranch(Direction.EAST);
              }
            }

            // finally, return this position
            // insert the updated position into the queue and return the current position
            mutable.set(treePos.pos);
            upcomingPositions.add(treePos.move());
            // acacia can continue outside the original trunk, so start marking it visited to prevent redundancy
            if (outsideTrunk(treePos.pos)) {
              branchVisited.add(treePos.pos);
            }
            return mutable;
          }
        } else {
          // branch logic, should always be checked ahead of time (question is which further branches can we find)
          // continue in same direction
          mutable.set(treePos.pos).move(0, 1, 0);
          if (isBranch(mutable)) {
            addBranch(treePos.direction);
            // just direction, no up
          } else if (isBranch(mutable.move(treePos.direction).move(0, -1, 0))) {
            addBranch(treePos.direction);
            // direction and up
          } else if (isBranch(mutable.move(0, 1, 0))) {
            addBranch(treePos.direction);
          }
          // try each side, we check pos, above, then continuing the side
          Direction rotated = treePos.direction.getClockWise();
          mutable.set(treePos.pos).move(rotated);
          if (isBranch(mutable)) {
            addBranch(rotated);
          } else if (isBranch(mutable.move(0, 1, 0))) {
            addBranch(rotated);
          } else if (isBranch(mutable.move(treePos.direction).move(0, -1, 0))) {
            addBranch(rotated);
          } else if (isBranch(mutable.move(0, 1, 0))) {
            addBranch(rotated);
          }
          rotated = rotated.getOpposite();
          mutable.set(treePos.pos).move(rotated);
          if (isBranch(mutable)) {
            addBranch(rotated);
          } else if (isBranch(mutable.move(0, 1, 0))) {
            addBranch(rotated);
          } else if (isBranch(mutable.move(treePos.direction).move(0, -1, 0))) {
            addBranch(rotated);
          } else if (isBranch(mutable.move(0, 1, 0))) {
            addBranch(rotated);
          }
          return treePos.pos;
        }
      }
      return endOfData();
    }
  }

  /** Helper class for queue contents */
  private static class TreePos {
    private final BlockPos.MutableBlockPos pos;
    private final Direction direction;
    /** If true, this position has been validated already for a log */
    private boolean isChecked;

    TreePos(BlockPos pos, boolean isChecked) {
      // note this copies the mutable if already mutable
      this.pos = pos.mutable();
      this.direction = Direction.UP;
      this.isChecked = isChecked;
    }

    TreePos(BlockPos pos, Direction direction) {
      // note this copies the mutable if already mutable
      this.pos = pos.mutable();
      this.direction = direction;
      this.isChecked = true;
    }

    /** Moves the tree position in the given direction */
    public TreePos move() {
      pos.move(direction);
      isChecked = false;
      return this;
    }
  }
}
