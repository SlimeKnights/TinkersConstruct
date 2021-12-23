package slimeknights.tconstruct.library.modifiers;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public abstract class DurabilityShieldModifier extends Modifier {
  public DurabilityShieldModifier(int color) {
    super(color);
  }

  @Override
  public ITextComponent getDisplayName(IModifierToolStack tool, int level) {
    return getDisplayName(level).deepCopy()
                                .appendString(": " + getShield(tool) + " / " + getShieldCapacity(tool, level));
  }


  /* Tool building */

  @Override
  public ValidatedResult validate(IModifierToolStack tool, int level) {
    // clear excess overslime
    if (level > 0) {
      int cap = getShieldCapacity(tool, level);
      if (getShield(tool) > cap) {
        setShield(tool.getPersistentData(), cap);
      }
    }
    return ValidatedResult.PASS;
  }

  @Override
  public void onRemoved(IModifierToolStack tool) {
    // remove all overslime on removal
    tool.getPersistentData().remove(getShieldKey());
  }


  /* Damaging */

  @Override
  public int onDamageTool(IModifierToolStack toolStack, int level, int amount) {
    int shield = getShield(toolStack);
    if (shield > 0) {
      // if we have more overslime than amount, remove some overslime
      if (shield >= amount) {
        setShield(toolStack, level, shield - amount);
        return 0;
      }
      // amount is more than overslime, reduce and clear overslime
      amount -= shield;
      setShield(toolStack, level, 0);
    }
    return amount;
  }

  @Override
  public double getDamagePercentage(IModifierToolStack tool, int level) {
    int shield = getShield(tool);
    if (shield > 0) {
      int cap = getShieldCapacity(tool, level);
      if (shield > cap) {
        return 0;
      }
      return ((double) (cap - shield) / cap);
    }
    return Double.NaN;
  }


  /* Helpers */

  /** Gets the key to use for teh shield */
  protected ResourceLocation getShieldKey() {
    return getId();
  }

  /** Gets the current shield amount */
  protected int getShield(IModifierToolStack tool) {
    return tool.getPersistentData().getInt(getShieldKey());
  }

  /** Gets the capacity of the shield for the given tool */
  protected abstract int getShieldCapacity(IModifierToolStack tool, int level);

  /**
   * Sets the shield, bypassing the capacity
   * @param persistentData  Persistent data
   * @param amount          Amount to set
   */
  protected void setShield(ModDataNBT persistentData, int amount) {
    persistentData.putInt(getShieldKey(), Math.max(amount, 0));
  }

  /**
   * Sets the shield on a tool
   */
  protected void setShield(IModifierToolStack tool, int level, int amount) {
    setShield(tool.getPersistentData(), Math.min(amount, getShieldCapacity(tool, level)));
  }

  /** Adds the given amount to the current shield */
  protected void addShield(IModifierToolStack tool, int level, int amount) {
    setShield(tool, level, amount + getShield(tool));
  }
}
