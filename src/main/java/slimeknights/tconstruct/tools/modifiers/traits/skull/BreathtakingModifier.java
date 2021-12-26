package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class BreathtakingModifier extends SingleUseModifier {
  public BreathtakingModifier() {
    super(0x56847E);
  }

  @Override
  public void attackWithArmor(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
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
