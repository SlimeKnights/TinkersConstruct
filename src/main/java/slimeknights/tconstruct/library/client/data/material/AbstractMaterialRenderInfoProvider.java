package slimeknights.tconstruct.library.client.data.material;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider.MaterialSpriteInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoJson;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoJson.MaterialGeneratorJson;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/** Base data generator for use in addons */
public abstract class AbstractMaterialRenderInfoProvider extends GenericDataProvider {
  /** Map of material ID to builder, there is at most one builder for each ID */
  private final Map<MaterialVariantId,RenderInfoBuilder> allRenderInfo = new HashMap<>();
  @Nullable
  private final AbstractMaterialSpriteProvider materialSprites;

  public AbstractMaterialRenderInfoProvider(DataGenerator gen, @Nullable AbstractMaterialSpriteProvider materialSprites) {
    super(gen, PackType.CLIENT_RESOURCES, MaterialRenderInfoLoader.FOLDER, MaterialRenderInfoLoader.GSON);
    this.materialSprites = materialSprites;
  }

  public AbstractMaterialRenderInfoProvider(DataGenerator gen) {
    this(gen, null);
  }

  /** Adds all relevant material stats */
  protected abstract void addMaterialRenderInfo();

  @Override
  public void run(HashCache cache) {
    addMaterialRenderInfo();
    // generate
    allRenderInfo.forEach((materialId, info) -> saveThing(cache, materialId.getLocation('/'), info.build()));
  }


  /* Helpers */

  /** Initializes a builder for the given material */
  private RenderInfoBuilder getBuilder(ResourceLocation texture) {
    RenderInfoBuilder builder = new RenderInfoBuilder();
    if (materialSprites != null) {
      MaterialSpriteInfo spriteInfo = materialSprites.getMaterialInfo(texture);
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
        builder.generator(spriteInfo);
      }
    }
    return builder;
  }

  /** Starts a builder for a general render info */
  protected RenderInfoBuilder buildRenderInfo(MaterialVariantId materialId) {
    return allRenderInfo.computeIfAbsent(materialId, id -> getBuilder(materialId.getLocation('_')));
  }

  /**
   * Starts a builder for a general render info with an overridden texture.
   * Use {@link #buildRenderInfo(MaterialVariantId)} if you plan to override the texture without copying the datagen settings
   */
  protected RenderInfoBuilder buildRenderInfo(MaterialVariantId materialId, ResourceLocation texture) {
    return allRenderInfo.computeIfAbsent(materialId, id -> getBuilder(texture).texture(texture));
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
    @Setter
    private MaterialGeneratorJson generator = null;

    /** Sets the fallback names */
    public RenderInfoBuilder fallbacks(@Nullable String... fallbacks) {
      this.fallbacks = fallbacks;
      return this;
    }

    /** Sets the texture from another material variant */
    public RenderInfoBuilder materialTexture(MaterialVariantId variantId) {
      return texture(variantId.getLocation('_'));
    }

    /** Builds the material */
    public MaterialRenderInfoJson build() {
      return new MaterialRenderInfoJson(texture, fallbacks, String.format("%06X", color), skipUniqueTexture ? Boolean.TRUE : null, luminosity, generator);
    }
  }
}
