package slimeknights.tconstruct.library.tools.definition.module.display;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;

import java.util.List;
import java.util.Set;

/** Material tool display name showing any material with a stat type in a set */
public record StatTypesToolNameModule(Set<MaterialStatsId> statTypes) implements MaterialToolName, ToolModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<StatTypesToolNameModule>defaultHooks(ToolHooks.DISPLAY_NAME);
  public static final RecordLoadable<StatTypesToolNameModule> LOADER = RecordLoadable.create(MaterialStatsId.PARSER.set(1).requiredField("stat_types", StatTypesToolNameModule::statTypes), StatTypesToolNameModule::new);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<StatTypesToolNameModule> getLoader() {
    return LOADER;
  }

  @Override
  public boolean shouldDisplayMaterial(int index, MaterialStatsId statType, MaterialVariantId material) {
    return statTypes.contains(statType);
  }
}
