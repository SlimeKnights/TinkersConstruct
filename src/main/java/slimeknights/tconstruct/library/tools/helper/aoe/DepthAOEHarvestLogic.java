package slimeknights.tconstruct.library.tools.helper.aoe;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.aoe.RectangleAOEHarvestLogic.RectangleIterator;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.Collections;
import java.util.function.Predicate;

import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic.AOEMatchType;

@RequiredArgsConstructor
public class DepthAOEHarvestLogic extends ToolHarvestLogic {
  private final int extraHeight;
  private final int extraDepth;

  @Override
  public Iterable<BlockPos> getAOEBlocks(IModifierToolStack tool, ItemStack stack, Player player, BlockState state, Level world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
    int expanded = tool.getModifierLevel(TinkerModifiers.expanded.get());
    return calculate(this, tool, stack, player, world, origin, extraHeight + expanded / 2, extraDepth + (expanded + 1) / 2 * 2, matchType);
  }

  public static Iterable<BlockPos> calculate(ToolHarvestLogic self, IModifierToolStack tool, ItemStack stack, Player player, Level world, BlockPos origin, int extraHeight, int extraDepth, AOEMatchType matchType) {
    // skip if no work
    if (extraDepth == 0 && extraHeight == 0) {
      return Collections.emptyList();
    }

    // depth is always direction into the block
    Direction playerLook = player.getDirection();
    float pitch = player.getViewXRot(1.0f);
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
