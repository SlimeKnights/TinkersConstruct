package slimeknights.tconstruct.library.materials.json;

import lombok.Data;
import net.minecraft.util.ResourceLocation;

/**
 * JSON representing a trait on a material
 */
@Data
public class TraitJson {
  private final ResourceLocation name;
  private final int level;
}
