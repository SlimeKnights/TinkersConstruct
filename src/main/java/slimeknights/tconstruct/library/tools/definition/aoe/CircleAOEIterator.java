package slimeknights.tconstruct.library.tools.definition.aoe;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.tools.definition.aoe.BoxAOEIterator.RectangleIterator;
import slimeknights.tconstruct.library.tools.definition.aoe.IBoxExpansion.ExpansionDirections;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.JsonUtils;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.Collections;
import java.util.function.Predicate;

/** AOE harvest logic that mines blocks in a circle */
@RequiredArgsConstructor
public class CircleAOEIterator implements IAreaOfEffectIterator {
  public static final Loader LOADER = new Loader();

  /** Diameter of the circle, starting from 1 */
  @Getter @VisibleForTesting
  protected final int diameter;
  /** If true, calculates AOE blocks in 3D instead of 2D */
  @Getter @VisibleForTesting
  protected final boolean is3D;

  @Override
  public IGenericLoader<? extends IAreaOfEffectIterator> getLoader() {
    return LOADER;
  }

  @Override
  public Iterable<BlockPos> getBlocks(IToolStackView tool, ItemStack stack, Player player, BlockState state, Level world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
    // expanded gives an extra width every odd level, and an extra height every even level
    int expanded = tool.getModifierLevel(TinkerModifiers.expanded.getId());
    return calculate(tool, stack, world, player, origin, sideHit, diameter + expanded, is3D, matchType);
  }

  /**
   *
   * @param tool       Tool used for harvest
   * @param stack      Item stack used for harvest (for vanilla hooks)
   * @param world      World containing the block
   * @param player     Player harvesting
   * @param origin     Center of harvest
   * @param sideHit    Block side hit
   * @param diameter   Circle diameter
   * @param matchType  Type of harvest being performed
   * @return  List of block positions
   */
  public static Iterable<BlockPos> calculate(IToolStackView tool, ItemStack stack, Level world, Player player, BlockPos origin, Direction sideHit, int diameter, boolean is3D, AOEMatchType matchType) {
    // skip if no work
    if (diameter == 1) {
      return Collections.emptyList();
    }

    // math works out that we can leave this an integer and get the radius working still
    int radiusSq = diameter * diameter / 4;
    Predicate<BlockPos> posPredicate = IAreaOfEffectIterator.defaultBlockPredicate(tool, stack, world, origin, matchType);
    ExpansionDirections directions = IBoxExpansion.SIDE_HIT.getDirections(player, sideHit);
    // max needs to be an odd number
    return () -> new CircleIterator(origin, directions.width(), directions.height(), directions.traverseDown(), directions.depth(), radiusSq, diameter / 2, is3D, posPredicate);
  }

  /** Iterator used for getting the blocks, secret is a circle is a rectangle */
  private static class CircleIterator extends RectangleIterator {
    /* Diameter of the area to mine, circular */
    private final int radiusSq;
    private CircleIterator(BlockPos origin, Direction widthDir, Direction heightDir, boolean traverseDown, Direction depthDir, int radiusSq, int extra, boolean is3D, Predicate<BlockPos> posPredicate) {
      super(origin, widthDir, extra, heightDir, extra, traverseDown, depthDir, is3D ? extra : 0, posPredicate);
      this.radiusSq = radiusSq;
    }

    /** Gets the squared distance between the origin and the mutable position */
    private int distanceSq() {
      // built in method returns a double, thats overkill
      int dx = origin.getX() - mutablePos.getX();
      int dy = origin.getY() - mutablePos.getY();
      int dz = origin.getZ() - mutablePos.getZ();
      return dx*dx + dy*dy + dz*dz;
    }

    @Override
    protected BlockPos computeNext() {
      // ensure the position did not get changed by the consumer last time
      mutablePos.set(lastX, lastY, lastZ);
      // as long as we have another position, try using it
      while (incrementPosition()) {
        // skip over the origin
        // ensure it matches the predicate
        if (!mutablePos.equals(origin) && distanceSq() <= radiusSq && posPredicate.test(mutablePos)) {
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

  private static class Loader implements IGenericLoader<CircleAOEIterator> {
    @Override
    public CircleAOEIterator deserialize(JsonObject json) {
      int diameter = JsonUtils.getIntMin(json, "diameter", 1);
      boolean is3D = GsonHelper.getAsBoolean(json, "3D", false);
      return new CircleAOEIterator(diameter, is3D);
    }

    @Override
    public CircleAOEIterator fromNetwork(FriendlyByteBuf buffer) {
      int diameter = buffer.readVarInt();
      boolean is3D = buffer.readBoolean();
      return new CircleAOEIterator(diameter, is3D);
    }

    @Override
    public void serialize(CircleAOEIterator object, JsonObject json) {
      json.addProperty("diameter", object.diameter);
      json.addProperty("3D", object.is3D);
    }

    @Override
    public void toNetwork(CircleAOEIterator object, FriendlyByteBuf buffer) {
      buffer.writeVarInt(object.diameter);
      buffer.writeBoolean(object.is3D);
    }
  }
}
