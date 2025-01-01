package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/** Modifier implementing trim behaviors */
public class TrimModifier extends NoLevelsModifier implements ModifierRemovalHook {
  private static final String FORMAT_KEY = TConstruct.makeTranslationKey("modifier", "trim.formatted");
  public static final ResourceLocation TRIM_PATTERN = TConstruct.getResource("trim_pattern");
  public static final ResourceLocation TRIM_MATERIAL = TConstruct.getResource("trim_material");

  /** Cache of styles for each material */
  private final Map<String,Component> formattedCache = new HashMap<>();

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    IModDataView modDataNBT = tool.getPersistentData();
    String trimMaterial = modDataNBT.getString(TRIM_MATERIAL);
    String trimPattern = modDataNBT.getString(TRIM_PATTERN);
    Component original = getDisplayName();
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

  // TODO: display name or tooltip?

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.REMOVE);
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(TRIM_PATTERN);
    tool.getPersistentData().remove(TRIM_MATERIAL);
    return null;
  }
}
