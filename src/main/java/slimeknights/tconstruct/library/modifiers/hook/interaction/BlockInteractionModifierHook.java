package slimeknights.tconstruct.library.modifiers.hook.interaction;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Hooks for standard interaction logic though blocks. See {@link GeneralInteractionModifierHook} for general interaction and {@link EntityInteractionModifierHook} for entities.
 */
public interface BlockInteractionModifierHook {
	/**
	 * Called when interacting with a block before calling the block's interaction method.
   * In general, it's better to use {@link #afterBlockUse(IToolStackView, ModifierEntry, UseOnContext, InteractionSource)} for consistency with vanilla behavior.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @param context    Usage context
   * @param source     Source of the interaction
   * @return  Return PASS or FAIL to allow vanilla handling, any other to stop vanilla and later modifiers from running.
   */
  default InteractionResult beforeBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    return InteractionResult.PASS;
  }

  /**
   * Called when interacting with a block after calling the block's interaction method.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @param context    Usage context
   * @param source     Source of the interaction
   * @return  Return PASS or FAIL to allow vanilla handling, any other to stop vanilla and later modifiers from running.
   */
  default InteractionResult afterBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    return InteractionResult.PASS;
  }

  /** Logic to merge multiple interaction hooks into one */
  record FirstMerger(Collection<BlockInteractionModifierHook> modules) implements BlockInteractionModifierHook {
    @Override
    public InteractionResult beforeBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
      InteractionResult result = InteractionResult.PASS;
      for (BlockInteractionModifierHook module : modules) {
        result = module.beforeBlockUse(tool, modifier, context, source);
        if (result.consumesAction()) {
          return result;
        }
      }
      return result;
    }

    @Override
    public InteractionResult afterBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
      InteractionResult result = InteractionResult.PASS;
      for (BlockInteractionModifierHook module : modules) {
        result = module.afterBlockUse(tool, modifier, context, source);
        if (result.consumesAction()) {
          return result;
        }
      }
      return result;
    }
  }
}
