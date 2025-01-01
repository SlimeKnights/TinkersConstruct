package slimeknights.tconstruct.library.modifiers.impl;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.IncrementalModifierEntry;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

public abstract class DurabilityShieldModifier extends Modifier implements ToolDamageModifierHook, ValidateModifierHook, ModifierRemovalHook, DurabilityDisplayModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.TOOL_DAMAGE, ModifierHooks.VALIDATE, ModifierHooks.REMOVE, ModifierHooks.DURABILITY_DISPLAY);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    return IncrementalModifierEntry.addAmountToName(getDisplayName(entry.getLevel()), getShield(tool), getShieldCapacity(tool, entry));
  }


  /* Tool building */

  @Nullable
  @Override
  public Component validate(IToolStackView tool, ModifierEntry modifier) {
    // clear excess overslime
    int cap = getShieldCapacity(tool, modifier);
    if (getShield(tool) > cap) {
      setShield(tool.getPersistentData(), cap);
    }
    return null;
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    // remove all overslime on removal
    tool.getPersistentData().remove(getShieldKey());
    return null;
  }


  /* Damaging */

  @Override
  public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
    int shield = getShield(tool);
    if (shield > 0) {
      // if we have more overslime than amount, remove some overslime
      if (shield >= amount) {
        setShield(tool, modifier, shield - amount);
        return 0;
      }
      // amount is more than overslime, reduce and clear overslime
      amount -= shield;
      setShield(tool, modifier, 0);
    }
    return amount;
  }

  @Override
  public int getDurabilityWidth(IToolStackView tool, ModifierEntry modifier) {
    int shield = getShield(tool);
    if (shield > 0) {
      return DurabilityDisplayModifierHook.getWidthFor(shield, getShieldCapacity(tool, modifier));
    }
    return 0;
  }


  /* Helpers */

  /** Gets the key to use for teh shield */
  protected ResourceLocation getShieldKey() {
    return getId();
  }

  /** Gets the current shield amount */
  public int getShield(IToolStackView tool) {
    return tool.getPersistentData().getInt(getShieldKey());
  }

  /** Gets the capacity of the shield for the given tool */
  public abstract int getShieldCapacity(IToolStackView tool, ModifierEntry modifier);

  /**
   * Sets the shield, bypassing the capacity
   * @param persistentData  Persistent data
   * @param amount          Amount to set
   */
  public void setShield(ModDataNBT persistentData, int amount) {
    persistentData.putInt(getShieldKey(), Math.max(amount, 0));
  }

  /**
   * Sets the shield on a tool
   */
  public void setShield(IToolStackView tool, ModifierEntry modifier, int amount) {
    setShield(tool.getPersistentData(), Math.min(amount, getShieldCapacity(tool, modifier)));
  }

  /** Adds the given amount to the current shield */
  protected void addShield(IToolStackView tool, ModifierEntry modifier, int amount) {
    setShield(tool, modifier, amount + getShield(tool));
  }
}
