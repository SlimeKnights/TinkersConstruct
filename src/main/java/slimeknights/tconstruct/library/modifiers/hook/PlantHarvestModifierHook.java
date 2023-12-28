package slimeknights.tconstruct.library.modifiers.hook;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;
import java.util.function.Function;

/**
 * Modifier module to detect when the harvest modifier harvested a block.
 * TODO 1.19: move to {@code slimeknights.tconstruct.library.modifiers.hook.special}
 */
public interface PlantHarvestModifierHook {
  /** Default behavior for modifiers not using this hook */
  PlantHarvestModifierHook EMPTY = (tool, modifier, context, world, state, pos) -> {};

  /** Merger that calls all hooks */
  Function<Collection<PlantHarvestModifierHook>,PlantHarvestModifierHook> ALL_MERGER = AllMerger::new;

  /**
   * Called after a block is successfully harvested
   * @param tool    Tool used in harvesting
   * @param modifier Entry calling this hook
   * @param context Item use context, corresponds to the original targeted position
   * @param world   Server world instance
   * @param state   State before it was harvested
   * @param pos     Position that was harvested, may be different from the context
   */
  void afterHarvest(IToolStackView tool, ModifierEntry modifier, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos);

  /** Merger that runs all hooks */
  record AllMerger(Collection<PlantHarvestModifierHook> modules) implements PlantHarvestModifierHook {
    @Override
    public void afterHarvest(IToolStackView tool, ModifierEntry modifier, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos) {
      for (PlantHarvestModifierHook module : modules) {
        module.afterHarvest(tool, modifier, context, world, state, pos);
      }
    }
  }
}
