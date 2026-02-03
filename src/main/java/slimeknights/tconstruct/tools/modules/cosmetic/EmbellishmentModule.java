package slimeknights.tconstruct.tools.modules.cosmetic;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.RawDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;

import javax.annotation.Nullable;
import java.util.List;

/** Module implementing {@link slimeknights.tconstruct.tools.TinkerModifiers#embellishment}. */
public enum EmbellishmentModule implements ModifierModule, DisplayNameModifierHook, ModifierRemovalHook, RawDataModifierHook {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<EmbellishmentModule>defaultHooks(ModifierHooks.DISPLAY_NAME, ModifierHooks.REMOVE, ModifierHooks.RAW_DATA);
  public static final RecordLoadable<EmbellishmentModule> LOADER = new SingletonLoader<>(INSTANCE);

  @Override
  public RecordLoadable<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
    MaterialVariantId materialVariant = MaterialVariantId.tryParse(tool.getPersistentData().getString(entry.getId()));
    if (materialVariant != null) {
      return Component.translatable(entry.getModifier().getTranslationKey() + ".formatted", MaterialTooltipCache.getDisplayName(materialVariant)).withStyle(style -> style.withColor(MaterialTooltipCache.getColor(materialVariant)));
    }
    return name;
  }

  @Override
  public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {
    // on build, migrate material redirects
    ModDataNBT data = tool.getPersistentData();
    ResourceLocation key = modifier.getId();
    MaterialVariantId materialVariant = MaterialVariantId.tryParse(data.getString(key));
    if (materialVariant != null) {
      MaterialId original = materialVariant.getId();
      MaterialId resolved = MaterialRegistry.getInstance().resolve(original);
      // instance check is safe here as resolve returns same instance if no redirect
      if (resolved != original) {
        data.putString(key, MaterialVariantId.create(resolved, materialVariant.getVariant()).toString());
      }
    }
  }

  @Override
  public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {}

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(modifier.getId());
    return null;
  }
}
