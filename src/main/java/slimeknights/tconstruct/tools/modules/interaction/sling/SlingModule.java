package slimeknights.tconstruct.tools.modules.interaction.sling;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.field.RecordField;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modifiers.effect.HelmetChargingEffect;

import java.util.List;

/** Common logic for sling modifiers such as bonking or flinging. */
public interface SlingModule extends ModifierModule, GeneralInteractionModifierHook, UsingToolModifierHook, KeybindInteractModifierHook, ConditionalModule<IToolStackView> {
  List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SlingModule>defaultHooks(ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOL_USING, ModifierHooks.ARMOR_INTERACT);
  /** Field for the force multiplier */
  RecordField<LevelingValue,SlingModule> FORCE_FIELD = LevelingValue.LOADABLE.requiredField("force_multiplier", SlingModule::forceMultiplier);
  /** Field for the draw time multiplier */
  RecordField<Float,SlingModule> DRAWTIME_FIELD = FloatLoadable.FROM_ZERO.requiredField("drawtime_multiplier", SlingModule::drawtimeMultiplier);
  /** Condition on the target to sling them */
  RecordField<IJsonPredicate<LivingEntity>,SlingModule> TARGET_FIELD = LivingEntityPredicate.LOADER.defaultField("target", SlingModule::target);

  /** Multiplier on the force to apply */
  LevelingValue forceMultiplier();

  /** Multiplier on draw time, higher takes longer to drawback */
  float drawtimeMultiplier();

  /** Condition on the target to sling them */
  IJsonPredicate<LivingEntity> target();

  @Override
  default List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /**
   * Common method to run sling logic, between armor keybind and held.
   * @param tool            Tool being used
   * @param modifier        Modifier being used
   * @param entity          Entity using the tool
   * @param chargeTime      Duration the tool has been used
   * @param activeModifier  Currently active modifier on the tool
   */
  void sling(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int chargeTime, ModifierEntry activeModifier);


  /* Held */

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

  @Override
  default void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    if (!tool.isBroken() && condition().matches(tool, modifier)) {
      int chargeTime = getUseDuration(tool, modifier) - timeLeft;
      if (chargeTime > 0) {
        sling(tool, modifier, entity, chargeTime, activeModifier);
      }
    }
  }


  /* Helmet */

  @Override
  default boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
    if (!tool.isBroken() && keyModifier == TooltipKey.NORMAL && condition().matches(tool, modifier)) {
      HelmetChargingEffect.startUsingHelmet(tool, player, drawtimeMultiplier());
      return true;
    }
    return false;
  }

  @Override
  default void stopInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, int chargeTime, ModifierEntry activeModifier) {
    if (chargeTime > 0 && !tool.isBroken() && condition().matches(tool, modifier)) {
      sling(tool, modifier, player, chargeTime, activeModifier);
    }
  }


  /* Helpers */

  /** Gets the current charge amount for this tool */
  default float getCharge(IToolStackView tool, ModifierEntry entry, int chargeTime) {
    return GeneralInteractionModifierHook.getToolCharge(tool, chargeTime);
  }

  /** Gets the scaled power to apply as a force multiplier. This is equivalent to 50% of power times velocity. */
  static float getPower(IToolStackView tool, LivingEntity living) {
    return ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.PROJECTILE_DAMAGE) / 2f * ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.VELOCITY);
  }
}
