package slimeknights.tconstruct.tools.modifiers.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.MagneticModifier;

/** TODO 1.21: move to {@link slimeknights.tconstruct.shared.effect} */
public class MagneticEffect extends TinkerEffect {
  public MagneticEffect() {
    super(MobEffectCategory.BENEFICIAL, 0x720000, false);
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
