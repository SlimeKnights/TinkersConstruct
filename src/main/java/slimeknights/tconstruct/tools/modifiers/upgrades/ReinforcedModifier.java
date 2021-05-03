package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.List;

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

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    float reinforced = 1 - 1f / (level + 1);
    tooltip.add(applyStyle(new StringTextComponent(Util.dfPercent.format(reinforced)).appendString(" ").append(makeDisplayName())));
  }
}
