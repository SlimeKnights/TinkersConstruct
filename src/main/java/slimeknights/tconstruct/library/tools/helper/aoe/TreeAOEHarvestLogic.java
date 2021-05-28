package slimeknights.tconstruct.library.tools.helper.aoe;

import com.google.common.collect.AbstractIterator;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.RectangleAOEHarvestLogic.RectangleIterator;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

/** Tree harvest logic that destroys a tree */
@RequiredArgsConstructor
public class TreeAOEHarvestLogic extends ToolHarvestLogic {
  /** Max distance between the branch and the trunk */
  private static final int MAX_BRANCK_DISTANCE = 10;

  private final int extraWidth;
  private final int extraDepth;
  private final int fallbackHeight;

  @Override
  public Iterable<BlockPos> getAOEBlocks(IModifierToolStack tool, ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
    int expanded = tool.getModifierLevel(TinkerModifiers.expanded.get());
    return calculate(this, tool, stack, player, state, world, origin, sideHit,
                     extraWidth + (expanded + 1) / 2, extraDepth + expanded / 2, fallbackHeight, matchType);
  }

  /**
   * Gets an iterator, either for a tree, or falling back to a cube
   * @param self            AOE harvest instance
   * @param tool            Tool used to mine the block
   * @param stack           Stack used to mine the block
   * @param player          Player instance
   * @param state           State being mined
   * @param world           World instance
   * @param origin          AOE origin
   * @param sideHit         Block side hit
   * @param extraWidth      Mining width
   * @param extraDepth      Mining depth
   * @param fallbackHeight  Bonus height to use when not a tree
   * @param matchType       Match type to use when not a tree
   * @return  Correct iterator for the targeted block
   */
  public static Iterable<BlockPos> calculate(ToolHarvestLogic self, IModifierToolStack tool, ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos origin, Direction sideHit, int extraWidth, int extraDepth, int fallbackHeight, AOEMatchType matchType) {
    Direction depthDir;
    Direction widthDir;
    // if we have expanders, add them in
    if (extraDepth > 0 || extraWidth > 0) {
      // if hit the top or bottom, use facing direction
      if (sideHit.getAxis().isVertical()) {
        depthDir = player.getHorizontalFacing();
      } else {
        depthDir = sideHit.getOpposite();
      }
      widthDir = depthDir.rotateY();
    } else {
      depthDir = Direction.UP;
      widthDir = Direction.UP;
    }

    // if logs, calculate a tree
    if (state.getBlock().isIn(TinkerTags.Blocks.TREE_LOGS)) {
      // TODO: would be nice to allow the stipped logs here as well as the logs
      return () -> new TreeIterator(world, state.getBlock(), origin, widthDir, extraWidth, depthDir, extraDepth);
    }

    // fallback to tree
    Predicate<BlockPos> posPredicate = getDefaultBlockPredicate(self, tool, stack, world, origin, matchType);
    return () -> new RectangleIterator(origin, widthDir, extraWidth, Direction.UP, fallbackHeight, false, depthDir, extraDepth, posPredicate);
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
    private final BlockPos.Mutable mutable = new BlockPos.Mutable();
    /** Branches that have been visited already */
    private final Set<BlockPos> branchVisited = new HashSet<>();

    private final World world;
    private final Block filter;
    /** Bounds for branch detection */
    private final int minX, maxX, minZ, maxZ;
    public TreeIterator(World world, Block filter, BlockPos origin, Direction widthDir, int extraWidth, Direction depthDir, int extraDepth) {
      this.world = world;
      this.filter = filter;

      // first, enqueue the origin
      upcomingPositions.add(new TreePos(origin.up(), false));

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
              mutable.setPos(origin).move(depthDir, d).move(widthDir, w);
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
      if ((deltaX + deltaZ) > MAX_BRANCK_DISTANCE || branchVisited.contains(pos)) {
        return false;
      }
      branchVisited.add(pos.toImmutable());
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
        if (!world.getBlockState(mutable.move(0, -1, 0)).isSolid()) {
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
            mutable.setPos(treePos.pos).move(direction);
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
                mutable.setPos(treePos.pos).move(-1, 0, -1);
                tryBranch(Direction.WEST);
              }
              if (isMaxZ) {
                mutable.setPos(treePos.pos).move(-1, 0, 1);
                tryBranch(Direction.WEST);
              }
            }
            if (isMaxX) {
              if (isMinZ) {
                mutable.setPos(treePos.pos).move(1, 0, -1);
                tryBranch(Direction.EAST);
              }
              if (isMaxZ) {
                mutable.setPos(treePos.pos).move(1, 0, 1);
                tryBranch(Direction.EAST);
              }
            }

            // finally, return this position
            // insert the updated position into the queue and return the current position
            mutable.setPos(treePos.pos);
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
          mutable.setPos(treePos.pos).move(0, 1, 0);
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
          Direction rotated = treePos.direction.rotateY();
          mutable.setPos(treePos.pos).move(rotated);
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
          mutable.setPos(treePos.pos).move(rotated);
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
    private final BlockPos.Mutable pos;
    private final Direction direction;
    /** If true, this position has been validated already for a log */
    private boolean isChecked;

    TreePos(BlockPos pos, boolean isChecked) {
      // note this copies the mutable if already mutable
      this.pos = pos.toMutable();
      this.direction = Direction.UP;
      this.isChecked = isChecked;
    }

    TreePos(BlockPos pos, Direction direction) {
      // note this copies the mutable if already mutable
      this.pos = pos.toMutable();
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
