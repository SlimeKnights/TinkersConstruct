package slimeknights.tconstruct.library.tools.definition.module.aoe;

import com.google.common.collect.AbstractIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Harvest logic that breaks a block plus neighbors of the same type
 * @param maxDistance  Maximum distance from the starting block to vein, min of 0
 */
public record VeiningAOEIterator(int maxDistance) implements AreaOfEffectIterator.Loadable {
  public static final RecordLoadable<VeiningAOEIterator> LOADER = RecordLoadable.create(IntLoadable.FROM_ZERO.defaultField("max_distance", 0, true, VeiningAOEIterator::maxDistance), VeiningAOEIterator::new);

  @Override
  public RecordLoadable<VeiningAOEIterator> getLoader() {
    return LOADER;
  }

  @Override
  public Iterable<BlockPos> getBlocks(IToolStackView tool, UseOnContext context, BlockState state, AOEMatchType matchType) {
    int expanded = tool.getModifierLevel(TinkerModifiers.expanded.getId());
    return calculate(state, context.getLevel(), context.getClickedPos(), maxDistance + expanded);
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
  public static Iterable<BlockPos> calculate(BlockState state, Level world, BlockPos origin, int maxDistance) {
    return () -> new VeiningIterator(world, origin, state.getBlock(), maxDistance);
  }

  /** Iterator that navigates block and other similar blocks */
  private static class VeiningIterator extends AbstractIterator<BlockPos> {
    private final Set<BlockPos> visited = new HashSet<>();
    private final Queue<DistancePos> queue = new ArrayDeque<>();

    private final Level world;
    private final Block target;
    private final int maxDistance;
    private VeiningIterator(Level world, BlockPos origin, Block target, int maxDistance) {
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
        BlockPos offset = pos.relative(direction);
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
        BlockPos pos = distancePos.pos;
        // must be a valid block
        if (world.getBlockState(pos).is(target)) {
          // if not at max distance yet, add blocks on all sides
          int distance = distancePos.distance;
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
  private record DistancePos(BlockPos pos, int distance) {}
}
