package slimeknights.tconstruct.library.tools.capability;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** Capability to allow an entity to store modifiers, used on projectiles fired from modifiable items */
public class EntityModifierCapability {
  /** Default instance to use with orElse */
  public static final EntityModifiers EMPTY = new EntityModifiers() {
    @Override
    public ModifierNBT getModifiers() {
      return ModifierNBT.EMPTY;
    }

    @Override
    public void setModifiers(ModifierNBT nbt) {}

    @Override
    public void addModifiers(ModifierNBT nbt) {}
  };

  private EntityModifierCapability() {}

  /* Static helpers */

  /** List of predicates to check if the entity supports this capability */
  private static final List<Predicate<Entity>> ENTITY_PREDICATES = new ArrayList<>();

  /** Capability ID */
  private static final ResourceLocation ID = TConstruct.getResource("modifiers");
  /** Capability type */
  public static final Capability<EntityModifiers> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

  /** Gets the capability for the entity or an empty instance if missing */
  public static EntityModifiers getCapability(Entity entity) {
    return entity.getCapability(CAPABILITY).orElse(EMPTY);
  }

  /** Gets the data or an empty instance if missing */
  public static ModifierNBT getOrEmpty(Entity entity) {
    return entity.getCapability(CAPABILITY).orElse(EMPTY).getModifiers();
  }

  /** Checks if the given entity supports this capability */
  public static boolean supportCapability(Entity entity) {
    for (Predicate<Entity> entityPredicate : ENTITY_PREDICATES) {
      if (entityPredicate.test(entity)) {
        return true;
      }
    }
    return false;
  }

  /** Registers a predicate of entites that need this capability */
  public static void registerEntityPredicate(Predicate<Entity> predicate) {
    ENTITY_PREDICATES.add(predicate);
  }

  /** Registers this capability with relevant busses*/
  public static void register() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, RegisterCapabilitiesEvent.class, event -> event.register(ModifierNBT.class));
    MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, EntityModifierCapability::attachCapability);
  }

  /** Event listener to attach the capability */
  private static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
    if (supportCapability(event.getObject())) {
      Provider provider = new Provider();
      event.addCapability(ID, provider);
      event.addListener(provider);
    }
  }

  /** Capability provider instance */
  private static class Provider implements ICapabilitySerializable<ListTag>, Runnable, EntityModifiers {
    @Getter @Setter
    private ModifierNBT modifiers = ModifierNBT.EMPTY;
    private LazyOptional<EntityModifiers> capability;
    private Provider() {
      this.capability = LazyOptional.of(() -> this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
      return CAPABILITY.orEmpty(cap, capability);
    }

    @Override
    public void run() {
      // called when capabilities invalidate, create a new cap just in case they are revived later
      capability.invalidate();
      capability = LazyOptional.of(() -> this);
    }

    @Override
    public ListTag serializeNBT() {
      return modifiers.serializeToNBT();
    }

    @Override
    public void deserializeNBT(ListTag nbt) {
      modifiers = ModifierNBT.readFromNBT(nbt);
      run();
    }
  }

  /** Interface for callers to use */
  public interface EntityModifiers {
    /** Gets the stored modifiers */
    ModifierNBT getModifiers();

    /** Sets the stored modifiers */
    void setModifiers(ModifierNBT nbt);

    /** Adds additional modifiers to the stored modifiers */
    default void addModifiers(ModifierNBT nbt) {
      ModifierNBT existing = getModifiers();
      if (existing.isEmpty()) {
        setModifiers(nbt);
      } else {
        setModifiers(ModifierNBT.builder().add(existing).add(nbt).build());
      }
    }
  }
}
