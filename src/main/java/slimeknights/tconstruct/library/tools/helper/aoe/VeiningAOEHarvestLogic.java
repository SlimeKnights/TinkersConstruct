package slimeknights.tconstruct.library.tools.helper.aoe;

import com.google.common.collect.AbstractIterator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Harvest logic that breaks a block plus neighbors of the same type
 */
@RequiredArgsConstructor
public class VeiningAOEHarvestLogic extends ToolHarvestLogic {
  public static final VeiningAOEHarvestLogic SMALL = new VeiningAOEHarvestLogic(0);
  private final int maxDistance;

  @Override
  public Iterable<BlockPos> getAOEBlocks(IModifierToolStack tool, ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
    if (!canAOE(tool, stack, state, matchType)) {
      return Collections.emptyList();
    }
    int expanded = tool.getModifierLevel(TinkerModifiers.expanded.get());
    return calculate(state, world, origin, maxDistance + expanded);
  }

  /**
   * Calculates the blocks for veining
   *
   * @param state        State being mined
   * @param world        World instance
   * @param origin       Position origin
   * @param maxDistance  Max distance to vein
   * @return  Iterator for veining
   */
  public static Iterable<BlockPos> calculate(BlockState state, World world, BlockPos origin, int maxDistance) {
    return () -> new VeiningIterator(world, origin, state.getBlock(), maxDistance);
  }

  /** Iterator that navigates block and other similar blocks */
  private static class VeiningIterator extends AbstractIterator<BlockPos> {
    private final Set<BlockPos> visited = new HashSet<>();
    private final Queue<DistancePos> queue = new ArrayDeque<>();

    private final World world;
    private final Block target;
    private final int maxDistance;
    private VeiningIterator(World world, BlockPos origin, Block target, int maxDistance) {
      this.world = world;
      this.target = target;
      this.maxDistance = maxDistance;
      // make use of origin
      visited.add(origin);
      if (maxDistance > 0) {
        // start off the queue with the position in each direction
        enqueueNeighbors(origin, 1);
      }
    }

    /**
     * Enqueues all neighbors of this position
     * @param pos       Position
     * @param distance  Distance for neighbors
     */
    private void enqueueNeighbors(BlockPos pos, int distance) {
      for (Direction direction : Direction.values()) {
        BlockPos offset = pos.offset(direction);
        if (!visited.contains(offset)) {
          visited.add(offset); // mark position visited to prevent adding again before we get to it
          queue.add(new DistancePos(offset, distance));
        }
      }
    }

    @Override
    protected BlockPos computeNext() {
      while (!queue.isEmpty()) {
        // grab the next queued position to check
        DistancePos distancePos = queue.remove();
        BlockPos pos = distancePos.getPos();
        // must be a valid block
        if (world.getBlockState(pos).isIn(target)) {
          // if not at max distance yet, add blocks on all sides
          int distance = distancePos.getDistance();
          if (distance < maxDistance) {
            enqueueNeighbors(pos, distance + 1);
          }
          // finally, return the position
          return pos;
        }
      }
      // queue ran out of data
      return endOfData();
    }
  }

  /** Helper data class */
  @Data
  private static class DistancePos {
    private final BlockPos pos;
    private final int distance;
  }
}
