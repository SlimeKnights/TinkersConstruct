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
import slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.CapacityBarHook;
import slimeknights.tconstruct.library.modifiers.modules.capacity.CapacityBarModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.CapacityBarValidator;
import slimeknights.tconstruct.library.modifiers.modules.capacity.DurabilityShieldModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

/** @deprecated use {@link DurabilityShieldModule} with {@link CapacityBarHook} */
@Deprecated
public abstract class DurabilityShieldModifier extends Modifier implements CapacityBarHook, ToolDamageModifierHook, DurabilityDisplayModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(new CapacityBarValidator(this));
    hookBuilder.addHook(this, ModifierHooks.TOOL_DAMAGE, ModifierHooks.DURABILITY_DISPLAY, ModifierHooks.CAPACITY_BAR);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    return IncrementalModifierEntry.addAmountToName(getDisplayName(entry.getLevel()), getAmount(tool), getCapacity(tool, entry));
  }


  /* Damaging */

  @Override
  public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
    return DurabilityShieldModule.onDamageTool(this, tool, modifier, amount);
  }

  @Override
  public int getDurabilityWidth(IToolStackView tool, ModifierEntry modifier) {
    int shield = getAmount(tool);
    if (shield > 0) {
      return DurabilityDisplayModifierHook.getWidthFor(shield, getCapacity(tool, modifier));
    }
    return 0;
  }


  /* Helpers */

  /** Gets the key to use for teh shield */
  protected ResourceLocation getShieldKey() {
    return getId();
  }

  /** @deprecated use {@link #getAmount(IToolStackView)} */
  @Deprecated
  public int getShield(IToolStackView tool) {
    return tool.getPersistentData().getInt(getShieldKey());
  }

  @Override
  public int getAmount(IToolStackView tool) {
    return getShield(tool);
  }

  @Override
  public int getCapacity(IToolStackView tool, ModifierEntry entry) {
    return getShieldCapacity(tool, entry);
  }

  /**
   * Gets the capacity of the shield for the given tool.
   * @deprecated use {@link CapacityBarHook#getCapacity(IToolStackView, ModifierEntry)}. Overriding is okay though you really should migrate to {@link CapacityBarModule} or alike.
   */
  @Deprecated
  public abstract int getShieldCapacity(IToolStackView tool, ModifierEntry modifier);

  /**
   * Sets the shield, bypassing the capacity
   * @param persistentData  Persistent data
   * @param amount          Amount to set
   * @deprecated use {@link OverslimeModule#setAmountRaw(ModDataNBT, int)}. For non-overslime usages, this currently has no migration.
   */
  @Deprecated
  public void setShield(ModDataNBT persistentData, int amount) {
    persistentData.putInt(getShieldKey(), Math.max(amount, 0));
  }

  /**
   * Sets the shield on a tool.
   * @deprecated use {@link #setAmount(IToolStackView, ModifierEntry, int)}
   */
  @Deprecated
  public void setShield(IToolStackView tool, ModifierEntry modifier, int amount) {
    setShield(tool.getPersistentData(), Math.min(amount, getShieldCapacity(tool, modifier)));
  }

  @Override
  public void setAmount(IToolStackView tool, ModifierEntry entry, int amount) {
    setShield(tool, entry, amount);
  }

  /**
   * Adds the given amount to the current shield.
   * @deprecated use {@link #addAmount(IToolStackView, ModifierEntry, int)}
   */
  @Deprecated
  protected void addShield(IToolStackView tool, ModifierEntry modifier, int amount) {
    setShield(tool, modifier, amount + getAmount(tool));
  }
}
