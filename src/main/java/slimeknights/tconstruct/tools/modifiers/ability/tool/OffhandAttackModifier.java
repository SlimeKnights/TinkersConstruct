package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import slimeknights.mantle.util.OffhandCooldownTracker;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.SingleUseModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class OffhandAttackModifier extends SingleUseModifier {
  public static final ResourceLocation DUEL_WIELDING = TConstruct.getResource("duel_wielding");

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
    return hand == InteractionHand.OFF_HAND && OffhandCooldownTracker.isAttackReady(player);
  }

  @Override
  public InteractionResult beforeEntityUse(IToolStackView tool, int level, Player player, Entity target, InteractionHand hand, EquipmentSlot slotType) {
    if (canAttack(tool, player, hand)) {
      if (!player.level.isClientSide()) {
        ToolAttackUtil.attackEntity(tool, player, InteractionHand.OFF_HAND, target, ToolAttackUtil.getCooldownFunction(player, InteractionHand.OFF_HAND), false, slotType);
      }
      OffhandCooldownTracker.applyCooldown(player, tool.getStats().get(ToolStats.ATTACK_SPEED), 20);
      // we handle swinging the arm, return consume to prevent resetting cooldown
      OffhandCooldownTracker.swingHand(player, InteractionHand.OFF_HAND, false);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, int level, Level world, Player player, InteractionHand hand, EquipmentSlot slotType) {
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
