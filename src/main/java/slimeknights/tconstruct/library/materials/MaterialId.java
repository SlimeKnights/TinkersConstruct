package slimeknights.tconstruct.library.materials;

import org.jetbrains.annotations.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public class MaterialId extends Identifier {

  public MaterialId(String resourceName) {
    super(resourceName);
  }

  public MaterialId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public MaterialId(Identifier resourceLocation) {
    super(resourceLocation.getNamespace(), resourceLocation.getPath());
  }

  /**
   * Creates a new material ID from the given string
   * @param string  String
   * @return  Material ID, or null if invalid
   */
  @Nullable
  public static MaterialId tryParse(String string) {
    try {
      return new MaterialId(string);
    } catch (InvalidIdentifierException resourcelocationexception) {
      return null;
    }
  }
}
