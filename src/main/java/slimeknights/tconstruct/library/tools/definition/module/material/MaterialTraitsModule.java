package slimeknights.tconstruct.library.tools.definition.module.material;

import lombok.AllArgsConstructor;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolTraitHook;
import slimeknights.tconstruct.library.tools.helper.ModifierBuilder;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module to add a single trait to the given tool.
 * Generally it's better to use {@link MaterialStatsModule} as it will add all traits to the tool. This module is used in some special circumstances.
 */
@AllArgsConstructor
public final class MaterialTraitsModule implements ToolTraitHook, ToolModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MaterialTraitsModule>defaultHooks(ToolHooks.TOOL_TRAITS);
  private static final MaterialStatsId MISSING = new MaterialStatsId(TConstruct.MOD_ID, "missingno");
  public static final RecordLoadable<MaterialTraitsModule> LOADER = RecordLoadable.create(
    MaterialStatsId.PARSER.nullableField("stat_type", m -> m.statType),
    IntLoadable.FROM_ZERO.requiredField("material_index", m -> m.materialIndex),
    MaterialTraitsModule::new);

  @Nullable
  private MaterialStatsId statType;
  private final int materialIndex;

  public MaterialTraitsModule(MaterialStatType<?> statType, int materialIndex) {
    this(statType.getId(), materialIndex);
  }

  public MaterialTraitsModule(int materialIndex) {
    this((MaterialStatsId)null, materialIndex);
  }

  @Override
  public RecordLoadable<MaterialTraitsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Gets the stat type given the definition */
  private MaterialStatsId getStatType(ToolDefinition definition) {
    if (statType == null) {
      // we can cache it given this module lives in the definition, so its not going to be given a different definition later
      List<MaterialStatsId> statTypes = ToolMaterialHook.stats(definition);
      if (materialIndex < statTypes.size()) {
        statType = statTypes.get(materialIndex);
      } else {
        TConstruct.LOG.error("Invalid material index {} for {} traits module, total materials {}", materialIndex, definition.getId(), statTypes.size());
        statType = MISSING;
      }
    }
    return statType;
  }

  @Override
  public void addTraits(ToolDefinition definition, MaterialNBT materials, ModifierBuilder builder) {
    MaterialVariant material = materials.get(materialIndex);
    if (!material.isUnknown()) {
      MaterialStatsId statsId = getStatType(definition);
      if (!MISSING.equals(statsId)) {
        builder.add(MaterialRegistry.getInstance().getTraits(material.getId(), getStatType(definition)));
      }
    }
  }
}
