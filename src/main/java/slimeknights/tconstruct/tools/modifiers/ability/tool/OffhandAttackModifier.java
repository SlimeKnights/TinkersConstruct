package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.util.OffhandCooldownTracker;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class OffhandAttackModifier extends NoLevelsModifier implements EntityInteractionModifierHook, GeneralInteractionModifierHook, VolatileDataModifierHook {
  public static final ResourceLocation DUEL_WIELDING = TConstruct.getResource("duel_wielding");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT, ModifierHooks.ENTITY_INTERACT, ModifierHooks.VOLATILE_DATA);
    hookBuilder.addModule(ShowOffhandModule.DISALLOW_BROKEN);
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return false;
  }

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
    volatileData.putBoolean(DUEL_WIELDING, true);
  }

  /** If true, we can use the attack */
  protected boolean canAttack(IToolStackView tool, Player player, InteractionHand hand) {
    return ToolAttackUtil.canPerformAttack(tool) && hand == InteractionHand.OFF_HAND && OffhandCooldownTracker.isAttackReady(player);
  }

  /** Applies offhand cooldown based on the tool attack speed */
  private static void applyCooldown(IToolStackView tool, Player player, InteractionSource source) {
    float attackSpeed;
    if (source == InteractionSource.ARMOR) {
      // for armor, always assume attack speed is 4.0, we cannot change the attack speed of the main hand and we want them to match
      attackSpeed = 4;
    } else {
      // if we get here, its always offhand
      // need to cancel out the base 4 attack speed in the tool attack speed, since we removed the main hand one doing it
      attackSpeed = ToolAttackUtil.getToolAttribute(tool, player, Attributes.ATTACK_SPEED, tool.getStats().get(ToolStats.ATTACK_SPEED) - 4);
    }
    OffhandCooldownTracker.applyCooldown(player, attackSpeed, 20);
  }

  @Override
  public InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
    if (canAttack(tool, player, hand)) {
      if (!player.level().isClientSide() && ToolAttackUtil.isAttackable(player, target)) {
        ToolAttackUtil.performAttack(tool, ToolAttackContext.attacker(player).target(target).offhandCooldown().slot(source.getSlot(hand), hand).toolAttributes(tool).build());
      }
      applyCooldown(tool, player, source);
      // we handle swinging the arm, return consume to prevent resetting cooldown
      OffhandCooldownTracker.swingHand(player, InteractionHand.OFF_HAND, false);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (canAttack(tool, player, hand)) {
      // target done in onEntityInteract, this is just for cooldown cause you missed
      applyCooldown(tool, player, source);
      // we handle swinging the arm, return consume to prevent resetting cooldown
      OffhandCooldownTracker.swingHand(player, InteractionHand.OFF_HAND, false);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }
}
