package slimeknights.tconstruct.library;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
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

import java.util.Collection;

public final class MaterialRegistry {

  protected static MaterialRegistry INSTANCE;

  private final MaterialManager materialManager;
  private final MaterialStatsManager materialStatsManager;
  private final MaterialTraitsManager materialTraitsManager;
  private final IMaterialRegistry registry;
  // booleans to keeep track of which packets the client has received
  private boolean materialsSynced = false;
  private boolean statsSynced = false;
  private boolean traitsSynced = false;

  public static IMaterialRegistry getInstance() {
    return INSTANCE.registry;
  }

  public static void init() {
    MaterialRegistry.INSTANCE = new MaterialRegistry();
    MinecraftForge.EVENT_BUS.addListener(MaterialRegistry::addDataPackListeners);
  }

  /**
   * Returns true if the material registry is initialized
   * @return  True when initialized
   */
  public static boolean initialized() {
    return INSTANCE != null;
  }

  /** Adds the managers as datapack listeners */
  private static void addDataPackListeners(final AddReloadListenerEvent event) {
    event.addListener(INSTANCE.materialManager);
    event.addListener(INSTANCE.materialStatsManager);
    event.addListener(INSTANCE.materialTraitsManager);
  }

  public MaterialRegistry() {
    materialManager = new MaterialManager();
    materialStatsManager = new MaterialStatsManager();
    materialTraitsManager = new MaterialTraitsManager();
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

  /** Checks if all three types have synced, ensures we can receive the three packets in any order */
  private void checkSync() {
    if (materialsSynced && statsSynced && traitsSynced) {
      registry.onMaterialSync();
      materialsSynced = false;
      statsSynced = false;
      traitsSynced = false;
    }
  }

  /**
   * Updates the material list from the server list. Should only be called client side
   * @param packet  Materials packet
   */
  public static void updateMaterialsFromServer(UpdateMaterialsPacket packet) {
    INSTANCE.materialManager.updateMaterialsFromServer(packet.getMaterials());
    INSTANCE.materialsSynced = true;
    INSTANCE.checkSync();
  }

  /**
   * Updates material stats from the server list. Should only be called client side
   * @param packet  Materials stats packet
   */
  public static void updateMaterialStatsFromServer(UpdateMaterialStatsPacket packet) {
    INSTANCE.materialStatsManager.updateMaterialStatsFromServer(packet.getMaterialToStats());
    INSTANCE.statsSynced = true;
    INSTANCE.checkSync();
  }

  /**
   * Updates material traits from the server list. Should only be called client side
   * @param packet  Materials traits packet
   */
  public static void updateMaterialTraitsFromServer(UpdateMaterialTraitsPacket packet) {
    INSTANCE.materialTraitsManager.updateFromServer(packet.getMaterialToTraits());
    INSTANCE.traitsSynced = true;
    INSTANCE.checkSync();
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
   * Gets a material by fluid lookup
   * @param fluid  Fluid instance
   * @return  Material, or IMateiral.UNKNOWN if none match the fluid
   */
  public static IMaterial getMaterial(Fluid fluid) {
    return INSTANCE.registry.getMaterial(fluid);
  }

  /**
   * Gets all currently registered materials
   * @return  Collection of all materials
   */
  public static Collection<IMaterial> getMaterials() {
    return INSTANCE.registry.getMaterials();
  }


  /* Stats */

  /**
   * Gets the class for a material stat ID
   * @param id  Material stat type
   * @return  Material stat class
   */
  public static Class<? extends IMaterialStats> getClassForStat(MaterialStatsId id) {
    return INSTANCE.materialStatsManager.getClassForStat(id);
  }
}
