package slimeknights.tconstruct.library.modifiers.hooks;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
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
  void afterHarvest(IModifierToolStack tool, int level, ItemUseContext context, ServerWorld world, BlockState state, BlockPos pos);
}
