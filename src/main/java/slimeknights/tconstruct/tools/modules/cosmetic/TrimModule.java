package slimeknights.tconstruct.tools.modules.cosmetic;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.NoFieldRecordLoadable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Creates a new trim module */
public class TrimModule implements ModifierModule, DisplayNameModifierHook, ModifierRemovalHook {
  private static final String FORMAT_KEY = TConstruct.makeTranslationKey("modifier", "trim.formatted");
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<TrimModule>defaultHooks(ModifierHooks.DISPLAY_NAME, ModifierHooks.REMOVE);
  public static final RecordLoadable<TrimModule> LOADER = new NoFieldRecordLoadable<>(TrimModule::new);

  /** Cache of styles for each material. */
  private final Map<String,Component> formattedCache = new HashMap<>();

  @Override
  public RecordLoadable<TrimModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
    IModDataView modDataNBT = tool.getPersistentData();
    ModifierId id = entry.getId();
    String trimMaterial = modDataNBT.getString(materialKey(id));
    String trimPattern = modDataNBT.getString(patternKey(id));
    // get the unformatted name
    Component original = entry.getModifier().getDisplayName();
    if (trimMaterial.isEmpty() || trimPattern.isEmpty()) {
      return original;
    }
    String key = trimMaterial + '#' + trimPattern;
    Component formatted = formattedCache.get(key);
    if (formatted == null) {
      if (access == null) {
        return original;
      }
      formatted = original;
      TrimMaterial material = access.registryOrThrow(Registries.TRIM_MATERIAL).get(ResourceLocation.tryParse(trimMaterial));
      TrimPattern pattern = access.registryOrThrow(Registries.TRIM_PATTERN).get(ResourceLocation.tryParse(trimPattern));
      if (material != null && pattern != null) {
        // format is "___ Armor Trim (___ Material)"
        formatted = Component.translatable(FORMAT_KEY, pattern.description(), material.description()).withStyle(material.description().getStyle());
      }
      formattedCache.put(trimMaterial, formatted);
    }
    return formatted;
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    ModifierId id = modifier.getId();
    tool.getPersistentData().remove(patternKey(id));
    tool.getPersistentData().remove(materialKey(id));
    return null;
  }


  /* Helpers */

  /** Gets the pattern key for the given modifier ID */
  public static ResourceLocation patternKey(ModifierId modifier) {
    return modifier.withSuffix("_pattern");
  }

  /** Gets the material key for the given modifier ID */
  public static ResourceLocation materialKey(ModifierId modifier) {
    return modifier.withSuffix("_material");
  }
}
