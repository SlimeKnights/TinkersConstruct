package slimeknights.tconstruct.library.client.materials;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

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

  public boolean isSkipUniqueTexture() {
    return skipUniqueTexture == Boolean.TRUE;
  }
}
