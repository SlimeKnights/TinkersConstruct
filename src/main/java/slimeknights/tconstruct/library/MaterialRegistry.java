package slimeknights.tconstruct.library;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.fluid.Fluid;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.MaterialManager;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsManager;
import slimeknights.tconstruct.library.network.UpdateMaterialStatsPacket;
import slimeknights.tconstruct.library.network.UpdateMaterialsPacket;
import slimeknights.tconstruct.library.traits.MaterialTraitsManager;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.Collection;

public final class MaterialRegistry {

  public static MaterialRegistry INSTANCE;

  public final MaterialManager materialManager;
  public final MaterialStatsManager materialStatsManager;
  private final MaterialTraitsManager materialTraitsManager;
  private final IMaterialRegistry registry;

  public static IMaterialRegistry getInstance() {
    return INSTANCE.registry;
  }

  public static void init() {
    MaterialRegistry.INSTANCE = new MaterialRegistry();
  }

  /**
   * Returns true if the material registry is initialized
   * @return  True when initialized
   */
  public static boolean initialized() {
    return INSTANCE != null;
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
    INSTANCE.registry.onMaterialSync(); // called on stat reload as it should happen second
  }

  /**
   * Gets the class for a material stat ID
   * @param id  Material stat type
   * @return  Material stat class
   */
  public static Class<? extends IMaterialStats> getClassForStat(MaterialStatsId id) {
    return INSTANCE.materialStatsManager.getClassForStat(id);
  }

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
//
//  public static <T extends IMaterialStats> Optional<T> getMaterialStats(MaterialId materialId, MaterialStatsId statsId) {
//    return INSTANCE.getMaterialStats(materialId, statsId);
//  }
//
//  public static <T extends IMaterialStats> T getDefaultStats(MaterialStatsId statsId) {
//    return INSTANCE.getDefaultStats(statsId);
//  }
//
//  public static Collection<IMaterialStats> getAllStats(MaterialId materialId) {
//    return INSTANCE.getAllStats(materialId);
//  }

  /**
   * Convenience method. Default stats for all part types must exist, to be used when an invalid material with missing stats is used.
   */
//  static <T extends IMaterialStats> T getDefaultStatsForType(MaterialStatType partType) {
//    return (T) UNKNOWN.getStatsForType(partType).orElseThrow(() -> new IllegalStateException("Trying to get the fallback materials stats for a type that doesn't exist. You're either using something unregistered or some external influence messed things up, since that's impossible by design."));
//  }

  /**
   * Obtain the stats for the given part type.
   * Those are usually used to calculate the stats of a tool.
   * If an empty optional is returned it means this material is not fit to be used for this part type.
   *
   * @return Optional containing the stats, or empty optional if there are no stats for the given type.
   */
  //<T extends IMaterialStats> Optional<T> getStatsForType(MaterialStatType partType);

  /**
   * Get the traits that shall be added to a tool if the given part type is used.
   *
   * @return List of traits to be used.
   */
  //List<ITrait> getAllTraitsForStats(MaterialStatType partType);

  /**
   * All stats available with this material. Usually only used for display purposes.
   *
   * @return A collection of all stats registered with this material. Usually ordered, but not guaranteed.
   */
  //Collection<IMaterialStats> getAllStats();

  /**
   * All traits possible with this material, regardless of part type.
   * Usually only used for display purposes.
   *
   * @return A collection of all traits registered with this material. Usually ordered, but not guaranteed.
   */
  //Collection<ITrait> getAllTraits();
}
