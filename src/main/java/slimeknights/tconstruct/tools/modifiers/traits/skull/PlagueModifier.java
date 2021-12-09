package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class PlagueModifier extends SingleUseModifier {
  public PlagueModifier() {
    super(0x59503B);
  }

  @Override
  public void attackWithArmor(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
    if (isDirectDamage && !source.isProjectile()) {
      // copy all negative effects to target
      for (EffectInstance effect : context.getEntity().getActivePotionEffects()) {
        if (!effect.getPotion().isBeneficial() && !effect.getCurativeItems().isEmpty()) {
          target.addPotionEffect(new EffectInstance(effect));
        }
      }
    }
  }
}
