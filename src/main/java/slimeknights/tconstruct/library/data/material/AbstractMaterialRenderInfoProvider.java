package slimeknights.tconstruct.library.data.material;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoJson;
import slimeknights.tconstruct.library.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

import java.util.HashMap;
import java.util.Map;

/** Base data generator for use in addons */
public abstract class AbstractMaterialRenderInfoProvider extends GenericDataProvider {
  /** Map of material ID to builder, there is at most one builder for each ID */
  private final Map<MaterialId,RenderInfoBuilder> allRenderInfo = new HashMap<>();

  public AbstractMaterialRenderInfoProvider(DataGenerator gen) {
    super(gen, ResourcePackType.CLIENT_RESOURCES, "models/tool_materials");
  }

  /** Adds all relevant material stats */
  protected abstract void addMaterialRenderInfo();

  @Override
  public void act(DirectoryCache cache) {
    addMaterialRenderInfo();
    // generate
    allRenderInfo.forEach((materialId, info) -> saveThing(cache, materialId, info.build()));
  }


  /* Helpers */

  /** Starts a builder for a general render info */
  protected RenderInfoBuilder buildRenderInfo(MaterialId materialId) {
    return allRenderInfo.computeIfAbsent(materialId, id -> new RenderInfoBuilder());
  }

  @Accessors(fluent = true, chain = true)
  protected static class RenderInfoBuilder {
    @Setter
    private ResourceLocation texture = null;
    private String[] fallbacks;
    @Setter
    private int color = -1;
    @Setter
    private boolean skipUniqueTexture;
    @Setter
    private int luminosity = 0;

    /** Sets the fallback names */
    public RenderInfoBuilder fallbacks(String... fallbacks) {
      this.fallbacks = fallbacks;
      return this;
    }

    /** Builds the material */
    public MaterialRenderInfoJson build() {
      return new MaterialRenderInfoJson(texture, fallbacks, String.format("%06X", color), skipUniqueTexture ? Boolean.TRUE : null, luminosity);
    }
  }
}
