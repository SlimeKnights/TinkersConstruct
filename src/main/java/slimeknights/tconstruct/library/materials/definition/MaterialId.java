package slimeknights.tconstruct.library.materials.definition;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.utils.IdParser;
import slimeknights.tconstruct.library.utils.ResourceId;

import javax.annotation.Nullable;

/**
 * This is just a copy of ResourceLocation for type safety in material JSON.
 */
public final class MaterialId extends ResourceId implements MaterialVariantId {
  public static final IdParser<MaterialId> PARSER = new IdParser<>(MaterialId::new, "Material");

  public MaterialId(String resourceName) {
    super(resourceName);
  }

  public MaterialId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public MaterialId(ResourceLocation location) {
    super(location);
  }

  private MaterialId(String namespace, String path, @Nullable Dummy pDummy) {
    super(namespace, path, pDummy);
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
  public String getSuffix() {
    return getNamespace() + '_' + getPath();
  }

  @Override
  public boolean matchesVariant(MaterialVariantId other) {
    return this.equals(other.getId());
  }


  /* Helpers */

  /** {@return Material ID, or null if invalid} */
  @Nullable
  public static MaterialId tryParse(String string) {
    return tryParse(string, (namespace, path) -> new MaterialId(namespace, path, null));
  }

  /** {@return Material ID, or null if invalid} */
  @Nullable
  public static MaterialId tryBuild(String namespace, String path) {
    return tryBuild(namespace, path, (n, p) -> new MaterialId(namespace, path, null));
  }
}
