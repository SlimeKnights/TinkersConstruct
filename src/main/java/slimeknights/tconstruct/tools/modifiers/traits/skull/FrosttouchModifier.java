package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.impl.TotalArmorLevelModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class FrosttouchModifier extends TotalArmorLevelModifier {
  public FrosttouchModifier() {
    super(StrongBonesModifier.CALCIFIABLE, true);
  }

  @Override
  public void attackWithArmor(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
    // must drink milk to melee slowness. Always can range slowness
    if (isDirectDamage) {
      boolean isCalcified = context.getEntity().hasEffect(TinkerModifiers.calcifiedEffect.get());
      if (isCalcified || source.isProjectile()) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, isCalcified ? 1 : 0));
      }
    }
  }
}
