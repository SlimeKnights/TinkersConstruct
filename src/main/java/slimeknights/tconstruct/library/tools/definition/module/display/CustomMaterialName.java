package slimeknights.tconstruct.library.tools.definition.module.display;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Translates a custom format key for the given material
 * @param index   Material index to fetch
 * @param suffix  Translation key suffix to apply to the material name.
 */
public record CustomMaterialName(int index, String suffix) implements ToolNameHook.FromDefault, ToolModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<CustomMaterialName>defaultHooks(ToolHooks.DISPLAY_NAME);
  /** Loader instance */
  public static final RecordLoadable<CustomMaterialName> LOADER = RecordLoadable.create(
    IntLoadable.FROM_ZERO.requiredField("index", CustomMaterialName::index),
    StringLoadable.DEFAULT.requiredField("suffix", CustomMaterialName::suffix),
    CustomMaterialName::new);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<CustomMaterialName> getLoader() {
    return LOADER;
  }

  @Override
  public Component getDisplayName(ToolDefinition definition, ItemStack stack, @Nullable IToolStackView tool, Component itemName) {
    MaterialVariantId material = ToolNameHook.getTool(stack, tool).getMaterials().get(index).getVariant();
    if (IMaterial.UNKNOWN_ID.equals(material)) {
      return itemName;
    }
    // translate the suffixed key
    Component component;
    find: {
      // first, try the material directly
      String materialKey = MaterialTooltipCache.getKey(material) + '.' + suffix;
      if (Util.canTranslate(materialKey)) {
        component = Component.translatable(materialKey);
        break find;
      }
      // if that did not work, do base material
      if (material.hasVariant()) {
        materialKey = MaterialTooltipCache.getKey(material.getId()) + '.' + suffix;
        if (Util.canTranslate(materialKey)) {
          component = Component.translatable(materialKey);
          break find;
        }
      }
      // if both failed, use the regular key
      component = MaterialTooltipCache.getDisplayName(material);
    }
    return Component.translatable(TooltipUtil.KEY_FORMAT, component, itemName);
  }
}
