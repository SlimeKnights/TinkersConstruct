package slimeknights.tconstruct.library.materials.stats;

// todo: possibly switch this directly to a class and implement an interface that signifies the registry stuff?

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * <p>Part types are actually different material stat types.
 * Think of them as a collection of attributes a material has, when it's used for a specific part.
 * e.g. for a material to be used as a bowstring, it needs to have bowstring material stats.</p>
 *
 * <p>Each instance of this class should be unique. If two instances with the same id exist, internal systems might break.</p>
 */
@AllArgsConstructor
@Getter
public final class MaterialStatType<T extends IMaterialStats> {
  private final ResourceLocation identifier;
  private final Class<T> statsClass;
  private final T defaultStats;
  @Accessors(fluent = true)
  private final boolean canRepair;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MaterialStatType<?> that = (MaterialStatType<?>) o;
    return identifier.equals(that.identifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier);
  }
}
