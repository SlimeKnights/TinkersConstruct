package slimeknights.tconstruct.library.client.materials;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.client.data.spritetransformer.ISpriteTransformer;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import javax.annotation.Nullable;
import java.util.Set;

/** Base JSON for material render info */
@RequiredArgsConstructor
public class MaterialRenderInfoJson {
  @Nullable @Getter
  private final ResourceLocation texture;
  @Nullable @Getter
  private final String[] fallbacks;
  @Nullable @Getter
  private final String color;
  @Nullable
  private final Boolean skipUniqueTexture;
  @Getter
  private final int luminosity;
  @Getter @Nullable
  private final MaterialGeneratorJson generator;

  public boolean isSkipUniqueTexture() {
    return skipUniqueTexture == Boolean.TRUE;
  }

  /** Nested object with info for the generate part textures command */
  @RequiredArgsConstructor
  public static class MaterialGeneratorJson {
    /** Transformer to update images */
    @Getter
    private final ISpriteTransformer transformer;
    /** List of stat types supported by this material */
    private final Set<MaterialStatsId> supportedStats;
    /** If true, this ignores the material stats when determining applicable stat types for the command. Only affects the command, not datagen */
    protected final boolean ignoreMaterialStats;

    public MaterialGeneratorJson(MaterialGeneratorJson other) {
      this(other.transformer, other.supportedStats, other.ignoreMaterialStats);
    }

    /** If true, this stat type is supported */
    public boolean supportStatType(MaterialStatsId statType) {
      return supportedStats.contains(statType);
    }
  }
}
