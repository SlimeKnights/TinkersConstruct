package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class WildfireModifier extends NoLevelsModifier {
  @Override
  public void attackWithArmor(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
    if (isDirectDamage && !source.isProjectile()) {
      int fire = context.getEntity().getRemainingFireTicks();
      if (fire > 0) {
        // copy fire duration, merge with their current duration, and a little extra to account for divide flooring
        target.setRemainingFireTicks((fire + target.getRemainingFireTicks()) / 20 + 1);
      }
    }
  }
}
