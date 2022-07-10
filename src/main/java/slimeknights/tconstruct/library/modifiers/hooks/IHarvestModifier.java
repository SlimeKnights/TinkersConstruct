package slimeknights.tconstruct.library.modifiers.hooks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.PlantHarvestModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Modifier module to detect when the harvest modifier harvested a block. Use with {@link slimeknights.tconstruct.library.modifiers.Modifier#getModule(Class)}
 * @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.PlantHarvestModifierHook}
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public interface IHarvestModifier extends PlantHarvestModifierHook {
  /**
   * Called after a block is successfully harvested
   * @param tool    Tool used in harvesting
   * @param level   Tool level
   * @param context Item use context, corresponds to the original targeted position
   * @param world   Server world instance
   * @param state   State before it was harvested
   * @param pos     Position that was harvested, may be different from the context
   * @deprecated use {@link #afterHarvest(IToolStackView, ModifierEntry, UseOnContext, ServerLevel, BlockState, BlockPos)}
   */
  @Deprecated
  void afterHarvest(IToolStackView tool, int level, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos);


  /** New interface fallback to make transition easier */

  @Override
  default void afterHarvest(IToolStackView tool, ModifierEntry modifier, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos) {
    afterHarvest(tool, modifier.getLevel(), context, world, state, pos);
  }
}
