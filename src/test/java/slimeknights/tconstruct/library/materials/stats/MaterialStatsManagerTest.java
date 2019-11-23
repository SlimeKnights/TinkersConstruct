package slimeknights.tconstruct.library.materials.stats;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialId;
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

  private MaterialStatsManager materialStatsManager = new MaterialStatsManager();
  private JsonFileLoader fileLoader = new JsonFileLoader(MaterialStatsManager.GSON, MaterialStatsManager.FOLDER);

  @Test
  void testLoadFile_statsExist() {
    materialStatsManager.registerMaterialStat(STATS_ID_SIMPLE, BaseMaterialStats.class);

    ResourceLocation file = Util.getResource("teststat");
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(file);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<BaseMaterialStats> optionalStats = materialStatsManager.getStats(MATERIAL_ID, STATS_ID_SIMPLE);
    assertThat(optionalStats).isNotEmpty();
    assertThat(optionalStats.get().getIdentifier()).isEqualByComparingTo(STATS_ID_SIMPLE);
  }

  @Test
  void testLoadFile_complexStats() {
    materialStatsManager.registerMaterialStat(STATS_ID_SIMPLE, ComplexTestStats.class);

    ResourceLocation file = Util.getResource("teststat");
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(file);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(MATERIAL_ID, STATS_ID_SIMPLE);
    assertThat(optionalStats).isNotEmpty();
    ComplexTestStats stats = optionalStats.get();
    assertThat(stats.num).isEqualTo(123);
    assertThat(stats.floating).isEqualTo(12.34f);
    assertThat(stats.text).isEqualTo("why would you ever do this for stats");
  }

  @Test
  void testLoadFile_multipleStatsInOneFile() {
    ResourceLocation file = Util.getResource("multiple");
    MaterialStatsId statId1 = new MaterialStatsId("test", "stat1");
    materialStatsManager.registerMaterialStat(statId1, BaseMaterialStats.class);
    MaterialStatsId statId2 = new MaterialStatsId("test", "stat2");
    materialStatsManager.registerMaterialStat(statId2, BaseMaterialStats.class);

    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(file);
    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    assertThat(materialStatsManager.getStats(MATERIAL_ID, statId1)).isNotEmpty();
    assertThat(materialStatsManager.getStats(MATERIAL_ID, statId2)).isNotEmpty();
  }

  @Test
  void testLoadFileWithEmptyStats_ok() {
    ResourceLocation file = Util.getResource("empty");
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(file);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    // ensure that we get this far and that querying the missing material causes no errors
    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(MATERIAL_ID, STATS_ID_DONT_CARE);
    assertThat(optionalStats).isEmpty();
  }

  @Test
  void testLoadFileWithoutStats_ok() {
    ResourceLocation file = Util.getResource("missing_stats");
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(file);

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
    materialStatsManager.registerMaterialStat(STATS_ID_SIMPLE, BaseMaterialStats.class);
    materialStatsManager.registerMaterialStat(otherStatId, BaseMaterialStats.class);

    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(file1, file2);
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

    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(file1, file2);
    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<ComplexTestStats> stats = materialStatsManager.getStats(MATERIAL_ID, STATS_ID_SIMPLE);
    assertThat(stats).isNotEmpty();
    assertThat(stats.get().num).isEqualTo(123);
  }

  @Test
  void loadMissingFile_ignored() {
    ResourceLocation file = Util.getResource("nonexistant");
    Map<ResourceLocation, JsonObject> splashList = ImmutableMap.of(file, new JsonObject());

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    // ensure that we get this far and that querying the missing material causes no errors
    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(MATERIAL_ID, STATS_ID_DONT_CARE);
    assertThat(optionalStats).isEmpty();
  }

  @Test
  void loadFileWithOnlyUnregisteredStats_doNothing() {
    ResourceLocation file = Util.getResource("invalid");
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(file);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(MATERIAL_ID, new MaterialStatsId("test", "fails"));
    assertThat(optionalStats).isEmpty();
  }

  @Test
  void loadStatsWithMissingId_doNothing() {
    ResourceLocation file = Util.getResource("missing_stat_id");
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(file);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(MATERIAL_ID, new MaterialStatsId("test", "fails"));
    assertThat(optionalStats).isEmpty();
  }

  private static class ComplexTestStats extends BaseMaterialStats {

    private final int num;
    private final float floating;
    private final String text;

    public ComplexTestStats(MaterialStatsId identifier, int num, float floating, String text) {
      super(identifier);
      this.num = num;
      this.floating = floating;
      this.text = text;
    }
  }
}
