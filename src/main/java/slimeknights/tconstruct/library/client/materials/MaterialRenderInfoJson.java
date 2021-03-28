package slimeknights.tconstruct.library.client.materials;

import lombok.Data;
import net.minecraft.util.ResourceLocation;

@Data
public class MaterialRenderInfoJson {
  private final ResourceLocation texture;
  private final String[] fallbacks;
  private final String color;
}
