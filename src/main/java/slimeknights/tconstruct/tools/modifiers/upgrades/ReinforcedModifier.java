package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.List;

public class ReinforcedModifier extends IncrementalModifier {
  public ReinforcedModifier() {
    super(0xcacaca);
  }

  @Override
  public int onDamageTool(IModifierToolStack tool, int level, int amount) {
    // vanilla formula, 100 / (level + 1), means 50% chance at level 1
    float chance = 1f / (getScaledLevel(tool, level) + 1f);
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
    float reinforced;
    if (tool.getModifierLevel(TinkerModifiers.unbreakable.get()) > 0) {
      reinforced = 1;
    } else {
      reinforced = 1 - 1f / (getScaledLevel(tool, level) + 1);
    }
    tooltip.add(applyStyle(new StringTextComponent(Util.PERCENT_FORMAT.format(reinforced)).appendString(" ").appendSibling(makeDisplayName())));
  }
}
