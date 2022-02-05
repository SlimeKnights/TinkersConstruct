package slimeknights.tconstruct.tools.modifiers.traits.general;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class CultivatedModifier extends Modifier {
  @Override
  public float getRepairFactor(IToolStackView toolStack, int level, float factor) {
    // +50% repair per level
    return (factor * (1 + (level * 0.5f)));
  }
}
