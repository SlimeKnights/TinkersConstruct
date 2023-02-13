package slimeknights.tconstruct.tools.modifiers.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.MagneticModifier;

public class RepulsiveEffect extends NoMilkEffect {
  public RepulsiveEffect() {
    super(MobEffectCategory.BENEFICIAL, 0x727272, false);
  }

  @Override
  public boolean isDurationEffectTick(int duration, int amplifier) {
    return (duration & 1) == 0;
  }

  @Override
  public void applyEffectTick(LivingEntity entity, int amplifier) {
    MagneticModifier.applyVelocity(entity, amplifier, LivingEntity.class, 2, -0.1f, 10);
  }
}
