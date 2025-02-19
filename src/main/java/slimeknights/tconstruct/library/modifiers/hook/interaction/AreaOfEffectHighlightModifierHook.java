package slimeknights.tconstruct.library.modifiers.hook.interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Hook for controlling which blocks are highlighted when holding a tool */
public interface AreaOfEffectHighlightModifierHook {
  /**
   * Checks if the given block should be highlighted
   * @param tool      Tool instance
   * @param modifier  Modifier performing the effect
   * @param context   Context representing the targeted block
   * @param offset    Position to check for highlight
   * @param state     State at position
   * @return  True if the block should highlight
   */
  boolean shouldHighlight(IToolStackView tool, ModifierEntry modifier, UseOnContext context, BlockPos offset, BlockState state);

  /** Merger returning true if any nested hook returns true */
  record AnyMerger(Collection<AreaOfEffectHighlightModifierHook> modules) implements AreaOfEffectHighlightModifierHook {
    @Override
    public boolean shouldHighlight(IToolStackView tool, ModifierEntry modifier, UseOnContext context, BlockPos offset, BlockState state) {
      for (AreaOfEffectHighlightModifierHook module : modules) {
        if (module.shouldHighlight(tool, modifier, context, offset, state)) {
          return true;
        }
      }
      return false;
    }
  }
}
