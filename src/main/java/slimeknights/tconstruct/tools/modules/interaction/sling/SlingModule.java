package slimeknights.tconstruct.tools.modules.interaction.sling;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import slimeknights.mantle.data.loadable.field.RecordField;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

/** Common logic for sling modifiers such as bonking or flinging. */
public interface SlingModule extends ModifierModule, GeneralInteractionModifierHook, UsingToolModifierHook, ConditionalModule<IToolStackView> {
  List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SlingModule>defaultHooks(ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOL_USING);
  /** Field for the force multiplier */
  RecordField<Float,SlingModule> FORCE_FIELD = FloatLoadable.FROM_ZERO.requiredField("force_multiplier", SlingModule::forceMultiplier);
  /** Field for the draw time multiplier */
  RecordField<Float,SlingModule> DRAWTIME_FIELD = FloatLoadable.FROM_ZERO.requiredField("drawtime_multiplier", SlingModule::drawtimeMultiplier);
  /** Condition on the target to sling them */
  RecordField<IJsonPredicate<LivingEntity>,SlingModule> TARGET_FIELD = LivingEntityPredicate.LOADER.defaultField("target", SlingModule::target);

  /** Multiplier on the force to apply */
  float forceMultiplier();

  /** Multiplier on draw time, higher takes longer to drawback */
  float drawtimeMultiplier();

  /** Condition on the target to sling them */
  IJsonPredicate<LivingEntity> target();

  @Override
  default List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  default InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (!tool.isBroken() && source == InteractionSource.RIGHT_CLICK && condition().matches(tool, modifier)) {
      GeneralInteractionModifierHook.startUsingWithDrawtime(tool, modifier.getId(), player, hand, drawtimeMultiplier());
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }

  @Override
  default int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return 72000;
  }

  @Override
  default UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return ModifierUtil.blockWhileCharging(tool, UseAnim.BOW);
  }


  /* Helpers */

  /** Gets the current charge amount for this tool */
  default float getCharge(IToolStackView tool, ModifierEntry entry, int timeLeft) {
    return GeneralInteractionModifierHook.getToolCharge(tool, getUseDuration(tool, entry) - timeLeft);
  }

  /** Gets the scaled power to apply as a force multiplier. This is equivalent to 50% of power times velocity. */
  static float getPower(IToolStackView tool, LivingEntity living) {
    return ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.PROJECTILE_DAMAGE) / 2f * ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.VELOCITY);
  }
}
