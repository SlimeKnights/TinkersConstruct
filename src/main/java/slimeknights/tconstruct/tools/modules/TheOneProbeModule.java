package slimeknights.tconstruct.tools.modules;

import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.RawDataModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;

import java.util.List;

/** Module implementing the one probe on held tools and helmets */
public enum TheOneProbeModule implements ModifierModule, RawDataModifierHook {
  INSTANCE;

  private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierModule.<TheOneProbeModule>defaultHooks(TinkerHooks.RAW_DATA);
  public static final IGenericLoader<TheOneProbeModule> LOADER = new SingletonLoader<>(INSTANCE);
  public static final String TOP_NBT_HELMET = "theoneprobe";
  public static final String TOP_NBT_HAND = "theoneprobe_hand";

  @Override
  public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {
    if (tool.hasTag(TinkerTags.Items.HELD)) {
      tag.putBoolean(TOP_NBT_HAND, true);
    }
    if (tool.hasTag(TinkerTags.Items.HELMETS)) {
      tag.putBoolean(TOP_NBT_HELMET, true);
    }
  }

  @Override
  public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {
    tag.remove(TOP_NBT_HAND);
    tag.remove(TOP_NBT_HELMET);
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }
}
