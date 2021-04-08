package slimeknights.tconstruct.library.client.materials;

import lombok.Data;
import net.minecraft.util.Identifier;

@Data
public class MaterialRenderInfoJson {
  private final Identifier texture;
  private final String[] fallbacks;
  private final String color;
}
