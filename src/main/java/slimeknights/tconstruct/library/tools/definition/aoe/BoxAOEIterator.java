package slimeknights.tconstruct.library.tools.definition.aoe;

import com.google.common.collect.AbstractIterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.tools.definition.aoe.IBoxExpansion.ExpansionDirections;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.JsonUtils;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/** AOE harvest logic that mines blocks in a rectangle */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class BoxAOEIterator implements IAreaOfEffectIterator {
  public static final Loader LOADER = new Loader();

  /** Base size of the AOE */
  private final BoxSize base;
  /** Values to boost the size by for each expansion */
  private final BoxSize[] expansions;
  /** Direction for expanding */
  private final IBoxExpansion direction;

  /** Creates a builder for this iterator */
  public static BoxAOEIterator.Builder builder(int width, int height, int depth) {
    return new Builder(new BoxSize(width, height, depth));
  }

  @Override
  public IGenericLoader<? extends IAreaOfEffectIterator> getLoader() {
    return LOADER;
  }

  /** Gets the size for a given level of expanded */
  private BoxSize sizeFor(int level) {
    if (level == 0 || expansions.length == 0) {
      return base;
    }
    int width = base.width;
    int height = base.height;
    int depth = base.depth;
    // if we have the number of expansions or more, add in all expansions as many times as needed
    if (level >= expansions.length) {
      int cycles = level / expansions.length;
      for (BoxSize expansion : expansions) {
        width  += expansion.width  * cycles;
        height += expansion.height * cycles;
        depth  += expansion.depth  * cycles;
      }
    }
    // partial iteration through the list for the remaining expansions
    int remainder = level % expansions.length;
    for (int i = 0; i < remainder; i++) {
      BoxSize expansion = expansions[i];
      width  += expansion.width;
      height += expansion.height;
      depth  += expansion.depth;
    }
    return new BoxSize(width, height, depth);
  }

  @Override
  public Iterable<BlockPos> getBlocks(IToolStackView tool, ItemStack stack, Player player, BlockState state, Level world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
    // expanded gives an extra width every odd level, and an extra height every even level
    int expanded = tool.getModifierLevel(TinkerModifiers.expanded.getId());
    return calculate(tool, stack, world, player, origin, sideHit, sizeFor(expanded), direction, matchType);
  }

  /**
   *
   * @param tool          Tool used for harvest
   * @param stack         Item stack used for harvest (for vanilla hooks)
   * @param world         World containing the block
   * @param player        Player harvesting
   * @param origin        Center of harvest
   * @param sideHit       Block side hit
   * @param extraSize     Extra size to iterate
   * @param matchType     Type of harvest being performed
   * @return  List of block positions
   */
  public static Iterable<BlockPos> calculate(IToolStackView tool, ItemStack stack, Level world, Player player, BlockPos origin, Direction sideHit, BoxSize extraSize, IBoxExpansion expansionDirection, AOEMatchType matchType) {
    // skip if no work
    if (extraSize.isZero()) {
      return Collections.emptyList();
    }
    ExpansionDirections expansion = expansionDirection.getDirections(player, sideHit);
    Predicate<BlockPos> posPredicate = IAreaOfEffectIterator.defaultBlockPredicate(tool, stack, world, origin, matchType);
    return () -> new RectangleIterator(origin, expansion.width(), extraSize.width, expansion.height(), extraSize.height, expansion.traverseDown(), expansion.depth(), extraSize.depth, posPredicate);
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
    /** If true, the box is 0 in all dimensions */
    public boolean isZero() {
      return width == 0 && height == 0 && depth == 0;
    }

    /** Serializes this record to JSON */
    public JsonObject toJson() {
      JsonObject object = new JsonObject();
      if (width > 0)  object.addProperty("width", width);
      if (height > 0) object.addProperty("height", height);
      if (depth > 0)  object.addProperty("depth", depth);
      return object;
    }

    /** Writes this record to the network */
    public void toNetwork(FriendlyByteBuf buf) {
      buf.writeVarInt(width);
      buf.writeVarInt(height);
      buf.writeVarInt(depth);
    }

    /** Parses the box from json */
    public static BoxSize fromJson(JsonObject json) {
      return new BoxSize(
        JsonUtils.getIntMin(json, "width", 0),
        JsonUtils.getIntMin(json, "height", 0),
        JsonUtils.getIntMin(json, "depth", 0)
      );
    }

    /** Parses the box from the network */
    public static BoxSize fromNetwork(FriendlyByteBuf buffer) {
      return new BoxSize(buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt());
    }
  }

  /** Builder to create a rectangle AOE iterator */
  @RequiredArgsConstructor
  public static class Builder {
    private final BoxSize base;
    /** Direction to expand the AOE */
    @Nonnull @Setter @Accessors(fluent = true)
    private IBoxExpansion direction = IBoxExpansion.SIDE_HIT;
    private final List<BoxSize> expansions = new ArrayList<>();

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
      return new BoxAOEIterator(base, expansions.toArray(new BoxSize[0]), direction);
    }
  }

  /** Loads the configuration from JSON */
  private static class Loader implements IGenericLoader<BoxAOEIterator> {
    @Override
    public BoxAOEIterator deserialize(JsonObject json) {
      BoxSize base = BoxSize.fromJson(GsonHelper.getAsJsonObject(json, "bonus"));
      BoxSize[] expansions;
      if (json.has("expansions")) {
        expansions = JsonHelper.parseList(json, "expansions", BoxSize::fromJson).toArray(new BoxSize[0]);
      } else {
        expansions = new BoxSize[0];
      }
      IBoxExpansion direction = IBoxExpansion.REGISTRY.deserialize(json, "expansion_direction");
      return new BoxAOEIterator(base, expansions, direction);
    }

    @Override
    public BoxAOEIterator fromNetwork(FriendlyByteBuf buffer) {
      BoxSize base = BoxSize.fromNetwork(buffer);
      int count = buffer.readVarInt();
      BoxSize[] expansions = new BoxSize[count];
      for (int i = 0; i < count; i++) {
        expansions[i] = BoxSize.fromNetwork(buffer);
      }
      IBoxExpansion direction = IBoxExpansion.REGISTRY.fromNetwork(buffer);
      return new BoxAOEIterator(base, expansions, direction);
    }

    @Override
    public void serialize(BoxAOEIterator object, JsonObject json) {
      json.add("bonus", object.base.toJson());
      if (object.expansions.length > 0) {
        JsonArray expansions = new JsonArray();
        for (BoxSize box : object.expansions) {
          expansions.add(box.toJson());
        }
        json.add("expansions", expansions);
        json.addProperty("expansion_direction", IBoxExpansion.REGISTRY.getKey(object.direction).toString());
      }
    }

    @Override
    public void toNetwork(BoxAOEIterator object, FriendlyByteBuf buffer) {
      object.base.toNetwork(buffer);
      buffer.writeVarInt(object.expansions.length);
      for (BoxSize box : object.expansions) {
        box.toNetwork(buffer);
      }
      IBoxExpansion.REGISTRY.toNetwork(object.direction, buffer);
    }
  }
}
