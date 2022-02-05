package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.impl.SingleUseModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class BreathtakingModifier extends SingleUseModifier {
  @Override
  public void attackWithArmor(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
    if (isDirectDamage && !source.isProjectile()) {
      LivingEntity attacker = context.getEntity();
      int attackerAir = attacker.getAirSupply();
      int maxAir = attacker.getMaxAirSupply();
      if (attackerAir < maxAir) {
        attacker.setAirSupply(Math.min(attackerAir + 60, maxAir));
      }
      target.setAirSupply(Math.max(-20, target.getAirSupply() - 60));
    }
  }
}
