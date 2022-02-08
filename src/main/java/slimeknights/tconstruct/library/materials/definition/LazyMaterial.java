package slimeknights.tconstruct.library.materials.definition;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import slimeknights.tconstruct.library.materials.MaterialRegistry;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/** This class handles lazy loading of a material, as the times recipes load is too soon to fetch material objects */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class LazyMaterial implements Supplier<IMaterial> {
  /** ID to fetch */
  @Getter
  private final MaterialId id;
  /** Cached material fetched from the registry */
  private IMaterial material;

  protected LazyMaterial(IMaterial material) {
    this.id = material.getIdentifier();
    this.material = material;
  }

  /** Creates a new lazy material instance */
  public static LazyMaterial of(MaterialId id) {
    return new LazyMaterial(id);
  }

  /** Creates a new lazy material instance from an existing material */
  public static LazyMaterial of(IMaterial material) {
    return new LazyMaterial(material);
  }

  @Override
  public IMaterial get() {
    if (material == null) {
      if (!MaterialRegistry.isFullyLoaded()) {
        return IMaterial.UNKNOWN;
      }
      material = MaterialRegistry.getMaterial(id);
    }
    return material;
  }

  /** If true, this material was not found in the registry. Can use to immediately resolve a material */
  public boolean isUnknown() {
    return get() == IMaterial.UNKNOWN;
  }

  /* Predicate */

  /** Returns true if the passed material matches this lazy material, matches based on ID comparison */
  public boolean matches(MaterialId material) {
    return id.equals(material);
  }

  @Override
  public String toString() {
    return "LazyMaterial{" + id + '}';
  }

  @Override
  public boolean equals(@Nullable Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || this.getClass() != other.getClass()) {
      return false;
    }
    return this.id.equals(((LazyMaterial)other).id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
