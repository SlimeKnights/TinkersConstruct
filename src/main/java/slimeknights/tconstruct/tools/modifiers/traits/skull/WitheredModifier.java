package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.impl.TotalArmorLevelModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class WitheredModifier extends TotalArmorLevelModifier {
  public WitheredModifier() {
    super(0x343434, StrongBonesModifier.CALCIFIABLE, true);
  }

  @Override
  public void attackWithArmor(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
    // drink milk for more power, but less duration
    if (isDirectDamage && !source.isProjectile()) {
      boolean isCalcified = context.getEntity().hasEffect(TinkerModifiers.calcifiedEffect.get());
      target.addEffect(new MobEffectInstance(MobEffects.WITHER, isCalcified ? 100 : 200, isCalcified ? 1 : 0));
    }
  }
}
