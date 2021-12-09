package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.List;

public class ThornsModifier extends IncrementalModifier {
  public ThornsModifier() {
    super(0x9FA76D);
  }

  @Override
  public void onAttacked(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // this works like vanilla, damage is capped due to the hurt immunity mechanics, so if multiple pieces apply thorns between us and vanilla, damage is capped at 4
    Entity attacker = source.getTrueSource();
    if (attacker != null && isDirectDamage) {
      // 15% chance of working per level
      float scaledLevel = getScaledLevel(tool, level);
      if (RANDOM.nextFloat() < (scaledLevel * 0.15f)) {
        float damage = scaledLevel > 10 ? scaledLevel - 10 : 1 + RANDOM.nextInt(4);
        LivingEntity user = context.getEntity();
        attacker.attackEntityFrom(DamageSource.causeThornsDamage(user), damage);
        ToolDamageUtil.damageAnimated(tool, 1, user, slotType);
      }
    }
  }

  @Override
  public int afterEntityHit(IModifierToolStack tool, int level, ToolAttackContext context, float damageDealt) {
    // deals 1 pierce damage per level for unarmed, scaled, half of sharpness
    DamageSource source;
    PlayerEntity player = context.getPlayerAttacker();
    if (player != null) {
      source = DamageSource.causePlayerDamage(player);
    } else {
      source = DamageSource.causeMobDamage(context.getAttacker());
    }
    source.setDamageBypassesArmor();
    float secondaryDamage = (getScaledLevel(tool, level) * tool.getModifier(ToolStats.ATTACK_DAMAGE)) * context.getCooldown();
    if (context.isCritical()) {
      secondaryDamage *= 1.5f;
    }
    ToolAttackUtil.attackEntitySecondary(source, secondaryDamage, context.getTarget(), context.getLivingTarget(), true);
    return 0;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, TooltipFlag flag) {
    if (tool.getModifierLevel(TinkerModifiers.unarmed.get()) > 0) {
      addDamageTooltip(tool, getScaledLevel(tool, level) * 1.0f, tooltip);
    }
  }
}
