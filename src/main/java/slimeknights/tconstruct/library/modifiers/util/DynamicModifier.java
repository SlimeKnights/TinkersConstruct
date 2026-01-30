package slimeknights.tconstruct.library.modifiers.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.ModifierManager.ModifiersLoadedEvent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Supplier that will return a modifier from a datapack, automatically updating to the new instance when datapacks reload.
 * Extends {@link StaticModifier} to make it easier to migrate a static modifier to a dynamic one without breaking binary compatability on the field.
 */
public class DynamicModifier extends StaticModifier<Modifier> {
  /** List of all dynamic modifiers, to clear cache when modifiers reload */
  private static final AtomicInteger INVALIDATION_COUNTER = new AtomicInteger(0);

  /** Last count of the invalidation counter, if this is smaller than the global, time to invalidate */
  private int invalidationCount = -1;

  /**
   * Creates a new instance.
   * Creating an instance of a dynamic modifier is considered "expensive", as it will never be garbage collected
   * @param id           Modifier ID to fetch
   */
  public DynamicModifier(ModifierId id) {
    super(id);
  }

  @Override
  public boolean isBound() {
    return ModifierManager.INSTANCE.isDynamicModifiersLoaded() && getUnchecked() != ModifierManager.INSTANCE.getDefaultValue();
  }

  @Override
  protected Modifier getUnchecked() {
    if (result == null || invalidationCount < INVALIDATION_COUNTER.get()) {
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
      throw new IllegalStateException("Cannot fetch a dynamic modifiers before datapacks are loaded");
    }
    Modifier result = getUnchecked();
    if (result == ModifierManager.INSTANCE.getDefaultValue()) {
      throw new IllegalStateException("Dynamic modifier for " + id + " returned " + ModifierManager.EMPTY + ", this typically means the modifier is not registered");
    }
    return result;
  }

  @Override
  public String toString() {
    return "DynamicModifier{" + id + '}';
  }

  /** Registers event listeners with the forge event bus */
  public static void init() {
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ModifiersLoadedEvent.class, e -> INVALIDATION_COUNTER.incrementAndGet());
  }
}
