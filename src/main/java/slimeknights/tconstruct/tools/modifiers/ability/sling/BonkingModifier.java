package slimeknights.tconstruct.tools.modifiers.ability.sling;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.interaction.sling.SlingKnockbackModule;

/** @deprecated use {@link SlingKnockbackModule} */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
public class BonkingModifier extends SlingModifier implements MeleeHitModifierHook, MeleeDamageModifierHook {
  private static final SlingKnockbackModule BONKING = new SlingKnockbackModule(3, 1.5f, 1.5f, LivingEntityPredicate.ANY, ModifierCondition.ANY_TOOL);
  /** @deprecated use {@link SlingKnockbackModule#IS_BONKING}. */
  @Deprecated(forRemoval = true)
  public static final ResourceLocation IS_BONKING = SlingKnockbackModule.IS_BONKING;

  @Override
  protected void registerHooks(Builder builder) {
    super.registerHooks(builder);
    builder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.MELEE_DAMAGE);
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    return BONKING.onToolUse(tool, modifier, player, hand, source);
  }

  @Override
  public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    return BONKING.beforeMeleeHit(tool, modifier, context, damage, baseKnockback, knockback);
  }

  @Override
  public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
    return BONKING.getMeleeDamage(tool, modifier, context, baseDamage, damage);
  }

  @Override
  public void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    BONKING.beforeReleaseUsing(tool, modifier, entity, useDuration, timeLeft, activeModifier);
  }
}
