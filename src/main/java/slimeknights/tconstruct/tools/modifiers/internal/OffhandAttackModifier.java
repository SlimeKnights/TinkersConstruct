package slimeknights.tconstruct.tools.modifiers.internal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableWeapon;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class OffhandAttackModifier extends Modifier {
  public OffhandAttackModifier(int color) {
    super(color);
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return false;
  }

  @Override
  public ActionResultType onToolUse(IModifierToolStack tool, int level, World world, PlayerEntity player, Hand hand) {
    if (hand == Hand.OFF_HAND && !player.getCooldownTracker().hasCooldown(tool.getItem()) && tool.getItem() instanceof IModifiableWeapon) {
      // target done in onEntityUse, this is just for cooldown cause you missed
      player.swingArm(Hand.OFF_HAND);
      player.getCooldownTracker().setCooldown(tool.getItem(), (int)(20 / tool.getStats().getFloat(ToolStats.ATTACK_SPEED)));
      return ActionResultType.SUCCESS;
    }
    return ActionResultType.PASS;
  }

  @Override
  public ActionResultType onEntityUse(IModifierToolStack tool, int level, PlayerEntity player, LivingEntity target, Hand hand) {
    if (hand == Hand.OFF_HAND && !player.getCooldownTracker().hasCooldown(tool.getItem()) && tool.getItem() instanceof IModifiableWeapon) {
      ToolAttackUtil.attackEntity((IModifiableWeapon) tool.getItem(), tool, player, Hand.OFF_HAND, target, false, false);
      player.swingArm(Hand.OFF_HAND);
      player.getCooldownTracker().setCooldown(tool.getItem(), (int)(20 / tool.getStats().getFloat(ToolStats.ATTACK_SPEED)));
      return ActionResultType.SUCCESS;
    }
    return ActionResultType.PASS;
  }
}
