package slimeknights.tconstruct.library.modifiers.modules.display;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.build.SwappableSlotModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.recipe.modifiers.adding.SwappableModifierRecipe.VariantFormatter;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module for formatting the modifier variant name using a variant formatter
 * @see ModifierVariantColorModule
 */
public record ModifierVariantNameModule(VariantFormatter formatter) implements ModifierModule, DisplayNameModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ModifierVariantNameModule>defaultHooks(ModifierHooks.DISPLAY_NAME);
  public static final RecordLoadable<ModifierVariantNameModule> LOADER = RecordLoadable.create(
    VariantFormatter.LOADER.requiredField("formatter", ModifierVariantNameModule::formatter),
    ModifierVariantNameModule::new);

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
    String variant = tool.getPersistentData().getString(entry.getId());
    if (!variant.isEmpty()) {
      // allow overriding the color of the result using the resource color manager
      TextColor color = ResourceColorManager.getOrNull(entry.getModifier().getTranslationKey() + '.' + variant);
      Style style = name.getStyle();
      if (color != null) {
        style = style.withColor(color);
      }
      return Component.translatable(SwappableSlotModule.FORMAT, name.copy().withStyle(Style.EMPTY), formatter.format(entry.getId(), variant)).withStyle(style);
    }
    return name;
  }

  @Override
  public RecordLoadable<ModifierVariantNameModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
}
