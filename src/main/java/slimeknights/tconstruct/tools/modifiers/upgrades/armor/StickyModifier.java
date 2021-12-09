package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class StickyModifier extends IncrementalModifier {
  public StickyModifier() {
    super(0xffffff);
  }

  /** Gets the slowness effect for the given level */
  private static EffectInstance getEffect(float scaledLevel) {
    int duration = 20;
    int maxBonus = (int)(10 * scaledLevel);
    if (maxBonus > 0) {
      duration += RANDOM.nextInt(maxBonus);
    }
    return new EffectInstance(Effects.SLOWNESS, duration, Math.round(scaledLevel));
  }

  @Override
  public void onAttacked(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float amount, boolean isDirectDamage) {
    Entity attacker = source.getTrueSource();
    if (isDirectDamage && attacker instanceof LivingEntity) {
      // 15% chance of working per level
      float scaledLevel = getScaledLevel(tool, level);
      if (RANDOM.nextFloat() < (scaledLevel * 0.25f)) {
        ((LivingEntity)attacker).addPotionEffect(getEffect(scaledLevel));
        ToolDamageUtil.damageAnimated(tool, 1, context.getEntity(), slotType);
      }
    }
  }

  @Override
  public int afterEntityHit(IModifierToolStack tool, int level, ToolAttackContext context, float damageDealt) {
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      target.addPotionEffect(getEffect(getScaledLevel(tool, level)));
    }
    return 0;
  }
}
