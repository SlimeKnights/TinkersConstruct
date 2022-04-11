package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

public class ThornsModifier extends IncrementalModifier {
  @Override
  public void onAttacked(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // this works like vanilla, damage is capped due to the hurt immunity mechanics, so if multiple pieces apply thorns between us and vanilla, damage is capped at 4
    Entity attacker = source.getEntity();
    if (attacker != null && isDirectDamage) {
      // 15% chance of working per level
      float scaledLevel = getScaledLevel(tool, level);
      if (RANDOM.nextFloat() < (scaledLevel * 0.15f)) {
        float damage = scaledLevel > 10 ? scaledLevel - 10 : 1 + RANDOM.nextInt(4);
        LivingEntity user = context.getEntity();
        attacker.hurt(DamageSource.thorns(user), damage);
        ToolDamageUtil.damageAnimated(tool, 1, user, slotType);
      }
    }
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    // deals 1 pierce damage per level for unarmed, scaled, half of sharpness
    DamageSource source;
    Player player = context.getPlayerAttacker();
    if (player != null) {
      source = DamageSource.playerAttack(player);
    } else {
      source = DamageSource.mobAttack(context.getAttacker());
    }
    source.bypassArmor();
    float secondaryDamage = (getScaledLevel(tool, level) * tool.getMultiplier(ToolStats.ATTACK_DAMAGE) * 0.75f) * context.getCooldown();
    if (context.isCritical()) {
      secondaryDamage *= 1.5f;
    }
    ToolAttackUtil.attackEntitySecondary(source, secondaryDamage, context.getTarget(), context.getLivingTarget(), true);
    return 0;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (tool.getModifierLevel(TinkerModifiers.unarmed.getId()) > 0) {
      addDamageTooltip(tool, getScaledLevel(tool, level) * 0.75f, tooltip);
    }
  }
}
