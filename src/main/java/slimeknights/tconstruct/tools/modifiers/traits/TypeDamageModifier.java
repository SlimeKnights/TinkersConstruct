package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class TypeDamageModifier extends Modifier {
  private final CreatureAttribute type;
  public TypeDamageModifier(int color, CreatureAttribute type) {
    super(color);
    this.type = type;
  }

  @Override
  public float applyLivingDamage(IModifierToolStack tool, int level, LivingEntity attackerLiving, LivingEntity targetLiving, float baseDamage, float damage, boolean isCritical, boolean fullyCharged) {
    if (targetLiving.getCreatureAttribute() == type) {
      damage += level * 2.5f;
    }
    return damage;
  }
}
