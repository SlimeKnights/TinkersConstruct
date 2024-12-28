package slimeknights.tconstruct.library.modifiers.hook.mining;

import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Hook called before a tool harvests any blocks and after it finishes AOE.
 * <br>
 * Alternatives:
 * <ul>
 *   <li>{@link BlockBreakModifierHook}: Called after each individual block is broken.</li>
 * </ul>
 */
public interface BlockHarvestModifierHook {
  /**
   * Called when a tool is about to break blocks to allow setting markers on the tool for hooks with less context (such as {@link slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook}).
   * @param tool      Tool used
   * @param modifier  Modifier level
   * @param context   Harvest context
   */
  default void startHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {}

  /**
   * Called after all blocks are broken on the target block. Use to perform effects or to cleanup changes from {@link #startHarvest(IToolStackView, ModifierEntry, ToolHarvestContext)}.
   * @param tool       Tool used
   * @param modifier   Modifier level
   * @param context    Harvest context
   * @param harvested  Number of blocks harvested
   */
  default void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, int harvested) {
    finishHarvest(tool, modifier, context, harvested > 0);
  }

  /**
   * Called after all blocks are broken on the target block. Use to perform effects or to cleanup changes from {@link #startHarvest(IToolStackView, ModifierEntry, ToolHarvestContext)}.
   * @param tool        Tool used
   * @param modifier    Modifier level
   * @param context     Harvest context
   * @param didHarvest  If true, the block was actually broken.
   * @deprecated use {@link #finishHarvest(IToolStackView, ModifierEntry, ToolHarvestContext, int)}
   */
  @Deprecated
  void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, boolean didHarvest);

  /** Merger that runs all submodules */
  record AllMerger(Collection<BlockHarvestModifierHook> modules) implements BlockHarvestModifierHook {
    @Override
    public void startHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
      for (BlockHarvestModifierHook module : modules) {
        module.startHarvest(tool, modifier, context);
      }
    }

    @Override
    public void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, int harvested) {
      for (BlockHarvestModifierHook module : modules) {
        module.finishHarvest(tool, modifier, context, harvested);
      }
    }

    @Override
    public void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, boolean didHarvest) {
      for (BlockHarvestModifierHook module : modules) {
        module.finishHarvest(tool, modifier, context, didHarvest);
      }
    }
  }

  /**
   * Implementation that simply marks we are harvesting in the tools persistent data.
   * Not a problem if multiple modifiers use the default impl as it just sets the flag and clears it multiple times.
   * @see slimeknights.tconstruct.library.modifiers.modules.technical.MarkHarvestingModule
   */
  interface MarkHarvesting extends BlockHarvestModifierHook {
    /** Flag marking we are currently harvesting. Will be shared by all usages of this hook as its not a problem if its set/removed multiple times. */
    ResourceLocation HARVESTING_FLAG = TConstruct.getResource("is_harvesting");

    @Override
    default void startHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
      tool.getPersistentData().putBoolean(HARVESTING_FLAG, true);
    }

    @Override
    default void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, boolean didHarvest) {
      tool.getPersistentData().remove(HARVESTING_FLAG);
    }

    /** Checks if we are presently harvesting */
    static boolean isHarvesting(IToolStackView tool) {
      return tool.getPersistentData().getBoolean(HARVESTING_FLAG);
    }
  }
}
