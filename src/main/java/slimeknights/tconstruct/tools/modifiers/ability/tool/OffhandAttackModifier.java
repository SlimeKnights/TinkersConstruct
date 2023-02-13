package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.util.OffhandCooldownTracker;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class OffhandAttackModifier extends NoLevelsModifier implements EntityInteractionModifierHook, GeneralInteractionModifierHook {
  public static final ResourceLocation DUEL_WIELDING = TConstruct.getResource("duel_wielding");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.CHARGEABLE_INTERACT, TinkerHooks.ENTITY_INTERACT);
  }

  @Override
  public int getPriority() {
    return 90;
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return false;
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    volatileData.putBoolean(DUEL_WIELDING, true);
  }

  /** If true, we can use the attack */
  protected boolean canAttack(IToolStackView tool, Player player, InteractionHand hand) {
    return !tool.isBroken() && hand == InteractionHand.OFF_HAND && OffhandCooldownTracker.isAttackReady(player);
  }

  @Override
  public InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
    if (canAttack(tool, player, hand)) {
      if (!player.level.isClientSide()) {
        ToolAttackUtil.attackEntity(tool, player, InteractionHand.OFF_HAND, target, ToolAttackUtil.getCooldownFunction(player, InteractionHand.OFF_HAND), false, source.getSlot(hand));
      }
      OffhandCooldownTracker.applyCooldown(player, tool.getStats().get(ToolStats.ATTACK_SPEED), 20);
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
      OffhandCooldownTracker.applyCooldown(player, tool.getStats().get(ToolStats.ATTACK_SPEED), 20);
      // we handle swinging the arm, return consume to prevent resetting cooldown
      OffhandCooldownTracker.swingHand(player, InteractionHand.OFF_HAND, false);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (!tool.isBroken() && context.getChangedSlot() == EquipmentSlot.OFFHAND) {
      context.getEntity().getCapability(OffhandCooldownTracker.CAPABILITY).ifPresent(cap -> cap.setEnabled(true));
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (!tool.isBroken() && context.getChangedSlot() == EquipmentSlot.OFFHAND) {
      context.getEntity().getCapability(OffhandCooldownTracker.CAPABILITY).ifPresent(cap -> cap.setEnabled(false));
    }
  }
}
