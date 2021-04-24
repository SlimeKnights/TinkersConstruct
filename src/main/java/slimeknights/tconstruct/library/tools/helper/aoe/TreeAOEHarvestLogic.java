package slimeknights.tconstruct.library.tools.helper.aoe;

import com.google.common.collect.AbstractIterator;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.RectangleAOEHarvestLogic.RectangleIterator;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
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
      return calculateTree(state, world, origin, widthDir, extraWidth, depthDir, extraDepth);
    }

    // fallback to tree
    Predicate<BlockPos> posPredicate = getDefaultBlockPredicate(self, tool, stack, world, origin, matchType);
    return () -> new RectangleIterator(origin, widthDir, extraWidth, Direction.UP, fallbackHeight, false, depthDir, extraDepth, posPredicate);
  }

  /**
   * Creates the iterator for a tree
   * @param state        Block state targeted
   * @param world        World instance
   * @param origin       Target position
   * @param widthDir     Direction of the side
   * @param extraWidth   Bonus on the sides
   * @param depthDir     Direction of the depth
   * @param extraDepth   Bonus on the depth
   * @return  Tree iterator
   */
  public static Iterable<BlockPos> calculateTree(BlockState state, World world, BlockPos origin, Direction widthDir, int extraWidth, Direction depthDir, int extraDepth) {
    // position above origin for seed
    List<BlockPos> seed = new ArrayList<>((extraWidth * 2 + 1) * (extraDepth + 1));
    seed.add(origin.up());
    // if we have expanders, add them in
    if (extraDepth > 0 || extraWidth > 0) {
      // add extra blocks
      for (int depth = 0; depth <= extraDepth; depth++) {
        BlockPos depthCenter = origin.offset(depthDir, depth);
        for (int width = -extraWidth; width <= extraWidth; width++) {
          if (depth != 0 || width != 0) {
            seed.add(depthCenter.offset(widthDir, width));
          }
        }
      }
    }

    // final iterator
    return () -> new TreeIterator(world, state.getBlock(), seed);
  }

  /** Iterator that continues up until the block does not match */
  private static class TreeIterator extends AbstractIterator<BlockPos> {
    private final Queue<BlockPos> upcomingPositions = new ArrayDeque<>();
    private final World world;
    private final Block filter;

    private TreeIterator(World world, Block filter, List<BlockPos> seed) {
      this.world = world;
      this.filter = filter;
      this.upcomingPositions.addAll(seed);
    }

    @Override
    protected BlockPos computeNext() {
      while (!upcomingPositions.isEmpty()) {
        BlockPos pos = upcomingPositions.remove();
        if (world.getBlockState(pos).getBlock() == filter) {
          upcomingPositions.add(pos.up());
          return pos;
        }
      }
      return endOfData();
    }
  }
}
