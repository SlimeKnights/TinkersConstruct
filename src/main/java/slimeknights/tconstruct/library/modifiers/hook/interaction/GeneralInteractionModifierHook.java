package slimeknights.tconstruct.library.modifiers.hook.interaction;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;
import java.util.function.Function;

/**
 * Hooks for standard interaction logic post block/entity interaction. See {@link GeneralInteractionModifierHook} for general interaction and {@link EntityInteractionModifierHook} for entities.
 */
public interface GeneralInteractionModifierHook {
  /** Default instance that performs no action */
  GeneralInteractionModifierHook EMPTY = (tool, modifier, player, hand, source) -> InteractionResult.PASS;
  /** Merger that returns when the first hook succeeds */
  Function<Collection<GeneralInteractionModifierHook>, GeneralInteractionModifierHook> FIRST_MERGER = FirstMerger::new;


  /**
   * Hook called after block/entity interaction passes, or when interacting with empty air.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @param player     Interacting player
   * @param hand       Hand used for interaction
   * @param source     Source of the interaction
   * @return  Return PASS or FAIL to allow vanilla handling, any other to stop vanilla and later modifiers from running.
   */
  InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source);


  /* Charged usage */

  /**
   * Called when the player stops using the tool without finishing. See {@link #onFinishUsing(IToolStackView, ModifierEntry, LivingEntity)} for finishing interaction.
   * Only supported for {@link InteractionSource#RIGHT_CLICK}.
   * To setup, use {@link LivingEntity#startUsingItem(InteractionHand)} in {@link #onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)}.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @param entity     Interacting entity
   * @param timeLeft   How many ticks of use duration was left
   * @return  Whether the modifier should block any incoming ones from firing
   */
  default boolean onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
    return false;
  }

  /**
   * Called when the use duration on this tool reaches the end. See {@link #onStoppedUsing(IToolStackView, ModifierEntry, LivingEntity, int)} for unfinished interaction.
   * To setup, use {@link LivingEntity#startUsingItem(InteractionHand)} in {@link #onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)} and set the duration in {@link #getUseDuration(IToolStackView, ModifierEntry)}
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @param entity     Interacting entity
   * @return  Whether the modifier should block any incoming ones from firing
   */
  default boolean onFinishUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity) {
    return false;
  }

  /**
   * Determines how long usage lasts on a tool.
   * <p>
   * Since these hooks are called from several locations, it is recommended to set a boolean in persistent data
   * {@link #onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)} and only respond to these hooks if that boolean is set.
   * The boolean should be cleared in both {@link #onFinishUsing(IToolStackView, ModifierEntry, LivingEntity)} and {@link #onStoppedUsing(IToolStackView, ModifierEntry, LivingEntity, int)}.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @return  For how many ticks the modifier should run its use action
   */
  default int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return 0;
  }

  /**
   * Determines how long usage lasts on a tool.
   * <p>
   * Since these hooks are called from several locations, it is recommended to set a boolean in persistent data
   * {@link #onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)} and only respond to these hooks if that boolean is set.
   * The boolean should be cleared in both {@link #onFinishUsing(IToolStackView, ModifierEntry, LivingEntity)} and {@link #onStoppedUsing(IToolStackView, ModifierEntry, LivingEntity, int)}.
   * @param tool       Tool performing interaction
   * @param modifier   Modifier instance
   * @return  Use action to be performed
   */
  default UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return UseAnim.NONE;
  }


  /** Logic to merge multiple interaction hooks into one */
  record FirstMerger(Collection<GeneralInteractionModifierHook> modules) implements GeneralInteractionModifierHook {
    @Override
    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
      InteractionResult result = InteractionResult.PASS;
      for (GeneralInteractionModifierHook module : modules) {
        result = module.onToolUse(tool, modifier, player, hand, source);
        if (result.consumesAction()) {
          return result;
        }
      }
      return result;
    }

    @Override
    public boolean onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
      for (GeneralInteractionModifierHook module : modules) {
        if (module.onStoppedUsing(tool, modifier, entity, timeLeft)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean onFinishUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity) {
      for (GeneralInteractionModifierHook module : modules) {
        if (module.onFinishUsing(tool, modifier, entity)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
      for (GeneralInteractionModifierHook module : modules) {
        int duration = module.getUseDuration(tool, modifier);
        if (duration > 0) {
          return duration;
        }
      }
      return 0;
    }

    @Override
    public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
      for (GeneralInteractionModifierHook module : modules) {
        UseAnim anim = module.getUseAction(tool, modifier);
        if (anim != UseAnim.NONE) {
          return anim;
        }
      }
      return UseAnim.NONE;
    }
  }

  /** Fallback logic calling old hooks, remove in 1.19 */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  GeneralInteractionModifierHook FALLBACK = new GeneralInteractionModifierHook() {
    @Override
    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
      if (source != InteractionSource.LEFT_CLICK) {
        return modifier.getModifier().onToolUse(tool, modifier.getLevel(), player.level, player, hand, source.getSlot(hand));
      }
      return InteractionResult.PASS;
    }

    @Override
    public boolean onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
      return modifier.getModifier().onStoppedUsing(tool, modifier.getLevel(), entity.level, entity, timeLeft);
    }

    @Override
    public boolean onFinishUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity) {
      return modifier.getModifier().onFinishUsing(tool, modifier.getLevel(), entity.level, entity);
    }

    @Override
    public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
      return modifier.getModifier().getUseDuration(tool, modifier.getLevel());
    }

    @Override
    public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
      return modifier.getModifier().getUseAction(tool, modifier.getLevel());
    }
  };
}
