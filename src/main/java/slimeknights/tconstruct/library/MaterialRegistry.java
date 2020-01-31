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
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsManager;
import slimeknights.tconstruct.library.traits.MaterialTraitsManager;
import slimeknights.tconstruct.tools.stats.CommonMaterialStats;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MaterialRegistry {

  private static MaterialManager MATERIAL_MANAGER;
  private static MaterialStatsManager MATERIAL_STATS_MANAGER;
  private static MaterialTraitsManager MATERIAL_TRAITS_MANAGER;
  @VisibleForTesting
  protected static MaterialRegistryImpl INSTANCE;

  public static void init() {
    MATERIAL_MANAGER = new MaterialManager();
    MATERIAL_STATS_MANAGER = new MaterialStatsManager();
    MATERIAL_TRAITS_MANAGER = new MaterialTraitsManager();
    INSTANCE = new MaterialRegistryImpl(MATERIAL_MANAGER, MATERIAL_STATS_MANAGER, MATERIAL_TRAITS_MANAGER);

    CommonMaterialStats commonMaterialStats = new CommonMaterialStats(1, 1);
    INSTANCE.registerMaterial(new MaterialStatsId(Util.getResource("common")), commonMaterialStats, CommonMaterialStats.class);
  }

  @SubscribeEvent
  public static void onServerAboutToStart(final FMLServerAboutToStartEvent event) {
    event.getServer().getResourceManager().addReloadListener(MATERIAL_MANAGER);
    event.getServer().getResourceManager().addReloadListener(MATERIAL_STATS_MANAGER);
    event.getServer().getResourceManager().addReloadListener(MATERIAL_TRAITS_MANAGER);
  }

  @OnlyIn(Dist.CLIENT)
  static void updateMaterialsFromServer(Collection<IMaterial> materials) {
    MATERIAL_MANAGER.updateMaterialsFromServer(materials);
  }

  @OnlyIn(Dist.CLIENT)
  static void updateMaterialStatsFromServer(Map<MaterialId, Collection<BaseMaterialStats>> materialStats) {
    MATERIAL_STATS_MANAGER.updateMaterialStatsFromServer(materialStats);
  }

  @OnlyIn(Dist.CLIENT)
  static Class<? extends IMaterialStats> getClassForStat(MaterialStatsId id) {
    return MATERIAL_STATS_MANAGER.getClassForStat(id);
  }

  public static IMaterial getMaterial(MaterialId id) {
    return INSTANCE.getMaterial(id);
  }

  public static Collection<IMaterial> getMaterials() {
    return INSTANCE.getMaterials();
  }

  public static <T extends IMaterialStats> Optional<T> getMaterialStats(MaterialId materialId, MaterialStatsId statsId) {
    return INSTANCE.getMaterialStats(materialId, statsId);
  }

  public static <T extends IMaterialStats> T getDefaultStats(MaterialStatsId statsId) {
    return INSTANCE.getDefaultStats(statsId);
  }

  public static Collection<BaseMaterialStats> getAllStats(MaterialId materialId) {
    return INSTANCE.getAllStats(materialId);
  }

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
