package slimeknights.tconstruct.library.tools.definition.module.display;

import slimeknights.mantle.data.loadable.mapping.SimpleRecordLoadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;

import java.util.List;

/** Module setting the tool display name to a filtered list of material stat types */
public enum MaterialToolNameModule implements MaterialToolName, ToolModule {
  ALL {
    @Override
    public boolean shouldDisplayMaterial(int index, MaterialStatsId statType, MaterialVariantId material) {
      return true;
    }
  },
  REPAIRABLE {
    @Override
    public boolean shouldDisplayMaterial(int index, MaterialStatsId statType, MaterialVariantId material) {
      return MaterialRegistry.getInstance().canRepair(statType);
    }
  };
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MaterialToolNameModule>defaultHooks(ToolHooks.DISPLAY_NAME);
  public static final RecordLoadable<MaterialToolNameModule> LOADER = new SimpleRecordLoadable<>(new EnumLoadable<>(MaterialToolNameModule.class), "filter", null, false);

  @Override
  public RecordLoadable<MaterialToolNameModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
}
