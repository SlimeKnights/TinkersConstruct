package slimeknights.tconstruct.library.materials.definition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.materials.MaterialRegistry;

import java.util.function.Supplier;

/** This class handles lazy loading of a material, as the times recipes load is too soon to fetch material objects */
@RequiredArgsConstructor(staticName = "of")
public class LazyMaterial implements Supplier<IMaterial> {
  @Getter
  private final MaterialId id;
  private IMaterial material;

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

  /** Returns true if the passed material matches this lazy material, matches based on ID comparison */
  public boolean matches(IMaterial material) {
    return id.matches(material);
  }

  /** Checks if this matches the given stack, using material item logic */
  public boolean matches(ItemStack stack) {
    return !stack.isEmpty() && id.matches(stack);
  }

  @Override
  public String toString() {
    return "LazyMaterial{" + id + '}';
  }
}
