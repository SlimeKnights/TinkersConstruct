package slimeknights.tconstruct.library.modifiers.modules.build;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.loader.ResourceLocationLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.List;

/**
 * Module that just sets a boolean flag to true on a tool
 */
public record VolatileFlagModule(ResourceLocation flag) implements VolatileDataModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.VOLATILE_DATA);
  public static final IGenericLoader<VolatileFlagModule> LOADER = new ResourceLocationLoader<>("flag", VolatileFlagModule::new, VolatileFlagModule::flag);

  @Override
  public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    volatileData.putBoolean(flag, true);
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
}
