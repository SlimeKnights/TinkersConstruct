package slimeknights.tconstruct.library.modifiers.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

public abstract class DurabilityShieldModifier extends Modifier {
  @Override
  public Component getDisplayName(IToolStackView tool, int level) {
    return getDisplayName(level).copy()
                                .append(": " + getShield(tool) + " / " + getShieldCapacity(tool, level));
  }


  /* Tool building */

  @Override
  public ValidatedResult validate(IToolStackView tool, int level) {
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
  public void onRemoved(IToolStackView tool) {
    // remove all overslime on removal
    tool.getPersistentData().remove(getShieldKey());
  }


  /* Damaging */

  @Override
  public int onDamageTool(IToolStackView tool, int level, int amount, @Nullable LivingEntity holder) {
    int shield = getShield(tool);
    if (shield > 0) {
      // if we have more overslime than amount, remove some overslime
      if (shield >= amount) {
        setShield(tool, level, shield - amount);
        return 0;
      }
      // amount is more than overslime, reduce and clear overslime
      amount -= shield;
      setShield(tool, level, 0);
    }
    return amount;
  }

  @Override
  public double getDamagePercentage(IToolStackView tool, int level) {
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
  protected int getShield(IToolStackView tool) {
    return tool.getPersistentData().getInt(getShieldKey());
  }

  /** Gets the capacity of the shield for the given tool */
  protected abstract int getShieldCapacity(IToolStackView tool, int level);

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
  protected void setShield(IToolStackView tool, int level, int amount) {
    setShield(tool.getPersistentData(), Math.min(amount, getShieldCapacity(tool, level)));
  }

  /** Adds the given amount to the current shield */
  protected void addShield(IToolStackView tool, int level, int amount) {
    setShield(tool, level, amount + getShield(tool));
  }
}
