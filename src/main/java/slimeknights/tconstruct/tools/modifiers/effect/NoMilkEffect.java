package slimeknights.tconstruct.tools.modifiers.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import slimeknights.tconstruct.common.TinkerEffect;

import java.util.Set;

/**
 * Effect that cannot be cured with milk
 * TODO 1.21: move to {@link slimeknights.tconstruct.shared.effect}
 */
public class NoMilkEffect extends TinkerEffect {
  public NoMilkEffect(MobEffectCategory typeIn, int color, boolean show) {
    super(typeIn, color, show);
  }

  @Override
  public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
    cures.clear();
  }
}
