package slimeknights.tconstruct.library.modifiers;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.List;

/** Modifier which can take just part of an input instead of the whole input */
public class IncrementalModifier extends Modifier {
  public IncrementalModifier(int color) {
    super(color);
  }

  @Override
  public ITextComponent getDisplayName(IModifierToolStack tool, int level) {
    int neededPerLevel = ModifierRecipeLookup.getNeededPerLevel(this);
    ITextComponent name = this.getDisplayName(level);
    if (neededPerLevel > 0) {
      int amount = getAmount(tool);
      if (amount < neededPerLevel) {
        return name.deepCopy().appendString(": " + amount + " / " + neededPerLevel);
      }
    }
    return name;
  }

  @Override
  public void onRemoved(IModifierToolStack tool) {
    // remove current progress in incremental modifiers
    tool.getPersistentData().remove(getId());
  }

  /* Helpers */

  /**
   * Gets the amount of value applied to the tool thus far
   * @param persistentData  Tool persistent mod NBT
   * @param modifier        Modifier instance
   * @return  Amount applied to the tool
   */
  public static int getAmount(IModDataReadOnly persistentData, Modifier modifier) {
    if (persistentData.contains(modifier.getId(), NBT.TAG_ANY_NUMERIC)) {
      return persistentData.getInt(modifier.getId());
    }
    return ModifierRecipeLookup.getNeededPerLevel(modifier);
  }

  /**
   * Gets the amount of value applied to the tool thus far
   * @param tool      Tool instance
   * @param modifier  Modifier instance
   * @return  Amount applied to the tool
   */
  public static int getAmount(IModifierToolStack tool, Modifier modifier) {
    return getAmount(tool.getPersistentData(), modifier);
  }

  /**
   * Gets the amount of this modifier on the tool
   * @param persistentData  Tool persistent mod NBT
   * @return  Amount
   */
  public int getAmount(IModDataReadOnly persistentData) {
    return getAmount(persistentData, this);
  }

  /**
   * Gets the amount of this modifier on the tool
   * @param tool  Tool amount
   * @return  Amount
   */
  public int getAmount(IModifierToolStack tool) {
    return getAmount(tool, this);
  }

  /**
   * Gets the level scaled based on the current amount into the level
   * @param persistentData  Tool persistent mod NBT
   * @param level  Modifier level
   * @return  Level, possibly reduced by an incomplete level
   */
  public float getScaledLevel(IModDataReadOnly persistentData, int level) {
    if (level <= 0) {
      return 0;
    }
    int neededPerLevel = ModifierRecipeLookup.getNeededPerLevel(this);
    if (neededPerLevel > 0) {
      // if amount == needed per level, returns level
      // if amount == 0, returns level - 1, otherwise returns some fractional amount
      return level + (getAmount(persistentData) - neededPerLevel) / (float)neededPerLevel;
    }
    return level;
  }

  /**
   * Gets the level scaled based on the current amount into the level
   * @param tool   Tool instance
   * @param level  Modifier level
   * @return  Level, possibly reduced by an incomplete level
   */
  public float getScaledLevel(IModifierToolStack tool, int level) {
    return getScaledLevel(tool.getPersistentData(), level);
  }

  /**
   * Sets the amount on the tool
   * @param persistentData  Tool NBT
   * @param modifier        Modifier to set
   * @param amount          New amount
   */
  public static void setAmount(ModDataNBT persistentData, Modifier modifier, int amount) {
    persistentData.putInt(modifier.getId(), amount);
  }

  /**
   * Adds a tooltip showing the bonus damage and the type of damage dded
   * @param tool         Tool instance
   * @param level        Current level
   * @param levelAmount  Bonus per level
   * @param tooltip      Tooltip
   */
  protected void addDamageTooltip(IModifierToolStack tool, int level, float levelAmount, List<ITextComponent> tooltip) {
    addDamageTooltip(tool, getScaledLevel(tool, level) * levelAmount, tooltip);
  }
}
