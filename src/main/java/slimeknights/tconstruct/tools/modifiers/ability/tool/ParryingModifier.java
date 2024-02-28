package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class ParryingModifier extends OffhandAttackModifier {
  @Override
  public int getPriority() {
    return 100;
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return true;
  }

  @Override
  public InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
    if (source == InteractionSource.RIGHT_CLICK) {
      InteractionResult result = super.beforeEntityUse(tool, modifier, player, target, hand, source);
      if (result.consumesAction()) {
        ModifierUtil.startUsingItem(tool, modifier.getId(), player, hand);
      }
      return result;
    }
    return InteractionResult.PASS;
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (source == InteractionSource.RIGHT_CLICK && hand == InteractionHand.OFF_HAND) {
      InteractionResult result = super.onToolUse(tool, modifier, player, hand, source);
      // also allow just blocking when used in main hand
      if (result.consumesAction()) {
        ModifierUtil.startUsingItem(tool, modifier.getId(), player, hand);
        return InteractionResult.CONSUME;
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public boolean onFinishUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity) {
    if (entity instanceof Player player) {
      player.getCooldowns().addCooldown(tool.getItem(), (int)(20 / tool.getStats().get(ToolStats.ATTACK_SPEED)));
    }
    return true;
  }

  @Override
  public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return UseAnim.BLOCK;
  }

  @Override
  public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return 20;
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, int level, ToolAction toolAction) {
    return toolAction == ToolActions.SHIELD_BLOCK;
  }
}
