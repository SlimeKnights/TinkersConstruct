package slimeknights.tconstruct.library.materials.definition;

import net.minecraft.resources.ResourceLocation;

/** Internal record to represent a material ID with a variant. Use {@link MaterialVariantId} to create if needed */
record MaterialVariantIdImpl(MaterialId material, String variant) implements MaterialVariantId {
  @Override
  public MaterialId getId() {
    return material;
  }

  @Override
  public String getVariant() {
    return variant;
  }

  @Override
  public boolean hasVariant() {
    return true;
  }

  @Override
  public boolean matchesVariant(MaterialVariantId other) {
    return this.sameVariant(other);
  }

  @Override
  public ResourceLocation getLocation(char separator) {
    return new ResourceLocation(material.getNamespace(), material.getPath() + separator + variant);
  }

  @Override
  public String toString() {
    return material + "#" + variant;
  }
}
