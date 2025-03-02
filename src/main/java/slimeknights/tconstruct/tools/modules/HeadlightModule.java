package slimeknights.tconstruct.tools.modules;

import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.RawDataModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;

import java.util.List;

/** Module implementing headlight on helmets */
public record HeadlightModule(int defaultLight) implements ModifierModule, RawDataModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<HeadlightModule>defaultHooks(ModifierHooks.RAW_DATA);
  private static final String HEADLIGHT_LIGHT = "headlight_level";
  public static final RecordLoadable<HeadlightModule> LOADER = RecordLoadable.create(
    IntLoadable.range(1, 15).requiredField("default_light", HeadlightModule::defaultLight),
    HeadlightModule::new);

  @Override
  public RecordLoadable<HeadlightModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {
    String lightStr = tool.getPersistentData().getString(modifier.getId());
    if (!lightStr.isEmpty()) {
      try {
        tag.putInt(HEADLIGHT_LIGHT, Integer.parseInt(lightStr));
        return;
      } catch (NumberFormatException ex) {
        // NO-OP
      }
    }
    tag.putInt(HEADLIGHT_LIGHT, defaultLight);
  }

  @Override
  public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {
    tag.remove(HEADLIGHT_LIGHT);
  }
}
