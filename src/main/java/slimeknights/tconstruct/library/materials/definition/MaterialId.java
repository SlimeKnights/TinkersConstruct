package slimeknights.tconstruct.library.materials.definition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.utils.IdParser;

import javax.annotation.Nullable;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public final class MaterialId extends ResourceLocation implements MaterialVariantId {
  public static final IdParser<MaterialId> PARSER = new IdParser<>(MaterialId::new, "Material");

  public MaterialId(String resourceName) {
    super(resourceName);
  }

  public MaterialId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public MaterialId(ResourceLocation resourceLocation) {
    super(resourceLocation.getNamespace(), resourceLocation.getPath());
  }

  /** Checks if this ID matches the given material */
  public boolean matches(IMaterial material) {
    return this.equals(material.getIdentifier());
  }

  /** Checks if this ID matches the given stack */
  public boolean matches(ItemStack stack) {
    return !stack.isEmpty() && this.equals(IMaterialItem.getMaterialFromStack(stack));
  }

  @Override
  public MaterialId getId() {
    return this;
  }

  @Override
  public String getVariant() {
    return "";
  }

  @Override
  public boolean hasVariant() {
    return false;
  }

  @Override
  public ResourceLocation getLocation(char separator) {
    return this;
  }

  @Override
  public boolean matchesVariant(MaterialVariantId other) {
    return this.equals(other.getId());
  }

  /* Helpers */

  /**
   * Creates a new material ID from the given string
   * @param string  String
   * @return  Material ID, or null if invalid
   */
  @Nullable
  public static MaterialId tryParse(String string) {
    return PARSER.tryParse(string);
  }

  /**
   * Gets a resource location from JSON, throwing a nice exception if invalid
   * @param json  JSON object
   * @param key   Key to fetch
   * @return  Resource location parsed
   */
  public static MaterialId fromJson(JsonObject json, String key) {
    return PARSER.getFromJson(json, key);
  }

  /**
   * Gets a resource location from JSON, throwing a nice exception if invalid
   * @param json  JSON object
   * @param key   Key to fetch
   * @return  Resource location parsed
   */
  public static MaterialId convertJson(JsonElement json, String key) {
    return PARSER.convertFromJson(json, key);
  }
}
