package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.damagesource.DamageSource;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class WildfireModifier extends SingleUseModifier {
  public WildfireModifier() {
    super(0x487532);
  }

  @Override
  public void attackWithArmor(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
    if (isDirectDamage && !source.isProjectile()) {
      int fire = context.getEntity().getRemainingFireTicks();
      if (fire > 0) {
        // copy fire duration, merge with their current duration, and a little extra to account for divide flooring
        target.setRemainingFireTicks((fire + target.getRemainingFireTicks()) / 20 + 1);
      }
    }
  }
}
