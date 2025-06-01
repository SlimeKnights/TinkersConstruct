package slimeknights.tconstruct.tools.modifiers.ability.sling;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.shared.TinkerAttributes;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;

/**
 * Shared logic for all slinging modifiers
 */
public abstract class SlingModifier extends NoLevelsModifier implements GeneralInteractionModifierHook, UsingToolModifierHook {
  @Override
  protected void registerHooks(Builder builder) {
    builder.addHook(this, ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOL_USING);
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (!tool.isBroken() && source == InteractionSource.RIGHT_CLICK) {
      GeneralInteractionModifierHook.startUsingWithDrawtime(tool, modifier.getId(), player, hand, 1.5f);
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }

  @Override
  public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return 72000;
  }

  @Override
  public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return BlockingModifier.blockWhileCharging(tool, UseAnim.BOW);
  }

  /** Gets the force to launch the sling at, considers drawspeed and velocity */
  protected float getForce(IToolStackView tool, ModifierEntry entry, LivingEntity living, int timeLeft, boolean applyKnockback) {
    int chargeTime = getUseDuration(tool, entry) - timeLeft;
    if (chargeTime < 0) {
      return 0;
    }
    // if using knockback, boost projectile damage by 0.5 per level, that is a 25% boost, same as power
    // TODO: this is pretty hardcoded, is there a good way to softcode this?
    float knockback = 0;
    if (applyKnockback) {
      knockback = tool.getModifierLevel(TinkerModifiers.knockback.getId()) / 2f;
    }
    float force = GeneralInteractionModifierHook.getToolCharge(tool, chargeTime)
                  * (ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.PROJECTILE_DAMAGE) + knockback) / 2f
                  * ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.VELOCITY);
    // knockback also means we should apply padded, divide per level
    if (applyKnockback) {
      force /= Math.pow(2, tool.getModifierLevel(TinkerModifiers.padded.getId()));
    }
    return force;
  }

  /** Scales the given knockback value using the two attributes */
  public static float scaleKnockback(LivingEntity target, float knockback) {
    return (float) (knockback * target.getAttributeValue(TinkerAttributes.KNOCKBACK_MULTIPLIER.get()) * (1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
  }

  /** Checks if this modifier is the one actively being used. Used for failure sound effects. */
  public static boolean isActive(IToolStackView tool, ModifierEntry modifier, ModifierEntry activeModifier) {
    // active modifier being us, or a bow is firing and no drawback ammo
    return modifier == activeModifier || (activeModifier.getLevel() == 0 && !tool.getPersistentData().contains(ModifiableLauncherItem.KEY_DRAWBACK_AMMO));
  }
}
