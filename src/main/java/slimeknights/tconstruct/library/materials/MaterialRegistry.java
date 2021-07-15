package slimeknights.tconstruct.library.materials;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.PacketTarget;
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

import javax.annotation.Nullable;
import java.util.Collection;

public final class MaterialRegistry {
  protected static MaterialRegistry INSTANCE;

  private final MaterialManager materialManager;
  private final MaterialStatsManager materialStatsManager;
  private final MaterialTraitsManager materialTraitsManager;
  private final IMaterialRegistry registry;

  // booleans to keep track of which packets the client has received
  private static boolean materialsLoaded = false;
  private static boolean statsLoaded = false;
  private static boolean traitsLoaded = false;
  /** True if the material registry is fully loaded on the client */
  private static boolean fullyLoaded = false;

  public static IMaterialRegistry getInstance() {
    return INSTANCE.registry;
  }

  public static void init() {
    // create registry instance
    INSTANCE = new MaterialRegistry();
    // add event listeners
    MinecraftForge.EVENT_BUS.addListener(INSTANCE::addDataPackListeners);
    MinecraftForge.EVENT_BUS.addListener(INSTANCE::handleLogin);
    // on the client, mark materials not fully loaded when the client logs out.
    // this also runs when starting a world in SP, but its early enough that the player login event will correct the state later (see handleLogin method)
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LoggedOutEvent.class, e -> fullyLoaded = false));
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

    registry.registerStatType(HeadMaterialStats.DEFAULT, HeadMaterialStats.class);
    registry.registerStatType(HandleMaterialStats.DEFAULT, HandleMaterialStats.class);
    registry.registerStatType(ExtraMaterialStats.DEFAULT, ExtraMaterialStats.class);
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
    INSTANCE.materialManager.updateMaterialsFromServer(packet.getMaterials());
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

  /** Called when the player logs in to send packets */
  private void handleLogin(PlayerLoggedInEvent event) {
    PlayerEntity player = event.getPlayer();
    // on an integrated server, the material registries have a single instance on both the client and the server thread
    // this means syncing is unneeded, and has the side-effect of recreating all the material instances (which can lead to unexpected behavior)
    // as a result, integrated servers just mark fullyLoaded as true without syncing anything, side-effect is listeners may run twice on single player

    // on a dedicated server, the client is running a separate game instance, this is where we send packets, plus fully loaded should already be true
    // this event is not fired when connecting to a server

    // when a client is connecting to a dedicated server, this event does not fire at all client side
    if (FMLEnvironment.dist == Dist.CLIENT) {
      fullyLoaded = true;
      MinecraftForge.EVENT_BUS.post(new MaterialsLoadedEvent());
    } else if (player instanceof ServerPlayerEntity) {
      ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
      TinkerNetwork network = TinkerNetwork.getInstance();
      PacketTarget target = PacketDistributor.PLAYER.with(() -> serverPlayer);
      network.send(target, materialManager.getUpdatePacket());
      network.send(target, materialStatsManager.getUpdatePacket());
      network.send(target, materialTraitsManager.getUpdatePacket());
    }
  }
}
