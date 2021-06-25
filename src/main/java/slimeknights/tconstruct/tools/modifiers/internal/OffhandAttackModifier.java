package slimeknights.tconstruct.tools.modifiers.internal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableWeapon;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class OffhandAttackModifier extends SingleUseModifier {
  public static final ResourceLocation DUEL_WIELDING = Util.getResource("duel_wielding");
  private final int cooldownTime;
  public OffhandAttackModifier(int color, int cooldownTime) {
    super(color);
    // vanilla is 20 / attackSpeed, making it 25 / attackSpeed makes the offhand only 80% of the speed
    this.cooldownTime = cooldownTime;
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return false;
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    volatileData.putBoolean(DUEL_WIELDING, true);
  }

  /** If true, we can use the attack */
  protected boolean canAttack(IModifierToolStack tool, PlayerEntity player, Hand hand) {
    return hand == Hand.OFF_HAND && !player.getCooldownTracker().hasCooldown(tool.getItem()) && tool.getItem() instanceof IModifiableWeapon;
  }

  /** Applies cooldown to the player */
  protected void applyCooldown(IModifierToolStack tool, PlayerEntity player) {
    player.getCooldownTracker().setCooldown(tool.getItem(), (int)(cooldownTime / (tool.getStats().getFloat(ToolStats.ATTACK_SPEED))));
  }

  @Override
  public ActionResultType onToolUse(IModifierToolStack tool, int level, World world, PlayerEntity player, Hand hand) {
    if (canAttack(tool, player, hand)) {
      // target done in onEntityUse, this is just for cooldown cause you missed
      player.swing(Hand.OFF_HAND, false);
      applyCooldown(tool, player);
      // we handle swinging the arm, return consume to prevent resetting cooldown
      player.swing(Hand.OFF_HAND, !player.world.isRemote());
      return ActionResultType.CONSUME;
    }
    return ActionResultType.PASS;
  }

  @Override
  public ActionResultType onEntityUse(IModifierToolStack tool, int level, PlayerEntity player, LivingEntity target, Hand hand) {
    if (canAttack(tool, player, hand)) {
      if (!player.world.isRemote()) {
        int oldHurtResistance = target.hurtResistantTime;
        target.hurtResistantTime = 0;
        ToolAttackUtil.attackEntity((IModifiableWeapon)tool.getItem(), tool, player, Hand.OFF_HAND, target, false, false);
        target.hurtResistantTime = oldHurtResistance;
      }
      applyCooldown(tool, player);
      // we handle swinging the arm, return consume to prevent resetting cooldown
      player.swing(Hand.OFF_HAND, !player.world.isRemote());
      return ActionResultType.CONSUME;
    }
    return ActionResultType.PASS;
  }
}
