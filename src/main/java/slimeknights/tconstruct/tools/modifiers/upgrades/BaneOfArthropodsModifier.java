package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class BaneOfArthropodsModifier extends ScaledTypeDamageModifier {
  public BaneOfArthropodsModifier() {
    super(0xD39A88, EntityGroup.ARTHROPOD);
  }

  @Override
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damageDealt, boolean isCritical, boolean fullyCharged) {
    if (target.getGroup() == EntityGroup.ARTHROPOD) {
      int duration = 20 + attacker.getRandom().nextInt(10 * level);
      target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 3));
    }
    return 0;
  }
}
