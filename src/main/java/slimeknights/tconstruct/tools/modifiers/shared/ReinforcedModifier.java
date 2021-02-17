package slimeknights.tconstruct.tools.modifiers.shared;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class ReinforcedModifier extends Modifier {
  public ReinforcedModifier() {
    super(0xcacaca);
  }

  @Override
  public int onDamageTool(IModifierToolStack toolStack, int level, int amount) {
    // vanilla formula, 100 / (level + 1), means 50% chance at level 1
    float chance = 1f / (level + 1f);
    if (chance < 1f) {
      int dealt = 0;
      // TODO: is there a closed form version of this?
      for (int i = 0; i < amount; i++) {
        if (RANDOM.nextFloat() < chance) {
          dealt++;
        }
      }
      return dealt;
    }
    return amount;
  }
}
