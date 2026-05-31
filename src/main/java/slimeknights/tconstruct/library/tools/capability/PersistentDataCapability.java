package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import slimeknights.mantle.compat.neoforged.neoforge.capabilities.Capability;
import slimeknights.tconstruct.compat.neoforged.neoforge.capabilities.CapabilityManager;
import slimeknights.tconstruct.compat.neoforged.neoforge.capabilities.CapabilityToken;
import slimeknights.tconstruct.compat.neoforged.neoforge.capabilities.ICapabilitySerializable;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.Lazy;
import slimeknights.mantle.compat.neoforged.neoforge.common.util.LazyOptional;
import slimeknights.tconstruct.compat.neoforged.neoforge.event.AttachCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.network.SyncPersistentDataPacket;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Capability to store persistent NBT data on an entity. For players, this is automatically synced to the client on load, but not during gameplay.
 * Persists after death, will reassess if we need some data to not persist death
 */
public class PersistentDataCapability {
  private PersistentDataCapability() {}

  /** Capability ID */
  private static final ResourceLocation ID = TConstruct.getResource("persistent_data");
  private static final String DATA_KEY = ID.toString();
  /** Capability type */
  public static final Capability<ModDataNBT> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

  /** Gets the data or warns if its missing */
  public static ModDataNBT getOrWarn(Entity entity) {
    return getData(entity);
  }

  /** Gets the persistent data for an entity. */
  public static ModDataNBT getData(Entity entity) {
    CompoundTag persistentData = entity.getPersistentData();
    CompoundTag data = persistentData.getCompound(DATA_KEY);
    persistentData.put(DATA_KEY, data);
    return ModDataNBT.readFromNBT(data);
  }

  /** Gets the persistent data as a lazy optional for old call sites. */
  public static LazyOptional<ModDataNBT> getCapability(Entity entity) {
    return LazyOptional.of(() -> getData(entity));
  }

  /** Registers this capability */
  public static void register() {
    slimeknights.tconstruct.TConstruct.getModBus().addListener(EventPriority.NORMAL, false, RegisterCapabilitiesEvent.class, PersistentDataCapability::register);
    NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.Clone.class, PersistentDataCapability::playerClone);
    NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerRespawnEvent.class, PersistentDataCapability::playerRespawn);
    NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerChangedDimensionEvent.class, PersistentDataCapability::playerChangeDimension);
    NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerLoggedInEvent.class, PersistentDataCapability::playerLoggedIn);
  }

  /** Registers the capability with the event bus */
  private static void register(RegisterCapabilitiesEvent event) {
    // Entity data is stored in NeoForge persistent entity data for the 1.21 port.
  }

  /** Event listener to attach the capability */
  private static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
    Entity entity = event.getObject();
    // must be on living entities as we use this for potions, but also support anything else with modifiers, this is their data
    if (entity instanceof LivingEntity || EntityModifierCapability.supportCapability(entity)) {
      Provider provider = new Provider();
      event.addCapability(ID, provider);
      event.addListener(provider);
    }
  }

  /** Syncs the data to the given player */
  private static void sync(Player player) {
    TinkerNetwork.getInstance().sendTo(new SyncPersistentDataPacket(getData(player).getCopy()), player);
  }

  /** copy caps when the player respawns/returns from the end */
  private static void playerClone(PlayerEvent.Clone event) {
    Player original = event.getOriginal();
    CompoundTag nbt = getData(original).getCopy();
    if (!nbt.isEmpty()) {
      getData(event.getEntity()).copyFrom(nbt);
    }
  }

  /** sync caps when the player respawns/returns from the end */
  private static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
    sync(event.getEntity());
  }

  /** sync caps when the player changes dimensions */
  private static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
    sync(event.getEntity());
  }

  /** sync caps when the player logs in */
  private static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    sync(event.getEntity());
  }

  /** Capability provider instance */
  private static class Provider implements ICapabilitySerializable<CompoundTag>, Runnable {
    private Lazy<CompoundTag> nbt;
    private LazyOptional<ModDataNBT> capability;
    private Provider() {
      this.nbt = Lazy.of(CompoundTag::new);
      this.capability = LazyOptional.of(() -> ModDataNBT.readFromNBT(nbt.get()));
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
      capability = LazyOptional.of(() -> ModDataNBT.readFromNBT(nbt.get()));
    }

    @Override
    public CompoundTag serializeNBT() {
      return nbt.get().copy();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
      this.nbt = Lazy.of(() -> nbt);
      run();
    }
  }
}
