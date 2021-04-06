package slimeknights.tconstruct.tools.modifiers.traits;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class CultivatedModifier extends Modifier {
  public CultivatedModifier() {
    super(0x8e661b);
  }

  @Override
  public float getRepairFactor(IModifierToolStack toolStack, int level, float factor) {
    // +25% repair per level
    return (factor * (1 + (level * 0.25f)));
  }
}
