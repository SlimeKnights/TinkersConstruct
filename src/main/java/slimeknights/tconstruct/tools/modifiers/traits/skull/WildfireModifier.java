package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class WildfireModifier extends SingleUseModifier {
  public WildfireModifier() {
    super(0x487532);
  }

  @Override
  public void attackWithArmor(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
    if (isDirectDamage && !source.isProjectile()) {
      int fire = context.getEntity().getFireTimer();
      if (fire > 0) {
        // copy fire duration, merge with their current duration, and a little extra to account for divide flooring
        target.setFire((fire + target.getFireTimer()) / 20 + 1);
      }
    }
  }
}
