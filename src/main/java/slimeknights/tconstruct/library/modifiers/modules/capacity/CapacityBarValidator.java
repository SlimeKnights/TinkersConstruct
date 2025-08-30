package slimeknights.tconstruct.library.modifiers.modules.capacity;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.modifiers.IncrementalModifierEntry;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.CapacityBarHook;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/** Implements cleanup and removal of capacity bars, used mainly for durability bars */
public record CapacityBarValidator(CapacityBarHook bar) implements HookProvider, DisplayNameModifierHook, ValidateModifierHook, ModifierRemovalHook {
  private static final List<ModuleHook<?>> HOOKS = HookProvider.<CapacityBarValidator>defaultHooks(ModifierHooks.DISPLAY_NAME, ModifierHooks.VALIDATE, ModifierHooks.REMOVE);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return HOOKS;
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
    return IncrementalModifierEntry.addAmountToName(entry.getModifier().getDisplayName(entry.getLevel()), bar.getAmount(tool), bar.getCapacity(tool, entry));
  }

  @Nullable
  @Override
  public Component validate(IToolStackView tool, ModifierEntry modifier) {
    // clear excess amount
    int cap = bar.getCapacity(tool, modifier);
    if (bar.getCapacity(tool, modifier) > cap) {
      bar.setAmount(tool, modifier, cap);
    }
    return null;
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    bar.setAmount(tool, ModifierEntry.EMPTY, 0);
    return null;
  }
}
