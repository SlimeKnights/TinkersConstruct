package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class StickyModifier extends IncrementalModifier {
  /** Gets the slowness effect for the given level */
  private static MobEffectInstance getEffect(float scaledLevel) {
    int duration = 20;
    int maxBonus = (int)(10 * scaledLevel);
    if (maxBonus > 0) {
      duration += RANDOM.nextInt(maxBonus);
    }
    return new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, Math.round(scaledLevel));
  }

  @Override
  public void onAttacked(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    Entity attacker = source.getEntity();
    if (isDirectDamage && attacker instanceof LivingEntity) {
      // 15% chance of working per level
      float scaledLevel = getScaledLevel(tool, level);
      if (RANDOM.nextFloat() < (scaledLevel * 0.25f)) {
        ((LivingEntity)attacker).addEffect(getEffect(scaledLevel));
        ToolDamageUtil.damageAnimated(tool, 1, context.getEntity(), slotType);
      }
    }
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      target.addEffect(getEffect(getScaledLevel(tool, level)));
    }
    return 0;
  }
}
