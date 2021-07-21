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

  /**
   * Gets the reinforcment percentage for the given level
   * @param level  Level from 0 to 10
   * @return  Percentage
   */
  private static float getPercentage(float level) {
    // formula gives 25%, 45%, 60%, 70%, 75% for first 5 levels
    if (level < 5) {
      return 0.025f * level * (11 - level);
    }
    // after level 5.5 the above formula breaks, so just do +5% per level
    // means for levels 6 to 10, you get 80%, 85%, 90%, 95%, 100%
    // in default config we never go past level 5, but nice for datapacks to allow
    return 0.75f + (level - 5) * 0.05f;
  }

  @Override
  public int onDamageTool(IModifierToolStack tool, int level, int amount) {
    // vanilla formula, 100 / (level + 1), means 50% chance at level 1
    float percentage = getPercentage(getScaledLevel(tool, level));
    // 100% protection? all damage blocked
    if (percentage >= 1) {
      return 0;
    }
    // 0% protection? nothing blocked
    if (percentage <= 0) {
      return amount;
    }
    // no easy closed form formula for this that I know of, and damage amount tends to be small, so take a chance for each durability
    int dealt = 0;
    for (int i = 0; i < amount; i++) {
      if (RANDOM.nextFloat() >= percentage) {
        dealt++;
      }
    }
    return dealt;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    float reinforced;
    if (tool.getModifierLevel(TinkerModifiers.unbreakable.get()) > 0) {
      reinforced = 1;
    } else {
      reinforced = getPercentage(getScaledLevel(tool, level));
    }
    tooltip.add(applyStyle(new StringTextComponent(Util.PERCENT_FORMAT.format(reinforced)).appendString(" ").appendSibling(makeDisplayName())));
  }
}
