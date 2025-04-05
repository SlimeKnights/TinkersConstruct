package slimeknights.tconstruct.library.tools.definition.module.aoe;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.tools.definition.module.aoe.IBoxExpansion.ExpansionDirections;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * AOE harvest logic that mines blocks in a rectangle
 * @param base        Base size of the AOE
 * @param expansions  Values to boost the size by for each expansion
 * @param direction   Direction for expanding
 */
public record BoxAOEIterator(BoxSize base, List<BoxSize> expansions, IBoxExpansion direction) implements AreaOfEffectIterator.Loadable {
  public static final RecordLoadable<BoxAOEIterator> LOADER = RecordLoadable.create(
    BoxSize.LOADER.defaultField("bonus", BoxSize.ZERO, BoxAOEIterator::base),
    BoxSize.LOADER.list(0).defaultField("expansions", List.of(), BoxAOEIterator::expansions),
    IBoxExpansion.REGISTRY.requiredField("expansion_direction", BoxAOEIterator::direction),
    BoxAOEIterator::new);

  /** Creates a builder for this iterator */
  public static BoxAOEIterator.Builder builder(int width, int height, int depth) {
    return new Builder(new BoxSize(width, height, depth));
  }

  /** @apiNote Internal constructor, use {@link #builder(int, int, int)} */
  @Internal
  public BoxAOEIterator {}

  @Override
  public RecordLoadable<BoxAOEIterator> getLoader() {
    return LOADER;
  }

  /** Gets the size for a given level of expanded */
  private BoxSize sizeFor(int level) {
    int size = expansions.size();
    if (level == 0 || size == 0) {
      return base;
    }
    int width = base.width;
    int height = base.height;
    int depth = base.depth;
    // if we have the number of expansions or more, add in all expansions as many times as needed
    if (level >= size) {
      int cycles = level / size;
      for (BoxSize expansion : expansions) {
        width  += expansion.width  * cycles;
        height += expansion.height * cycles;
        depth  += expansion.depth  * cycles;
      }
    }
    // partial iteration through the list for the remaining expansions
    int remainder = level % size;
    for (int i = 0; i < remainder; i++) {
      BoxSize expansion = expansions.get(i);
      width  += expansion.width;
      height += expansion.height;
      depth  += expansion.depth;
    }
    return new BoxSize(width, height, depth);
  }

  @Override
  public Iterable<BlockPos> getBlocks(IToolStackView tool, UseOnContext context, BlockState state, AOEMatchType matchType) {
    // expanded gives an extra width every odd level, and an extra height every even level
    int expanded = tool.getModifierLevel(TinkerModifiers.expanded.getId());
    return calculate(tool, context, sizeFor(expanded), direction, matchType);
  }

  /**
   *
   * @param tool          Tool used for harvest
   * @param context       Interaction context
   * @param extraSize     Extra size to iterate
   * @param matchType     Type of harvest being performed
   * @return  List of block positions
   */
  public static Iterable<BlockPos> calculate(IToolStackView tool, UseOnContext context, BoxSize extraSize, IBoxExpansion expansionDirection, AOEMatchType matchType) {
    // skip if no work
    if (extraSize.isZero()) {
      return Collections.emptyList();
    }
    BlockHitResult hit = context.getHitResult();
    ExpansionDirections expansion = expansionDirection.getDirections(context.getPlayer(), hit.getDirection());
    Predicate<BlockPos> posPredicate = AreaOfEffectIterator.defaultBlockPredicate(tool, context, matchType);
    return () -> new RectangleIterator(hit.getBlockPos(), expansion.width(), extraSize.width, expansion.height(), extraSize.height, expansion.traverseDown(), expansion.depth(), extraSize.depth, posPredicate);
  }

  /** Iterator used for getting the blocks */
  public static class RectangleIterator extends AbstractIterator<BlockPos> {
    /** Primary direction of iteration */
    private final Direction widthDir;
    /** Secondary direction of iteration, mostly interchangeable with primary */
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

  /** Record encoding how AOE expands with each level */
  private record BoxSize(int width, int height, int depth) {
    public static final BoxSize ZERO = new BoxSize(0, 0, 0);

    public static final RecordLoadable<BoxSize> LOADER = RecordLoadable.create(
      IntLoadable.FROM_ZERO.defaultField("width", 0, BoxSize::width),
      IntLoadable.FROM_ZERO.defaultField("height", 0, BoxSize::height),
      IntLoadable.FROM_ZERO.defaultField("depth", 0, BoxSize::depth),
      BoxSize::new);

    /** If true, the box is 0 in all dimensions */
    public boolean isZero() {
      return width == 0 && height == 0 && depth == 0;
    }
  }

  /** Builder to create a rectangle AOE iterator */
  @RequiredArgsConstructor
  public static class Builder {
    private final BoxSize base;
    /** Direction to expand the AOE */
    @Nonnull @Setter @Accessors(fluent = true)
    private IBoxExpansion direction = IBoxExpansion.SIDE_HIT;
    private final ImmutableList.Builder<BoxSize> expansions = ImmutableList.builder();

    /** Adds an expansion to the AOE logic */
    public Builder addExpansion(int width, int height, int depth) {
      expansions.add(new BoxSize(width, height, depth));
      return this;
    }

    /** Adds an expansion to the AOE logic */
    public Builder addWidth(int width) {
      return addExpansion(width, 0, 0);
    }

    /** Adds an expansion to the AOE logic */
    public Builder addHeight(int height) {
      return addExpansion(0, height, 0);
    }

    /** Adds an expansion to the AOE logic */
    public Builder addDepth(int depth) {
      return addExpansion(0, 0, depth);
    }

    /** Builds the AOE iterator */
    public BoxAOEIterator build() {
      return new BoxAOEIterator(base, expansions.build(), direction);
    }
  }
}
