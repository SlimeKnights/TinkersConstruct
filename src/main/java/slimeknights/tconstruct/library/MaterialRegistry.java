package slimeknights.tconstruct.library;

import com.google.common.annotations.VisibleForTesting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.MaterialManager;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsManager;
import slimeknights.tconstruct.library.traits.MaterialTraitsManager;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.Collection;
import java.util.Map;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MaterialRegistry {

  protected static MaterialRegistry INSTANCE;

  private final MaterialManager materialManager;
  private final MaterialStatsManager materialStatsManager;
  private final MaterialTraitsManager materialTraitsManager;
  private final IMaterialRegistry registry;

  public static IMaterialRegistry getInstance() {
    return INSTANCE.registry;
  }

  public static void init() {
    MaterialRegistry.INSTANCE = new MaterialRegistry();
  }

  @SubscribeEvent
  public static void onServerAboutToStart(final FMLServerAboutToStartEvent event) {
    event.getServer().getResourceManager().addReloadListener(INSTANCE.materialManager);
    event.getServer().getResourceManager().addReloadListener(INSTANCE.materialStatsManager);
    event.getServer().getResourceManager().addReloadListener(INSTANCE.materialTraitsManager);
  }

  public MaterialRegistry() {
    materialManager = new MaterialManager();
    materialStatsManager = new MaterialStatsManager();
    materialTraitsManager = new MaterialTraitsManager();
    registry = new MaterialRegistryImpl(materialManager, materialStatsManager, materialTraitsManager);

    registry.registerMaterial(HeadMaterialStats.DEFAULT, HeadMaterialStats.class);
    registry.registerMaterial(HandleMaterialStats.DEFAULT, HandleMaterialStats.class);
    registry.registerMaterial(ExtraMaterialStats.DEFAULT, ExtraMaterialStats.class);
  }

  @VisibleForTesting
  MaterialRegistry(IMaterialRegistry registry) {
    this.registry = registry;
    this.materialManager = null;
    this.materialStatsManager = null;
    this.materialTraitsManager = null;
  }

  @OnlyIn(Dist.CLIENT)
  static void updateMaterialsFromServer(Collection<IMaterial> materials) {
    INSTANCE.materialManager.updateMaterialsFromServer(materials);
  }

  @OnlyIn(Dist.CLIENT)
  static void updateMaterialStatsFromServer(Map<MaterialId, Collection<IMaterialStats>> materialStats) {
    INSTANCE.materialStatsManager.updateMaterialStatsFromServer(materialStats);
  }

  @OnlyIn(Dist.CLIENT)
  static Class<? extends IMaterialStats> getClassForStat(MaterialStatsId id) {
    return INSTANCE.materialStatsManager.getClassForStat(id);
  }

  public static IMaterial getMaterial(MaterialId id) {
    return INSTANCE.registry.getMaterial(id);
  }
//
//  public static Collection<IMaterial> getMaterials() {
//    return INSTANCE.getMaterials();
//  }
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
