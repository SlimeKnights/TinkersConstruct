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

public class FrosttouchModifier extends TotalArmorLevelModifier {
  public FrosttouchModifier() {
    super(0xC5D6D5, StrongBonesModifier.CALCIFIABLE, true);
  }

  @Override
  public void attackWithArmor(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
    // must drink milk to melee slowness. Always can range slowness
    if (isDirectDamage) {
      boolean isCalcified = context.getEntity().isPotionActive(TinkerModifiers.calcifiedEffect.get());
      if (isCalcified || source.isProjectile()) {
        target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 300, isCalcified ? 1 : 0));
      }
    }
  }
}
