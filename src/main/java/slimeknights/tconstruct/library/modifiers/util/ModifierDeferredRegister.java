package slimeknights.tconstruct.library.modifiers.util;

import lombok.RequiredArgsConstructor;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager.ModifierRegistrationEvent;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

/** Utility similar to {@link net.neoforged.neoforge.registries.DeferredRegister} but for modifiers, as they no longer use a forge registry */
@RequiredArgsConstructor(staticName = "create")
public class ModifierDeferredRegister {
  /** All modifiers will be registered under this domain */
  private final String modId;
  /** List of entries to register */
  private final Map<ModifierId,Supplier<? extends Modifier>> entries = new LinkedHashMap<>();
  /** List of dynamic modifiers to expect */
  private final Set<ModifierId> expected = new LinkedHashSet<>();

  /** If true, the registration event has been seen, so its now too late to register new modifiers */
  private boolean seenRegisterEvent = false;

  /** Registers the deferred register with the relevant forge event busses */
  public void register(IEventBus bus) {
    bus.addListener(EventPriority.NORMAL, false, ModifierRegistrationEvent.class, this::handleEvent);
  }

  /**
   * Registers a new static modifier with the modifier manager. Its generally preferred to use dynamic modifiers as that gives datapacks more control
   * @param name      Modifier name
   * @param supplier  Supplier to modifier instance
   * @param <T>       Type of modifier
   * @return StaticModifier instance that will resolve to the modifier once static modifiers are registered
   */
  public <T extends Modifier> StaticModifier<T> register(String name, Supplier<? extends T> supplier) {
    if (seenRegisterEvent) {
      throw new IllegalStateException("Cannot register new entries to DeferredRegister after ModifierRegistrationEvent has been fired.");
    }
    ModifierId id = new ModifierId(modId, name);
    if (expected.contains(id)) {
      throw new IllegalArgumentException("Already registered as an dynamic modifier " + id);
    }
    Supplier<? extends Modifier> original = entries.put(id, supplier);
    if (original != null) {
      throw new IllegalArgumentException("Duplicate static registration " + id);
    }
    return new StaticModifier<>(id);
  }

  /**
   * Registers a modifier as an expected dynamic modifier
   * @param name         Modifier name, if this modifier is missing from datapacks a warning will be logged
   * @return  Dynamic modifier instance
   */
  public DynamicModifier registerDynamic(String name) {
    if (seenRegisterEvent) {
      throw new IllegalStateException("Cannot register new entries to DeferredRegister after ModifierRegistrationEvent has been fired.");
    }
    ModifierId id = new ModifierId(modId, name);
    if (entries.containsKey(id)) {
      throw new IllegalArgumentException("Already registered as a static modifier " + id);
    }
    expected.add(id);
    return new DynamicModifier(id);
  }

  /** Called on modifier registration to register all entries */
  private void handleEvent(ModifierRegistrationEvent event) {
    seenRegisterEvent = true;
    for (Entry<ModifierId, Supplier<? extends Modifier>> entry : entries.entrySet()) {
      event.registerStatic(entry.getKey(), entry.getValue().get());
    }
    for (ModifierId id : expected) {
      event.registerExpected(id);
    }
  }
}
