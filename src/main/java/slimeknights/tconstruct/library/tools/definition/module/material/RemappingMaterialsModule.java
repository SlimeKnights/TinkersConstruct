package slimeknights.tconstruct.library.tools.definition.module.material;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.RandomSource;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Module that fills in missing materials, remapping existing materials.
 * Note that due to how the missing materials hook works, this will only run if the material size was too small before.
 * This does not currently support remapping without a material size change.
 * @param materials  Data to fill in missing materials.
 * @param remap      Data to remap existing materials.
 */
public record RemappingMaterialsModule(List<RandomMaterial> materials, List<Map<MaterialId, MaterialVariantId>> remap) implements MissingMaterialsToolHook, ToolModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.defaultHooks(ToolHooks.MISSING_MATERIALS);
  /** Loader instance */
  public static final RecordLoadable<RemappingMaterialsModule> LOADER = RecordLoadable.create(
    RandomMaterial.LOADER.list(1).requiredField("missing", m -> m.materials),
    MaterialId.PARSER.mapWithValues(MaterialVariantId.LOADABLE, 0).list(1).requiredField("remap", m -> m.remap),
    RemappingMaterialsModule::new);

  @Override
  public RecordLoadable<RemappingMaterialsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public MaterialNBT fillMaterials(ToolDefinition definition, RandomSource random) {
    return RandomMaterial.build(ToolMaterialHook.stats(definition), materials, random);
  }

  @Override
  public MaterialNBT fillMaterials(ToolDefinition definition, MaterialNBT existing, RandomSource random) {
    MaterialNBT newMaterials = fillMaterials(definition, random);
    int oldSize = existing.size();
    // no existing materials, return the full new ones
    if (oldSize == 0) {
      return newMaterials;
    }
    // new materials is larger, copy all the ones from the smaller to fill in the gaps
    int newSize = newMaterials.size();
    List<MaterialVariant> materials = new ArrayList<>(newSize);
    for (int i = 0; i < oldSize; i++) {
      MaterialVariant material = existing.get(i);
      // if this index is remapping, remap matches
      if (i < this.remap.size()) {
        Map<MaterialId,MaterialVariantId> remap = this.remap.get(i);
        if (!remap.isEmpty()) {
          MaterialVariantId replacement = remap.get(material.getId());
          if (replacement != null) {
            material = MaterialVariant.of(replacement);
          }
        }
      }
      materials.add(material);
    }
    for (int i = oldSize; i < newSize; i++) {
      materials.add(newMaterials.get(i));
    }
    return new MaterialNBT(materials);
  }


  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final ImmutableList.Builder<RandomMaterial> materials = ImmutableList.builder();
    private final ImmutableList.Builder<Map<MaterialId,MaterialVariantId>> remap = ImmutableList.builder();

    private Builder() {}

    /** Adds a material to the builder */
    public Builder material(RandomMaterial material) {
      this.materials.add(material);
      return this;
    }

    /** Adds a material to the builder */
    public Builder material(RandomMaterial... materials) {
      for (RandomMaterial material : materials) {
        material(material);
      }
      return this;
    }

    /** Adds a material to the builder */
    public Builder material(MaterialVariantId material) {
      return material(RandomMaterial.fixed(material));
    }

    /** Starts a remap builder */
    public Remap remap() {
      return new Remap();
    }

    /** Builds the final module */
    public RemappingMaterialsModule build() {
      List<RandomMaterial> materials = this.materials.build();
      List<Map<MaterialId,MaterialVariantId>> remap = this.remap.build();
      if (materials.isEmpty()) {
        throw new IllegalArgumentException("Must have at least 1 material");
      }
      if (remap.isEmpty()) {
        throw new IllegalArgumentException("Must have at least 1 remap. For 0, use DefaultMaterialsModule");
      }
      return new RemappingMaterialsModule(materials, remap);
    }

    /** Nested builder for the map */
    public class Remap {
      private final ImmutableMap.Builder<MaterialId,MaterialVariantId> remap = ImmutableMap.builder();

      /** Adds the given material to the remapping */
      public Remap add(MaterialId material, MaterialVariantId replacement) {
        remap.put(material, replacement);
        return this;
      }

      /** Finishes the nested builder and returns to the main one */
      public Builder end() {
        Builder.this.remap.add(remap.build());
        return Builder.this;
      }
    }
  }
}
