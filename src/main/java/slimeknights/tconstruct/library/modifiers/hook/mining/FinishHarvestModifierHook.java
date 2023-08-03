package slimeknights.tconstruct.library.modifiers.hook.mining;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Hook called after a tool completes breaking all blocks in its AOE */
public interface FinishHarvestModifierHook {
  /**
   * Called after all blocks are broken on the target block
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link BlockBreakModifierHook}: Called after each individual block is broken.</li>
   * </ul>
   * @param tool      Tool used
   * @param modifier  Modifier level
   * @param context   Harvest context
   */
  void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context);

  /** Merger that runs all submodules */
  record AllMerger(Collection<FinishHarvestModifierHook> modules) implements FinishHarvestModifierHook {
    @Override
    public void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
      for (FinishHarvestModifierHook module : modules) {
        module.finishHarvest(tool, modifier, context);
      }
    }
  }
}
