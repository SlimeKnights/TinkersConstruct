package slimeknights.tconstruct.tools.modifiers.traits;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.modifiers.free.OverslimeModifier;

public class OvercastModifier extends Modifier {
  public OvercastModifier() {
    super(0x74c8c7);
  }

  @Override
  public void addVolatileData(IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    volatileData.putBoolean(OverslimeModifier.KEY_OVERSLIME_FRIEND, true);
  }

  @Override
  public int getPriority() {
    // greater than overslime
    return 200;
  }

  @Override
  public int onDamage(IModifierToolStack toolStack, int level, int amount) {
    int overslime = OverslimeModifier.getOverslime(toolStack);
    if (overslime > 0) {
      // bit better than vanilla, 80 / (level + 1), means 40% chance of damage at level 1
      float chance = 0.8f / (level + 1f);
      int dealt = 0;
      for (int i = amount - 1; i >= 0; i--) {
        if (RANDOM.nextFloat() < chance) {
          dealt++;
          // if we run out of overslime, stop. we only protect overslime
          if (dealt >= overslime) {
            // overslime will damage overslime dealt, the remaining i is passed to other modifiers
            return i + dealt;
          }
        }
      }

      // finished the loop with overslime to spare, pass the amount that got through
      return dealt;
    }

    // no overslime, no protection
    return amount;
  }
}
