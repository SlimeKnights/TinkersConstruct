package slimeknights.tconstruct.library.client.data.material;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.data.loadable.common.ColorLoadable;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider.MaterialSpriteInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoJson;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoJson.MaterialGeneratorJson;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** Base data generator for use in addons */
@SuppressWarnings("unused")  // API
public abstract class AbstractMaterialRenderInfoProvider extends GenericDataProvider {
  /** Map of material ID to builder, there is at most one builder for each ID */
  private final Map<MaterialVariantId,RenderInfoBuilder> allRenderInfo = new HashMap<>();
  @Nullable
  private final AbstractMaterialSpriteProvider materialSprites;
  @Nullable
  private final ExistingFileHelper existingFileHelper;

  public AbstractMaterialRenderInfoProvider(PackOutput packOutput, @Nullable AbstractMaterialSpriteProvider materialSprites, @Nullable ExistingFileHelper existingFileHelper) {
    super(packOutput, Target.RESOURCE_PACK, MaterialRenderInfoLoader.FOLDER, MaterialRenderInfoLoader.GSON);
    this.materialSprites = materialSprites;
    this.existingFileHelper = existingFileHelper;
  }

  public AbstractMaterialRenderInfoProvider(PackOutput packOutput) {
    this(packOutput, null, null);
  }

  /** Adds all relevant material stats */
  protected abstract void addMaterialRenderInfo();

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    if (existingFileHelper != null) {
      MaterialPartTextureGenerator.runCallbacks(existingFileHelper, null);
    }
    addMaterialRenderInfo();
    // generate
    return allOf(allRenderInfo.entrySet().stream().map((entry) -> saveJson(cache, entry.getKey().getLocation('/'), entry.getValue().build())))
      .thenRunAsync(() -> {
        if (existingFileHelper != null) {
          MaterialPartTextureGenerator.runCallbacks(null, null);
        }
    });
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
    private int color = -1;
    @Setter
    private boolean skipUniqueTexture;
    @Setter
    private int luminosity = 0;
    @Setter
    private MaterialGeneratorJson generator = null;

    /** Sets the color */
    public RenderInfoBuilder color(int color) {
      if ((color & 0xFF000000) == 0) {
        color |= 0xFF000000;
      }
      this.color = color;
      return this;
    }

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
      return new MaterialRenderInfoJson(texture, fallbacks, ColorLoadable.ALPHA.getString(color), skipUniqueTexture ? Boolean.TRUE : null, luminosity, generator);
    }
  }
}
