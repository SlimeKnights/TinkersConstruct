package slimeknights.tconstruct.library;

import com.google.common.annotations.VisibleForTesting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.MaterialManager;
import slimeknights.tconstruct.library.materials.UpdateMaterialsPacket;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
  private static final List<Runnable> onMaterialReload = new ArrayList<>();

  public static IMaterialRegistry getInstance() {
    return INSTANCE.registry;
  }

  public static void init() {
    MaterialRegistry.INSTANCE = new MaterialRegistry();
    MinecraftForge.EVENT_BUS.addListener(MaterialRegistry::addDataPackListeners);
    // on the client, mark materials not fully loaded when the client logs out. This also runs when starting a world in SP, but its early enough to not be an issue
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LoggedOutEvent.class, e -> fullyLoaded = false));
  }

  /**
   * Returns true if the material registry is initialized
   * @return  True when initialized
   */
  public static boolean isFullyLoaded() {
    return INSTANCE != null && fullyLoaded;
  }

  /** Adds the managers as datapack listeners */
  private static void addDataPackListeners(final AddReloadListenerEvent event) {
    event.addListener(INSTANCE.materialManager);
    event.addListener(INSTANCE.materialStatsManager);
    event.addListener(INSTANCE.materialTraitsManager);
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
    return INSTANCE.registry.getMaterial(id);
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

  /**
   * Adds a runnable called when materials are all reloaded
   * @param listener  Runnable to call
   */
  public static void addMaterialsLoadedListener(Runnable listener) {
    onMaterialReload.add(listener);
  }

  /** Checks if all three material types have loaded, running callbacks if they have */
  private static void checkAllLoaded() {
    if (materialsLoaded && statsLoaded && traitsLoaded) {
      materialsLoaded = false;
      statsLoaded = false;
      traitsLoaded = false;
      fullyLoaded = true;
      for (Runnable runnable : onMaterialReload) {
        runnable.run();
      }
    } else {
      fullyLoaded = false;
    }
  }
}
