package slimeknights.tconstruct.library.materials.stats;

import lombok.Getter;
import lombok.experimental.Accessors;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.registration.object.IdAwareObject;

import java.util.function.Function;

/**
 * <p>Part types are actually different material stat types.
 * Think of them as a collection of attributes a material has, when it's used for a specific part.
 * e.g. for a material to be used as a bowstring, it needs to have bowstring material stats.</p>
 *
 * <p>Each instance of this class should be unique. If two instances with the same id exist, internal systems might break.</p>
 */
@Getter
public class MaterialStatType<T extends IMaterialStats> implements IdAwareObject {
  /** Context key to use if you want the recipe serializer passed into your recipe */
  public static final ContextKey<MaterialStatType<?>> CONTEXT_KEY = new ContextKey<>("material_stat_type");

  private final MaterialStatsId id;
  private final T defaultStats;
  private final RecordLoadable<T> loadable;
  @Accessors(fluent = true)
  private final boolean canRepair;

  /** Creates a stat type using the given default instance */
  public MaterialStatType(MaterialStatsId id, T defaultStats, RecordLoadable<T> loadable) {
    this.id = id;
    this.defaultStats = defaultStats;
    this.loadable = loadable;
    this.canRepair = defaultStats instanceof IRepairableMaterialStats;
  }

  /**
   * Creates a stat type that wishes to store the stat type in a field. Use {@link MaterialStatType#CONTEXT_KEY} to fetch the type in the loadable.
   */
  public MaterialStatType(MaterialStatsId id, Function<MaterialStatType<T>,T> defaultStatsProvider, RecordLoadable<T> loadable) {
    this.id = id;
    this.loadable = loadable;
    this.defaultStats = defaultStatsProvider.apply(this);
    this.canRepair = defaultStats instanceof IRepairableMaterialStats;
  }

  /** Creates a stat type that always resolves to the same instance */
  public static <T extends IMaterialStats> MaterialStatType<T> singleton(MaterialStatsId id, T instance) {
    return new MaterialStatType<>(id, instance, new SingletonLoader<>(instance));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MaterialStatType<?> that = (MaterialStatType<?>) o;
    return this.id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }
}
