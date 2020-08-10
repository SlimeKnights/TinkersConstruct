package slimeknights.tconstruct.library.materials.stats;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.test.JsonFileLoader;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MaterialStatsManagerTest extends BaseMcTest {

  private static final MaterialId MATERIAL_ID = new MaterialId(Util.MODID, "material");
  private static final MaterialStatsId STATS_ID_SIMPLE = new MaterialStatsId("test", "stat");
  private static final MaterialStatsId STATS_ID_DONT_CARE = new MaterialStatsId("dont", "care");

  private MaterialStatsManager materialStatsManager = new MaterialStatsManager(mock(TinkerNetwork.class));
  private JsonFileLoader fileLoader = new JsonFileLoader(MaterialStatsManager.GSON, MaterialStatsManager.FOLDER);

  @Test
  void testLoadFile_statsExist() {
    materialStatsManager.registerMaterialStat(STATS_ID_SIMPLE, ComplexTestStats.class);

    ResourceLocation file = Util.getResource("teststat");
    Map<ResourceLocation,JsonElement> splashList = fileLoader.loadFilesAsSplashlist(file);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<BaseMaterialStats> optionalStats = materialStatsManager.getStats(MATERIAL_ID, STATS_ID_SIMPLE);
    assertThat(optionalStats).isNotEmpty();
  }

  @Test
  void testLoadFile_complexStats() {
    materialStatsManager.registerMaterialStat(STATS_ID_SIMPLE, ComplexTestStats.class);

    ResourceLocation file = Util.getResource("teststat");
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist(file);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(MATERIAL_ID, STATS_ID_SIMPLE);
    assertThat(optionalStats).isNotEmpty();
    ComplexTestStats stats = optionalStats.get();
    assertThat(stats.getNum()).isEqualTo(123);
    assertThat(stats.getFloating()).isEqualTo(12.34f);
    assertThat(stats.getText()).isEqualTo("why would you ever do this for stats");
  }

  @Test
  void testLoadFile_multipleStatsInOneFile() {
    ResourceLocation file = Util.getResource("multiple");
    MaterialStatsId statId1 = new MaterialStatsId("test", "stat1");
    materialStatsManager.registerMaterialStat(statId1, ComplexTestStats.class);
    MaterialStatsId statId2 = new MaterialStatsId("test", "stat2");
    materialStatsManager.registerMaterialStat(statId2, ComplexTestStats.class);

    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist(file);
    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    assertThat(materialStatsManager.getStats(MATERIAL_ID, statId1)).isNotEmpty();
    assertThat(materialStatsManager.getStats(MATERIAL_ID, statId2)).isNotEmpty();
  }

  @Test
  void testLoadFileWithEmptyStats_ok() {
    ResourceLocation file = Util.getResource("empty");
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist(file);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    // ensure that we get this far and that querying the missing material causes no errors
    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(MATERIAL_ID, STATS_ID_DONT_CARE);
    assertThat(optionalStats).isEmpty();
  }

  @Test
  void testLoadFileWithoutStats_ok() {
    ResourceLocation file = Util.getResource("missing_stats");
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist(file);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    // ensure that we get this far and that querying the missing material causes no errors
    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(MATERIAL_ID, STATS_ID_DONT_CARE);
    assertThat(optionalStats).isEmpty();
  }

  @Test
  void testLoadMultipleFiles_addDifferentStatsToSameMaterial() {
    ResourceLocation file1 = Util.getResource("teststat");
    ResourceLocation file2 = Util.getResource("teststat_extrastats");
    MaterialStatsId otherStatId = new MaterialStatsId("test", "otherstat");
    materialStatsManager.registerMaterialStat(STATS_ID_SIMPLE, ComplexTestStats.class);
    materialStatsManager.registerMaterialStat(otherStatId, ComplexTestStats.class);

    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist(file1, file2);
    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    assertThat(materialStatsManager.getStats(MATERIAL_ID, STATS_ID_SIMPLE)).isNotEmpty();
    assertThat(materialStatsManager.getStats(MATERIAL_ID, otherStatId)).isNotEmpty();
  }

  // Tests the behaviour when multiple mods try to add the same material
  // we use "keep first" to ensure that existence is actually checked in the code
  // since the default behaviour for maps would be to overwrite the existing value
  @Test
  void testLoadMultipleFiles_addSameStatsFromDifferentSources_useFirst() {
    ResourceLocation file1 = Util.getResource("teststat");
    ResourceLocation file2 = Util.getResource("teststat_duplicate");
    materialStatsManager.registerMaterialStat(STATS_ID_SIMPLE, ComplexTestStats.class);

    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist(file1, file2);
    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<ComplexTestStats> stats = materialStatsManager.getStats(MATERIAL_ID, STATS_ID_SIMPLE);
    assertThat(stats).isNotEmpty();
    assertThat(stats.get().getNum()).isEqualTo(123);
  }

  @Test
  void loadMissingFile_ignored() {
    ResourceLocation file = Util.getResource("nonexistant");
    Map<ResourceLocation, JsonElement> splashList = ImmutableMap.of(file, new JsonObject());

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    // ensure that we get this far and that querying the missing material causes no errors
    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(MATERIAL_ID, STATS_ID_DONT_CARE);
    assertThat(optionalStats).isEmpty();
  }

  @Test
  void loadFileWithOnlyUnregisteredStats_doNothing() {
    ResourceLocation file = Util.getResource("invalid");
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist(file);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(MATERIAL_ID, new MaterialStatsId("test", "fails"));
    assertThat(optionalStats).isEmpty();
  }

  @Test
  void loadStatsWithMissingId_doNothing() {
    ResourceLocation file = Util.getResource("missing_stat_id");
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist(file);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(MATERIAL_ID, new MaterialStatsId("test", "fails"));
    assertThat(optionalStats).isEmpty();
  }

}
