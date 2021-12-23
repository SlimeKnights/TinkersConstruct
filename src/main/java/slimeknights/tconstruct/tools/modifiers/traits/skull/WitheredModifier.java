package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.modifiers.impl.TotalArmorLevelModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class WitheredModifier extends TotalArmorLevelModifier {
  public WitheredModifier() {
    super(0x343434, StrongBonesModifier.CALCIFIABLE, true);
  }

  @Override
  public void attackWithArmor(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
    // drink milk for more power, but less duration
    if (isDirectDamage && !source.isProjectile()) {
      boolean isCalcified = context.getEntity().isPotionActive(TinkerModifiers.calcifiedEffect.get());
      target.addPotionEffect(new EffectInstance(Effects.WITHER, isCalcified ? 100 : 200, isCalcified ? 1 : 0));
    }
  }
}
