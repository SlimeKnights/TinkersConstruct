package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.network.SyncPersistentDataPacket;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Capability to store persistent NBT data on an entity. For players, this is automatically synced to the client on load, but not during gameplay.
 * Persists after death, will reassess if we need some data to not persist death
 */
public class PersistentDataCapability implements Capability.IStorage<NamespacedNBT> {
  private PersistentDataCapability() {}

  /** Capability ID */
  private static final ResourceLocation ID = TConstruct.getResource("persistent_data");
  /** Instance of the capability storage because forge requires it */
  private static final PersistentDataCapability INSTANCE = new PersistentDataCapability();
  /** Capability type */
  @CapabilityInject(NamespacedNBT.class)
  public static Capability<NamespacedNBT> CAPABILITY = null;

  /** Registers this capability */
  public static void register() {
    CapabilityManager.INSTANCE.register(NamespacedNBT.class, INSTANCE, NamespacedNBT::new);
    MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, PersistentDataCapability::attachCapability);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.Clone.class, PersistentDataCapability::playerClone);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerRespawnEvent.class, PersistentDataCapability::playerRespawn);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerChangedDimensionEvent.class, PersistentDataCapability::playerChangeDimension);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerLoggedInEvent.class, PersistentDataCapability::playerLoggedIn);
  }

  /** Event listener to attach the capability */
  private static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
    if (event.getObject() instanceof PlayerEntity) {
      Provider provider = new Provider();
      event.addCapability(ID, provider);
      event.addListener(provider);
    }
  }

  /** Syncs the data to the given player */
  private static void sync(PlayerEntity player) {
    player.getCapability(CAPABILITY).ifPresent(data -> TinkerNetwork.getInstance().sendTo(new SyncPersistentDataPacket(data.getCopy()), player));
  }

  /** copy caps when the player respawns/returns from the end */
  private static void playerClone(PlayerEvent.Clone event) {
    event.getOriginal().getCapability(CAPABILITY).ifPresent(oldData -> {
      CompoundNBT nbt = oldData.getCopy();
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


  /* Required methods */

  @Nullable
  @Override
  public INBT writeNBT(Capability<NamespacedNBT> capability, NamespacedNBT instance, Direction side) {
    return null;
  }

  @Override
  public void readNBT(Capability<NamespacedNBT> capability, NamespacedNBT instance, Direction side, INBT nbt) {}

  /** Capability provider instance */
  private static class Provider implements ICapabilitySerializable<CompoundNBT>, Runnable {
    private Lazy<CompoundNBT> nbt;
    private LazyOptional<NamespacedNBT> capability;
    private Provider() {
      this.nbt = Lazy.of(CompoundNBT::new);
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
    public CompoundNBT serializeNBT() {
      return nbt.get().copy();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
      this.nbt = Lazy.of(() -> nbt);
      run();
    }
  }
}
