package slimeknights.tconstruct.library.modifiers.hook.mining;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Hook called after a block is broken by a modifier. Called for every block broken by an AOE tool. */
public interface BlockBreakModifierHook {
  /**
   * Called after a block is broken to apply special effects
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link RemoveBlockModifierHook}: Called before the block is set to air.</li>
   *   <li>{@link BlockHarvestModifierHook}: Called before and after all blocks are broken instead of per block.</li>
   * </ul>
   * @param tool      Tool used
   * @param modifier  Modifier level
   * @param context   Harvest context
   */
  void afterBlockBreak(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context);

  /** Merger that runs all nested hooks */
  record AllMerger(Collection<BlockBreakModifierHook> modules) implements BlockBreakModifierHook {
    @Override
    public void afterBlockBreak(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
      for (BlockBreakModifierHook module : modules) {
        module.afterBlockBreak(tool, modifier, context);
      }
    }
  }
}
