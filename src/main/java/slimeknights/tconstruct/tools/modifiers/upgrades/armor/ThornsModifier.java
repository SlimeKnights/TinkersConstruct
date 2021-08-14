package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class ThornsModifier extends Modifier {
  public ThornsModifier() {
    super(0x91C2AC);
  }

  @Override
  public void onAttacked(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // this works like vanilla, damage is capped due to the hurt immunity mechanics, so if multiple pieces apply thorns between us and vanilla, damage is capped at 4
    Entity attacker = source.getTrueSource();
    if (attacker != null && isDirectDamage) {
      // 15% chance of working per level
      if (RANDOM.nextFloat() < (level * 0.15f)) {
        float damage = level > 10 ? level - 10 : 1 + RANDOM.nextInt(4);
        LivingEntity user = context.getEntity();
        attacker.attackEntityFrom(DamageSource.causeThornsDamage(user), damage);
        ToolDamageUtil.damageAnimated(tool, 1, user, slotType);
      }
    }
  }
}
