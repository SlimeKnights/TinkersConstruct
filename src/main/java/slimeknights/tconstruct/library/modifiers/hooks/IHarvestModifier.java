package slimeknights.tconstruct.library.modifiers.hooks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

/**
 * Modifier module to detect when the harvest modifier harvested a block. Use with {@link slimeknights.tconstruct.library.modifiers.Modifier#getModule(Class)}
 */
public interface IHarvestModifier {

  /**
   * Called after a block is successfully harvested
   * @param tool    Tool used in harvesting
   * @param level   Tool level
   * @param context Item use context, cooresponds to the original targeted position
   * @param world   Server world instance
   * @param state   State before it was harvested
   * @param pos     Position that was harvested, may be different from the context
   */
  void afterHarvest(IModifierToolStack tool, int level, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos);
}
