package slimeknights.tconstruct.tools.modifiers.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.MagneticModifier;

public class MagneticEffect extends NoMilkEffect {
  public MagneticEffect() {
    super(EffectType.BENEFICIAL, 0x720000, false);
  }

  @Override
  public boolean isDurationEffectTick(int duration, int amplifier) {
    return (duration & 1) == 0;
  }

  @Override
  public void applyEffectTick(LivingEntity entity, int amplifier) {
    MagneticModifier.applyMagnet(entity, amplifier);
  }
}
