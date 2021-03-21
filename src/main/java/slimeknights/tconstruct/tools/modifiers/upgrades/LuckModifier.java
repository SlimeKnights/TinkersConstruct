package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipeLookup;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.function.BiConsumer;

public class LuckModifier extends IncrementalModifier {
  public LuckModifier() {
    super(0x345EC3);
  }

  @Override
  public ITextComponent getDisplayName(int level) {
    IFormattableTextComponent name;
    if (level > 3) {
      name = new TranslationTextComponent(getTranslationKey() + ".beyond");
    } else {
      name = new TranslationTextComponent(getTranslationKey() + "." + level);
    }
    return name.modifyStyle(style -> style.setColor(Color.fromInt(getColor())));
  }

  @Override
  public ITextComponent getDisplayName(IModifierToolStack tool, int level) {
    int neededPerLevel = ModifierRecipeLookup.getNeededPerLevel(this);
    ITextComponent name = this.getDisplayName(level);
    if (neededPerLevel > 0) {
      // display the numeral based on percentage into the level
      int amount = getAmount(tool);
      int dispLevel = 3 + ((amount - neededPerLevel) * 3 / neededPerLevel);
      // we only have names for 1 to 4, so display 5 and onwards as 4 with a higher number
      if (level > 4) {
        dispLevel += (level - 4) * 3;
      }
      // finally build the string
      IFormattableTextComponent formattable = name.deepCopy();
      formattable = formattable.appendString(" ").append(new TranslationTextComponent(KEY_LEVEL + dispLevel));
      // if not at a full level, add that info too
      if (amount < neededPerLevel) {
        return formattable.appendString(": " + amount + " / " + neededPerLevel);
      }
      return formattable;
    }
    return name;
  }

  @Override
  public void applyEnchantments(IModifierToolStack tool, int level, BiConsumer<Enchantment,Integer> consumer) {
    // each level of the modifier is worth 3 levels of the enchant
    int applyLevel = level * 3;
    int neededPerSlot = ModifierRecipeLookup.getNeededPerLevel(this);
    if (neededPerSlot > 0) {
      // if we just have a partial amount, every third is worth 1 level
      int neededPerLevel = neededPerSlot / 3;
      int amount = getAmount(tool);

      // 0 to 33%: lose 2 levels
      if (amount < neededPerLevel) {
        applyLevel -= 2;
      } else {
        // rescale to be from 0 to 67%
        amount -= neededPerLevel;
        // 33% to 66%: lose 1 level
        if (amount < neededPerLevel) {
          applyLevel--;
        } else {
          // 66% to 100%, further rescale amount
          amount -= neededPerLevel;
        }
      }

      // for the remainder, if we don't have a full level left decrease it
      if (amount < neededPerLevel && amount < RANDOM.nextInt(neededPerLevel)) {
        applyLevel--;
      }
    }
    // apply level if we still have any
    if (applyLevel > 0) {
      consumer.accept(Enchantments.LOOTING, applyLevel);
      consumer.accept(Enchantments.FORTUNE, applyLevel);
    }
  }
}
