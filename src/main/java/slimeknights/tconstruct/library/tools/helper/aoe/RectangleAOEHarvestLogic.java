package slimeknights.tconstruct.library.tools.helper.aoe;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.Collections;
import java.util.List;

/** AOE harvest logic that mines blocks in a rectangle */
@RequiredArgsConstructor
public class RectangleAOEHarvestLogic extends ToolHarvestLogic {
  public static final RectangleAOEHarvestLogic SMALL = new RectangleAOEHarvestLogic(0, 0, 0);
  public static final RectangleAOEHarvestLogic LARGE = new RectangleAOEHarvestLogic(1, 1, 0);

  protected final int extraWidth;
  protected final int extraHeight;
  protected final int extraDepth;

  @Override
  public Iterable<BlockPos> getAOEBlocks(ToolStack tool, ItemStack stack, World world, PlayerEntity player, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
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
  public final List<BlockPos> calculate(ToolHarvestLogic self, ToolStack tool, ItemStack stack, World world, PlayerEntity player, BlockPos origin, Direction sideHit,
                                        int extraWidth, int extraHeight, int extraDepth, AOEMatchType matchType) {
    // skip if no work
    if (extraDepth == 0 && extraWidth == 0 && extraHeight == 0) {
      return Collections.emptyList();
    }


    // we know the block and we know which side of the block we're hitting. time to calculate the depth along the different axes
    int x, y, z;
    int signX = 1, signY = 1, signZ = 1;
    BlockPos center = origin;
    int depth = 0;
    int absDepth = 0;
    int signDepth = 1;
    if (extraDepth > 0) {
      int offset = -sideHit.getAxisDirection().getOffset();
      depth = offset * extraDepth;
      absDepth = Math.abs(depth);
      signDepth = depth / absDepth;
    }
    switch (sideHit.getAxis()) {
      case Y:
        // x y depends on the angle we look
        Vector3i vec = player.getHorizontalFacing().getDirectionVec();
        x = Math.abs(vec.getX() * extraHeight + vec.getZ() * extraWidth);
        y = absDepth;
        signY = signDepth;
        z = Math.abs(vec.getX() * extraWidth + vec.getZ() * extraHeight);
        center = center.add(0, depth, 0);
        break;
      case Z:
        x = extraWidth;
        y = extraHeight;
        z = absDepth;
        signZ = signDepth;
        center = center.add(0, 0, depth);
        break;
      case X:
        x = absDepth;
        signX = signDepth;
        y = extraHeight;
        z = extraWidth;
        center = center.add(depth, 0, 0);
        break;
      default:
        return Collections.emptyList();
    }

    // start building the position list
    // TODO: iterator
    ImmutableList.Builder<BlockPos> builder = ImmutableList.builder();
    Predicate<BlockPos> posPredicate = getDefaultBlockPredicate(self, tool, stack, world, origin, matchType);
    for (int xp = -x; xp <= x; xp++) {
      for (int yp = -y; yp <= y; yp++) {
        for (int zp = -z; zp <= z; zp++) {
          // build position
          BlockPos pos = center.add(signX * xp, signY * yp, signZ * zp);
          // don't add the origin block
          if (pos.equals(origin)) {
            continue;
          }
          // if valid, keep it
          if (posPredicate.test(pos)) {
            builder.add(pos);
          }
        }
      }
    }
    return builder.build();
  }
}
