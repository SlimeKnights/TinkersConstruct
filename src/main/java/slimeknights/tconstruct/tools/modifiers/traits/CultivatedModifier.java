package slimeknights.tconstruct.tools.modifiers.traits;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class CultivatedModifier extends Modifier {
  public CultivatedModifier() {
    super(0x8e661b);
  }

  @Override
  public int onRepairTool(IModifierToolStack toolStack, int level, int amount) {
    // +25% repair per level
    return (int)(amount * (1 + (level * 0.25f)));
  }
}
