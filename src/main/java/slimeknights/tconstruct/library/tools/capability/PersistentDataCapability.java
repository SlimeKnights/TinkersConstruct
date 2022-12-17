package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.network.SyncPersistentDataPacket;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;

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
  /** Capability type */
  public static final Capability<NamespacedNBT> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

  /** Gets the data or warns if its missing */
  public static NamespacedNBT getOrWarn(Entity entity) {
    Optional<NamespacedNBT> data = entity.getCapability(CAPABILITY).resolve();
    if (data.isEmpty()) {
      TConstruct.LOG.warn("Missing Tinkers NBT on entity {}, this should not happen", entity.getType());
      return new NamespacedNBT();
    }
    return data.get();
  }

  /** Registers this capability */
  public static void register() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, RegisterCapabilitiesEvent.class, PersistentDataCapability::register);
    MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, PersistentDataCapability::attachCapability);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.Clone.class, PersistentDataCapability::playerClone);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerRespawnEvent.class, PersistentDataCapability::playerRespawn);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerChangedDimensionEvent.class, PersistentDataCapability::playerChangeDimension);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerLoggedInEvent.class, PersistentDataCapability::playerLoggedIn);
  }

  /** Registers the capability with the event bus */
  private static void register(RegisterCapabilitiesEvent event) {
    event.register(NamespacedNBT.class);
  }

  /** Event listener to attach the capability */
  private static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
    Entity entity = event.getObject();
    // must be on players, but also support anything else with modifiers, this is their data
    if (entity instanceof Player || EntityModifierCapability.supportCapability(entity)) {
      Provider provider = new Provider();
      event.addCapability(ID, provider);
      event.addListener(provider);
    }
  }

  /** Syncs the data to the given player */
  private static void sync(Player player) {
    player.getCapability(CAPABILITY).ifPresent(data -> TinkerNetwork.getInstance().sendTo(new SyncPersistentDataPacket(data.getCopy()), player));
  }

  /** copy caps when the player respawns/returns from the end */
  private static void playerClone(PlayerEvent.Clone event) {
    event.getOriginal().getCapability(CAPABILITY).ifPresent(oldData -> {
      CompoundTag nbt = oldData.getCopy();
      if (!nbt.isEmpty()) {
        event.getPlayer().getCapability(CAPABILITY).ifPresent(newData -> newData.copyFrom(nbt));
      }
    });
  }

  /** sync caps when the player respawns/returns from the end */
  private static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
    sync(event.getPlayer());
  }

  /** sync caps when the player changes dimensions */
  private static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
    sync(event.getPlayer());
  }

  /** sync caps when the player logs in */
  private static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    sync(event.getPlayer());
  }

  /** Capability provider instance */
  private static class Provider implements ICapabilitySerializable<CompoundTag>, Runnable {
    private Lazy<CompoundTag> nbt;
    private LazyOptional<NamespacedNBT> capability;
    private Provider() {
      this.nbt = Lazy.of(CompoundTag::new);
      this.capability = LazyOptional.of(() -> NamespacedNBT.readFromNBT(nbt.get()));
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
      capability = LazyOptional.of(() -> NamespacedNBT.readFromNBT(nbt.get()));
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
