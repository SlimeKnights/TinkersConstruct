package slimeknights.tconstruct.library.modifiers.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.ModifierManager.ModifiersLoadedEvent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Supplier that will return a modifier from a datapack, automatically updating to the new instance when datapacks reload
 * TODO 1.20: remove type bound.
 */
public class DynamicModifier<T> extends LazyModifier {
  /** List of all dynamic modifiers, to clear cache when modifiers reload */
  private static final AtomicInteger INVALIDATION_COUNTER = new AtomicInteger(0);

  /** Filter for the modifier, must be of this type to return */
  private final Class<T> classFilter;
  /** Last count of the invalation counter, if this is smaller than the global, time to invalidate */
  private int invalidationCount = -1;

  /**
   * Creates a new instance.
   * Creating an instance of a dynamic modifier is considered "expensive", as it will never be garbage collected
   * @param id           Modifier ID to fetch
   * @param classFilter  expected type for the modifier
   */
  public DynamicModifier(ModifierId id, Class<T> classFilter) {
    super(id);
    this.classFilter = classFilter;
  }

  /** Returns true if this static modifier has a value. A return of true here means {@link #get()} will not throw */
  @Override
  public boolean isBound() {
    return super.isBound() && classFilter.isInstance(getUnchecked());
  }

  @Override
  protected Modifier getUnchecked() {
    if (invalidationCount < INVALIDATION_COUNTER.get()) {
      result = null;
    }
    if (result == null) {
      result = ModifierManager.getValue(id);
      invalidationCount = INVALIDATION_COUNTER.get();
    }
    return result;
  }

  /**
   * Fetches the modifier from the modifier manager. Should not be called until after the modifier registration event fires
   * @return  Modifier instance
   * @throws IllegalStateException  If the modifier manager has not registered modifiers, if the modifier ID was never registered or if the modifier is the wrong class type
   */
  @Override
  public Modifier get() {
    if (!ModifierManager.INSTANCE.isDynamicModifiersLoaded()) {
      throw new IllegalStateException("Cannot fetch a dynamic modifiers before datapacks load");
    }
    Modifier result = getUnchecked();
    if (result == ModifierManager.INSTANCE.getDefaultValue()) {
      throw new IllegalStateException("Dynamic modifier for " + id + " returned " + ModifierManager.EMPTY + ", this typically means the modifier is not registered");
    }
    if (!classFilter.isInstance(result)) {
      throw new IllegalStateException("Dynamic modifier is not the required type");
    }
    return result;
  }

  /**
   * Same as {@link #get()}, but fetches the modifier as the expected type. Separate to allow the expected type to be an interface
   * @deprecated use {@link #get()}, class specific modifier serializers are being phased out.
   */
  @Deprecated(forRemoval = true)
  public T asType() {
    return classFilter.cast(get());
  }

  @Override
  public String toString() {
    return "DynamicModifier{" + id + ",filter=" + classFilter.getSimpleName() + '}';
  }

  /** Registers event listeners with the forge event bus */
  public static void init() {
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ModifiersLoadedEvent.class, e -> INVALIDATION_COUNTER.incrementAndGet());
  }
}
