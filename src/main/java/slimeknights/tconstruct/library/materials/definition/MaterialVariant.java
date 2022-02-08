package slimeknights.tconstruct.library.materials.definition;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/** Class for a material variant, lazily loads the material instance and stores the variant string */
public class MaterialVariant extends LazyMaterial {
  /** Unknown material variant, as it comes up a lot */
  public static final MaterialVariant UNKNOWN = of(IMaterial.UNKNOWN);

  @Getter
  private final MaterialVariantId variant;
  protected MaterialVariant(MaterialVariantId variant) {
    super(variant.getId());
    this.variant = variant;
  }

  protected MaterialVariant(IMaterial material, String variant) {
    super(material);
    this.variant = MaterialVariantId.create(material.getIdentifier(), variant);
  }

  /** Creates a new lazy material variant with the given variant ID */
  public static MaterialVariant of(MaterialVariantId variantId) {
    return new MaterialVariant(variantId);
  }

  /** Creates a new lazy material variant with the given ID and variant */
  public static MaterialVariant of(MaterialId id, String variant) {
    return of(MaterialVariantId.create(id, variant));
  }

  /** Creates a new lazy material variant with the given ID and variant */
  public static MaterialVariant of(IMaterial material, String variant) {
    return new MaterialVariant(material, variant);
  }

  /** Creates a new lazy material variant with the given ID and variant */
  public static MaterialVariant of(IMaterial material) {
    return of(material, "");
  }

  /** Checks if two material variants match. If this has no variant, matches any variant of the same material */
  public boolean matchesVariant(MaterialVariantId variantId) {
    return this.variant.matchesVariant(variantId);
  }

  /** Checks if two material variants match. If this has no variant, matches any variant of the same material */
  public boolean matchesVariant(MaterialVariant variant) {
    return this.variant.matchesVariant(variant);
  }

  /** Checks if two material variants match. If this has no variant, matches any variant of the same material */
  public boolean matchesVariant(ItemStack stack) {
    return this.variant.matchesVariant(stack);
  }

  /** Checks if this matches the given variant ID */
  public boolean sameVariant(MaterialVariantId variantId) {
    return this.variant.sameVariant(variantId);
  }

  @Override
  public String toString() {
    return "MaterialVariant{" + variant + '}';
  }

  @Override
  public boolean equals(@Nullable Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || this.getClass() != other.getClass()) {
      return false;
    }
    return this.variant.sameVariant(((MaterialVariant)other).variant);
  }

  @Override
  public int hashCode() {
    return variant.hashCode();
  }
}
