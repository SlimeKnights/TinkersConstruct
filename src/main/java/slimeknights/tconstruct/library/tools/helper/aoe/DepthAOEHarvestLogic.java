package slimeknights.tconstruct.library.tools.helper.aoe;

import lombok.RequiredArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.RectangleAOEHarvestLogic.RectangleIterator;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.Collections;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class DepthAOEHarvestLogic extends ToolHarvestLogic {
  private final int extraHeight;
  private final int extraDepth;

  @Override
  public Iterable<BlockPos> getAOEBlocks(ToolStack tool, ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
    if (!canAOE(tool, stack, state, matchType)) {
      return Collections.emptyList();
    }
    int expanded = tool.getModifierLevel(TinkerModifiers.expanded.get());
    return calculate(this, tool, stack, player, world, origin, extraHeight + expanded / 2, extraDepth + (expanded + 1) / 2 * 2, matchType);
  }

  public static Iterable<BlockPos> calculate(ToolHarvestLogic self, ToolStack tool, ItemStack stack, PlayerEntity player, World world, BlockPos origin, int extraHeight, int extraDepth, AOEMatchType matchType) {
    // skip if no work
    if (extraDepth == 0 && extraHeight == 0) {
      return Collections.emptyList();
    }

    // depth is always direction into the block
    Direction playerLook = player.getHorizontalFacing();
    float pitch = player.getPitch(1.0f);
    Direction depthDir, heightDir;
    if (pitch < -60) {
      depthDir = Direction.UP;
      heightDir = playerLook;
    } else if (pitch > 60) {
      depthDir = Direction.DOWN;
      heightDir = playerLook;
    } else {
      heightDir = Direction.UP;
      depthDir = playerLook;
    }

    // finally, return the iterator
    Predicate<BlockPos> posPredicate = getDefaultBlockPredicate(self, tool, stack, world, origin, matchType);
    return () -> new RectangleIterator(origin, Direction.UP, 0, heightDir, extraHeight, depthDir, extraDepth, posPredicate);
  }
}
