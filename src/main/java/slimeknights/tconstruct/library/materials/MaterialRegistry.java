package slimeknights.tconstruct.library.materials;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.PacketTarget;
import slimeknights.mantle.network.packet.ISimplePacket;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.events.MaterialsLoadedEvent;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialManager;
import slimeknights.tconstruct.library.materials.definition.UpdateMaterialsPacket;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsManager;
import slimeknights.tconstruct.library.materials.stats.UpdateMaterialStatsPacket;
import slimeknights.tconstruct.library.materials.traits.MaterialTraitsManager;
import slimeknights.tconstruct.library.materials.traits.UpdateMaterialTraitsPacket;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.RepairKitStats;
import slimeknights.tconstruct.tools.stats.SkullStats;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Function;

public final class MaterialRegistry {
  static MaterialRegistry INSTANCE;

  private final MaterialManager materialManager;
  private final MaterialStatsManager materialStatsManager;
  private final MaterialTraitsManager materialTraitsManager;
  private final IMaterialRegistry registry;

  // booleans to keep track of which packets the client has received
  private static boolean materialsLoaded = false;
  private static boolean statsLoaded = false;
  private static boolean traitsLoaded = false;
  /** True if the material registry is fully loaded on the client */
  @VisibleForTesting
  static boolean fullyLoaded = false;

  public static IMaterialRegistry getInstance() {
    return INSTANCE.registry;
  }

  public static void init() {
    // create registry instance
    INSTANCE = new MaterialRegistry();
    // add event listeners
    MinecraftForge.EVENT_BUS.addListener(INSTANCE::addDataPackListeners);
    MinecraftForge.EVENT_BUS.addListener(INSTANCE::onDatapackSync);
    // on the client, mark materials not fully loaded when the client logs out.
    // this also runs when starting a world in SP, but its early enough that the player login event will correct the state later (see handleLogin method)
    // TODO: is this still needed? disabled as it runs before the world finishes unloading in SP
    // DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LoggedOutEvent.class, e -> fullyLoaded = false));
  }

  /**
   * Returns true if the material registry is initialized
   * @return  True when initialized
   */
  public static boolean isFullyLoaded() {
    return INSTANCE != null && fullyLoaded;
  }

  public MaterialRegistry() {
    materialManager = new MaterialManager(() -> {
      materialsLoaded = true;
      checkAllLoaded();
    });
    materialStatsManager = new MaterialStatsManager(() -> {
      statsLoaded = true;
      checkAllLoaded();
    });
    materialTraitsManager = new MaterialTraitsManager(() -> {
      traitsLoaded = true;
      checkAllLoaded();
    });
    registry = new MaterialRegistryImpl(materialManager, materialStatsManager, materialTraitsManager);

    registry.registerStatType(HeadMaterialStats.DEFAULT, HeadMaterialStats.class, HeadMaterialStats::new);
    registry.registerStatType(HandleMaterialStats.DEFAULT, HandleMaterialStats.class, HandleMaterialStats::new);
    registry.registerStatType(ExtraMaterialStats.DEFAULT, ExtraMaterialStats.class, buffer -> ExtraMaterialStats.DEFAULT);
    registry.registerStatType(RepairKitStats.DEFAULT, RepairKitStats.class, RepairKitStats::new);
    registry.registerStatType(SkullStats.DEFAULT, SkullStats.class, SkullStats::new);
  }

  @VisibleForTesting
  MaterialRegistry(IMaterialRegistry registry) {
    this.registry = registry;
    this.materialManager = null;
    this.materialStatsManager = null;
    this.materialTraitsManager = null;
  }


  /* Networking */

  /**
   * Updates the material list from the server list. Should only be called client side
   * @param packet  Materials packet
   */
  public static void updateMaterialsFromServer(UpdateMaterialsPacket packet) {
    INSTANCE.materialManager.updateMaterialsFromServer(packet.getMaterials(), packet.getRedirects());
  }

  /**
   * Updates material stats from the server list. Should only be called client side
   * @param packet  Materials stats packet
   */
  public static void updateMaterialStatsFromServer(UpdateMaterialStatsPacket packet) {
    INSTANCE.materialStatsManager.updateMaterialStatsFromServer(packet.getMaterialToStats());
  }

  /**
   * Updates material traits from the server list. Should only be called client side
   * @param packet  Materials traits packet
   */
  public static void updateMaterialTraitsFromServer(UpdateMaterialTraitsPacket packet) {
    INSTANCE.materialTraitsManager.updateFromServer(packet.getMaterialToTraits());
  }


  /* Materials */

  /**
   * Gets a material by ID
   * @param id  Material ID
   * @return  Material, or IMaterial.UNKNOWN if missing
   */
  public static IMaterial getMaterial(MaterialId id) {
    return getInstance().getMaterial(id);
  }

  /**
   * Gets all currently registered materials
   * @return  Collection of all materials
   */
  public static Collection<IMaterial> getMaterials() {
    return INSTANCE.registry.getVisibleMaterials();
  }


  /* Stats */

  /**
   * Gets the class for a material stat ID
   * @param id  Material stat type
   * @return  Material stat class
   */
  @Nullable
  public static Class<? extends IMaterialStats> getClassForStat(MaterialStatsId id) {
    return INSTANCE.materialStatsManager.getClassForStat(id);
  }

  /**
   * Gets the class for a material stat ID
   * @param id  Material stat type
   * @return  Material stat class
   */
  @Nullable
  public static Function<FriendlyByteBuf,? extends IMaterialStats> getStatDecoder(MaterialStatsId id) {
    return INSTANCE.materialStatsManager.getStatDecoder(id);
  }


  /* Loading */

  /** Checks if all three material types have loaded, running callbacks if they have */
  private static void checkAllLoaded() {
    if (materialsLoaded && statsLoaded && traitsLoaded) {
      materialsLoaded = false;
      statsLoaded = false;
      traitsLoaded = false;
      fullyLoaded = true;
      MinecraftForge.EVENT_BUS.post(new MaterialsLoadedEvent());
    } else {
      fullyLoaded = false;
    }
  }

  /* Reloading */

  /** Adds the managers as datapack listeners */
  private void addDataPackListeners(final AddReloadListenerEvent event) {
    event.addListener(materialManager);
    event.addListener(materialStatsManager);
    event.addListener(materialTraitsManager);
  }

  /** Sends all relevant packets to the given player */
  private void sendPackets(ServerPlayer player, ISimplePacket[] packets) {
    // on an integrated server, the material registries have a single instance on both the client and the server thread
    // this means syncing is unneeded, and has the side-effect of recreating all the material instances (which can lead to unexpected behavior)
    // as a result, integrated servers just mark fullyLoaded as true without syncing anything, side-effect is listeners may run twice on single player

    // on a dedicated server, the client is running a separate game instance, this is where we send packets, plus fully loaded should already be true
    // this event is not fired when connecting to a server
    if (player.connection.getConnection().isMemoryConnection()) {
      // if the packet is being sent to ourself, skip sending, prevents recreating all material instances in the registry a second time on dedicated servers
      // note it will still send the packet if another client connects in LAN
      fullyLoaded = true;
      MinecraftForge.EVENT_BUS.post(new MaterialsLoadedEvent());
    } else {
      TinkerNetwork network = TinkerNetwork.getInstance();
      PacketTarget target = PacketDistributor.PLAYER.with(() -> player);
      for (ISimplePacket packet : packets) {
        network.send(target, packet);
      }
    }
  }

  /** Called when the player logs in to send packets */
  private void onDatapackSync(OnDatapackSyncEvent event) {
    ISimplePacket[] packets = {
      materialManager.getUpdatePacket(),
      materialStatsManager.getUpdatePacket(),
      materialTraitsManager.getUpdatePacket()
    };

    // send to single player
    ServerPlayer targetedPlayer = event.getPlayer();
    if (targetedPlayer != null) {
      sendPackets(targetedPlayer, packets);
    } else {
      // send to all players
      for (ServerPlayer player : event.getPlayerList().getPlayers()) {
        sendPackets(player, packets);
      }
    }
  }
}
