package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.ToolEnergyCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/**
 * Modifier handling any energy hooks that need not be run multiple times.
 * @apiNote There is no need to directly use this modifier; just add it as a modifier trait to your energy modifier using {@link ToolEnergyCapability#ENERGY_HANDLER}.
 */
@Internal
public final class EnergyHandlerModifier extends NoLevelsModifier implements ValidateModifierHook, ModifierRemovalHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.VALIDATE, ModifierHooks.REMOVE);
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return false;
  }

  @Override
  public int getPriority() {
    // low priority so this runs after other modifiers
    return 10;
  }

  @Nullable
  @Override
  public Component validate(IToolStackView tool, ModifierEntry modifier) {
    ToolEnergyCapability.checkEnergy(tool);
    return null;
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    // we could just remove the energy, but this is less prone to risk of another modifier using energy
    // this will remove the energy if the capacity became 0 which should happen when your last energy modifier is removed
    ToolEnergyCapability.checkEnergy(tool);
    return null;
  }
}
