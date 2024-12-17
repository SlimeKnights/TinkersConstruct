package slimeknights.tconstruct.tools.modifiers.ability.interaction;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class BlockingModifier extends NoLevelsModifier implements GeneralInteractionModifierHook, ToolActionModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOL_ACTION);
  }

  @Override
  public int getPriority() {
    return 50; // late as many modifiers have special blocking interactions
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (source == InteractionSource.RIGHT_CLICK && !tool.isBroken()) {
      GeneralInteractionModifierHook.startUsing(tool, modifier.getId(), player, hand);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  @Override
  public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return 72000;
  }

  @Override
  public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return UseAnim.BLOCK;
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, ModifierEntry entry, ToolAction toolAction) {
    return toolAction == ToolActions.SHIELD_BLOCK;
  }

  /**
   * Makes the tool use the blocking animation if the blocking modifier is installed, falling back to the given animation.
   * Allows your tool to block while charging up.
   */
  public static UseAnim blockWhileCharging(IToolStackView tool, UseAnim fallback) {
    return ModifierUtil.canPerformAction(tool, ToolActions.SHIELD_BLOCK) ? UseAnim.BLOCK : fallback;
  }
}
