package slimeknights.tconstruct.library.tools.capability;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.mantle.registration.object.IdAwareObject;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Capability to make it easy for Tinkers to store common data on the player, primarily used for armor
 * Data stored in this capability is not saved to NBT, most often its filled by the relevant equipment events
 */
public class TinkerDataCapability {
  private TinkerDataCapability() {}

  /** Capability ID */
  private static final ResourceLocation ID = TConstruct.getResource("modifier_data");
  /** Capability type */
  public static final Capability<Holder> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

  /** Registers this capability */
  public static void register() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, RegisterCapabilitiesEvent.class, TinkerDataCapability::register);
    MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, TinkerDataCapability::attachCapability);
  }

  /** Registers the capability with the event bus */
  private static void register(RegisterCapabilitiesEvent event) {
    event.register(Holder.class);
  }

  /** Event listener to attach the capability */
  private static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
    if (event.getObject() instanceof LivingEntity) {
      Provider provider = new Provider();
      event.addCapability(ID, provider);
      event.addListener(provider);
    }
  }

  /** Gets the data capability from an entity, or null if missing */
  @SuppressWarnings("DataFlowIssue")
  @Nullable
  public static TinkerDataCapability.Holder getData(LivingEntity entity) {
    return entity.getCapability(CAPABILITY).orElse(null);
  }


  /* Required methods */

  /** Capability provider instance */
  private static class Provider implements ICapabilityProvider, Runnable {
    private LazyOptional<Holder> data;
    private Provider() {
      this.data = LazyOptional.of(Holder::new);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
      return CAPABILITY.orEmpty(cap, data);
    }

    @Override
    public void run() {
      // called when capabilities invalidate, just invalidate but preserve the old data
      // (as if they revive the equipment change event does not fire again, see dimension change)
      Holder oldData = data.orElse(new Holder());
      data.invalidate();
      data = LazyOptional.of(() -> oldData);
    }
  }

  /** Class for generic keys */
  @SuppressWarnings("unused")
  @RequiredArgsConstructor(staticName = "of")
  public static class TinkerDataKey<T> implements IdAwareObject {
    /** Name for debug */
    @Getter
    private final ResourceLocation id;

    @Override
    public String toString() {
      return "TinkerDataKey{" + id + '}';
    }
  }

  /** Extension key that can automatically create an instance if missing */
  public static class ComputableDataKey<T> extends TinkerDataKey<T> implements Function<TinkerDataKey<?>, T> {
    private final Supplier<T> constructor;
    private ComputableDataKey(ResourceLocation name, Supplier<T> constructor) {
      super(name);
      this.constructor = constructor;
    }

    /** Creates a new instance */
    public static <T> ComputableDataKey<T> of(ResourceLocation name, Supplier<T> constructor) {
      return new ComputableDataKey<>(name, constructor);
    }

    @Override
    public T apply(TinkerDataKey<?> tinkerDataKey) {
      return constructor.get();
    }
  }


  /** Data class holding the tinker data */
  public static class Holder {
    private final Map<TinkerDataKey<?>, Object> data = new IdentityHashMap<>();

    /**
     * Adds a value to the holder
     * @param key    Key to add
     * @param value  Value to add
     * @param <T>    Data type
     */
    public <T> void put(TinkerDataKey<T> key, T value) {
      data.put(key, value);
    }

    /**
     * Adds the given value to the float data key
     * @param key    Key to add
     * @param value  Value to add
     */
    public void add(TinkerDataKey<Float> key, float value) {
      float newValue = get(key, 0f) + value;
      if (newValue == 0) {
        data.remove(key);
      } else {
        data.put(key, newValue);
      }
    }

    /**
     * Removes a value to the holder
     * @param key  Key to remove
     */
    public void remove(TinkerDataKey<?> key) {
      data.remove(key);
    }

    /**
     * Gets a value from the holder, or a default if missing
     * @param key           Holder key
     * @param defaultValue  Value
     * @param <T>           Data type
     * @return  Data or default
     */
    @SuppressWarnings("unchecked")
    public <S, T extends S> S get(TinkerDataKey<T> key, S defaultValue) {
      return (T) data.getOrDefault(key, defaultValue);
    }

    /**
     * Gets a value from the holder, or null if missing
     * @param key           Holder key
     * @param <T>           Data type
     * @return  Data or default
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(TinkerDataKey<T> key) {
      return (T) data.get(key);
    }

    /** Gets the value from the holder, creating it if missing */
    @SuppressWarnings("unchecked")
    public <T> T computeIfAbsent(TinkerDataKey<T> key, Function<TinkerDataKey<?>,T> constructor) {
      return (T) data.computeIfAbsent(key, constructor);
    }

    /** Gets the value from the holder, creating it if missing */
    public <T, U extends TinkerDataKey<T> & Function<TinkerDataKey<?>,T>> T computeIfAbsent(U key) {
      return computeIfAbsent(key, key);
    }

    /**
     * Checks if the given key is present
     * @param key  Key to check
     * @return  true if present
     */
    public boolean contains(TinkerDataKey<?> key) {
      return data.containsKey(key);
    }
  }
}
