package slimeknights.tconstruct.library.client.data.material;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider.MaterialSpriteInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoJson;
import slimeknights.tconstruct.library.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/** Base data generator for use in addons */
public abstract class AbstractMaterialRenderInfoProvider extends GenericDataProvider {
  /** Map of material ID to builder, there is at most one builder for each ID */
  private final Map<MaterialId,RenderInfoBuilder> allRenderInfo = new HashMap<>();
  @Nullable
  private final AbstractMaterialSpriteProvider materialSprites;

  public AbstractMaterialRenderInfoProvider(DataGenerator gen, @Nullable AbstractMaterialSpriteProvider materialSprites) {
    super(gen, ResourcePackType.CLIENT_RESOURCES, "models/tool_materials");
    this.materialSprites = materialSprites;
  }

  public AbstractMaterialRenderInfoProvider(DataGenerator gen) {
    this(gen, null);
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

  /** Initializes a builder for the given material */
  private RenderInfoBuilder getBuilder(MaterialId materialId) {
    RenderInfoBuilder builder = new RenderInfoBuilder();
    if (materialSprites != null) {
      MaterialSpriteInfo spriteInfo = materialSprites.getMaterialInfo(materialId);
      if (spriteInfo != null) {
        String[] fallbacks = spriteInfo.getFallbacks();
        if (fallbacks.length > 0) {
          builder.fallbacks(fallbacks);
        }
        // colors are in AABBGGRR format, we want AARRGGBB, so swap red and blue
        int color = spriteInfo.getTransformer().getFallbackColor();
        if (color != 0xFFFFFFFF) {
          builder.color((color & 0x00FF00) | ((color >> 16) & 0x0000FF) | ((color << 16) & 0xFF0000));
        }
      }
    }
    return builder;
  }

  /** Starts a builder for a general render info */
  protected RenderInfoBuilder buildRenderInfo(MaterialId materialId) {
    return allRenderInfo.computeIfAbsent(materialId, this::getBuilder);
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
    public RenderInfoBuilder fallbacks(@Nullable String... fallbacks) {
      this.fallbacks = fallbacks;
      return this;
    }

    /** Builds the material */
    public MaterialRenderInfoJson build() {
      return new MaterialRenderInfoJson(texture, fallbacks, String.format("%06X", color), skipUniqueTexture ? Boolean.TRUE : null, luminosity);
    }
  }
}
