package slimeknights.tconstruct.library.modifiers.util;

import lombok.RequiredArgsConstructor;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager.ModifierRegistrationEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

/** Utility similar to {@link net.minecraftforge.registries.DeferredRegister} but for modifiers, as they no longer use a forge registry */
@RequiredArgsConstructor(staticName = "create")
public class ModifierDeferredRegister {
  /** All modifiers will be registered under this domain */
  private final String modId;
  /** List of entries to register */
  private final Map<ModifierId,Supplier<? extends Modifier>> entries = new LinkedHashMap<>();
  /** List of dynamic modifiers to expect */
  private final Map<ModifierId,Class<?>> expected = new LinkedHashMap<>();

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
    if (expected.containsKey(id)) {
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
   * @param classFilter  Class filter, if the modifier does not match this type in datapacks a warning will be logged
   * @param <T>  Class type
   * @return  Dynamic modifier instance
   * @deprecated use {@link #registerDynamic(String)}, class specific modifier serializers are being phased out.
   */
  @Deprecated(forRemoval = true)
  public <T extends Modifier> DynamicModifier<T> registerDynamic(String name, Class<T> classFilter) {
    if (seenRegisterEvent) {
      throw new IllegalStateException("Cannot register new entries to DeferredRegister after ModifierRegistrationEvent has been fired.");
    }
    ModifierId id = new ModifierId(modId, name);
    if (entries.containsKey(id)) {
      throw new IllegalArgumentException("Already registered as a static modifier " + id);
    }
    Class<?> original = expected.put(id, classFilter);
    if (original != null) {
      throw new IllegalArgumentException("Duplicate dynamic registration " + id);
    }
    return new DynamicModifier<>(id, classFilter);
  }

  /**
   * Registers a modifier as an expected dynamic modifier supporting any class
   * @param name  Modifier name, if this modifier is missing from datapacks a warning will be logged
   * @return  Dynamic modifier instance
   */
  public DynamicModifier<Modifier> registerDynamic(String name) {
    return registerDynamic(name, Modifier.class);
  }

  /** Called on modifier registration to register all entries */
  private void handleEvent(ModifierRegistrationEvent event) {
    seenRegisterEvent = true;
    for (Entry<ModifierId, Supplier<? extends Modifier>> entry : entries.entrySet()) {
      event.registerStatic(entry.getKey(), entry.getValue().get());
    }
    for (Entry<ModifierId, Class<?>> entry : expected.entrySet()) {
      event.registerExpected(entry.getKey(), entry.getValue());
    }
  }
}
