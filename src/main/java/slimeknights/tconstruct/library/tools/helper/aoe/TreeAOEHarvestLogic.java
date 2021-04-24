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
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

/** Tree harvest logic that destroys a tree */
@RequiredArgsConstructor
public class TreeAOEHarvestLogic extends ToolHarvestLogic {
  private final int extraWidth;
  private final int extraDepth;
  private final int fallbackHeight;

  @Override
  public Iterable<BlockPos> getAOEBlocks(ToolStack tool, ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
    if (!canAOE(tool, stack, state, matchType)) {
      return Collections.emptyList();
    }
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
  public static Iterable<BlockPos> calculate(ToolHarvestLogic self, ToolStack tool, ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos origin, Direction sideHit, int extraWidth, int extraDepth, int fallbackHeight, AOEMatchType matchType) {
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
      return () -> new TreeIterator(world, state.getBlock(), origin, widthDir, extraWidth, depthDir, extraDepth);
    }

    // fallback to tree
    Predicate<BlockPos> posPredicate = getDefaultBlockPredicate(self, tool, stack, world, origin, matchType);
    return () -> new RectangleIterator(origin, widthDir, extraWidth, Direction.UP, fallbackHeight, false, depthDir, extraDepth, posPredicate);
  }

  /** Iterator that continues up until the block does not match */
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
                if (mutable.getX() < maxX) maxX = mutable.getX();
                if (mutable.getZ() < minZ) minZ = mutable.getZ();
                if (mutable.getZ() < maxZ) maxZ = mutable.getZ();
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

    /** Checks if the block position is a branch position, meaning outside the original tree */
    private boolean isBranch(BlockPos pos) {
      if (!outsideTrunk(pos)) {
        return false;
      }
      if (branchVisited.contains(pos)) {
        return false;
      }
      branchVisited.add(pos.toImmutable());
      return isValidBlock(pos);
    }

    /** Checks if the block position is a branch position, meaning outside the original tree */
    private boolean outsideTrunk(BlockPos pos) {
      return (pos.getX() < minX || pos.getX() > maxX || pos.getZ() < minZ || pos.getZ() > maxZ);
    }

    /** Adds a branch to the queue at the current mutable position */
    private void addBranch(Direction direction) {
      upcomingPositions.add(new TreePos(mutable, direction));
    }

    /** Tries to find a branch at the current mutable position */
    private void tryBranch(Direction direction) {
      // block must not have log both above and below it to count
      if (outsideTrunk(mutable) && isBranch(mutable)) {
        TreePos branchPos = new TreePos(mutable, direction);
        // must have no block below, and must be a corner or be 1-2 blocks tall (dark oak support/jungle sapling thick branches)
        if (!isValidBlock(mutable.move(Direction.DOWN))
            && (!isValidBlock(mutable.move(Direction.UP, 2)) || !isValidBlock(mutable.move(Direction.UP)))) {
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
          // find branches in all 4 directions if going up, assuming we are in the
          for (Direction direction : Plane.HORIZONTAL) {
            // if the position is a branch, meaning its a log with no log above it, queue it
            mutable.setPos(treePos.pos).move(direction);
            tryBranch(direction);
          }

          // corner case, only care if no branch found
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

          // if valid, return this position
          if (treePos.isChecked || isValidBlock(treePos.pos)) {
            // insert the updated position into the queue and return the current position
            mutable.setPos(treePos.pos);
            upcomingPositions.add(treePos.move());
            return mutable;
          }
        } else {
          // branch logic, should always be checked ahead of time (question is which further branches can we find)
          // first try up
          mutable.setPos(treePos.pos).move(Direction.UP);
          if (isBranch(mutable)) {
            addBranch(treePos.direction);
            // direction and up
          } else if (isBranch(mutable.move(treePos.direction))) {
            addBranch(treePos.direction);
            // just direction, no up
          } else if (isBranch(mutable.move(Direction.DOWN))) {
            addBranch(treePos.direction);
          } else {
            // try each side, we check pos, above, then continuing the side
            Direction rotated = treePos.direction.rotateY();
            mutable.setPos(treePos.pos).move(rotated);
            if (isBranch(mutable)) {
              addBranch(rotated);
            } else if (isBranch(mutable.move(Direction.UP))) {
              addBranch(rotated);
            } else if (isBranch(mutable.move(treePos.direction))) {
              addBranch(rotated);
            } else if (isBranch(mutable.move(Direction.DOWN))) {
              addBranch(rotated);
            }
            rotated = rotated.getOpposite();
            mutable.setPos(treePos.pos).move(rotated);
            if (isBranch(mutable)) {
              addBranch(rotated);
            } else if (isBranch(mutable.move(Direction.UP))) {
              addBranch(rotated);
            } else if (isBranch(mutable.move(treePos.direction))) {
              addBranch(rotated);
            } else if (isBranch(mutable.move(Direction.DOWN))) {
              addBranch(rotated);
            }
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
