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

  private MaterialStatsManager materialStatsManager = new MaterialStatsManager();
  private JsonFileLoader fileLoader = new JsonFileLoader(MaterialStatsManager.GSON, MaterialStatsManager.FOLDER);

  @Test
  void testLoadFile_statsExist() {
    // material id doubles as filename
    MaterialId materialId = new MaterialId(Util.getResource("teststat"));
    MaterialStatsId statId = new MaterialStatsId("test", "stat");
    materialStatsManager.registerMaterialStat(statId, BaseMaterialStats.class);
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<BaseMaterialStats> optionalStats = materialStatsManager.getStats(materialId, statId);
    assertThat(optionalStats).isNotEmpty();
    assertThat(optionalStats.get().getIdentifier()).isEqualByComparingTo(statId);
  }

  @Test
  void testLoadFile_complexStats() {
    // material id doubles as filename
    MaterialId materialId = new MaterialId(Util.getResource("teststat"));
    MaterialStatsId statId = new MaterialStatsId("test", "stat");
    materialStatsManager.registerMaterialStat(statId, ComplexTestStats.class);
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(materialId, statId);
    assertThat(optionalStats).isNotEmpty();
    ComplexTestStats stats = optionalStats.get();
    assertThat(stats.num).isEqualTo(123);
    assertThat(stats.floating).isEqualTo(12.34f);
    assertThat(stats.text).isEqualTo("why would you ever do this for stats");
  }

  @Test
  void testLoadFile_multipleStatsInOneFile() {
    // material id doubles as filename
    MaterialId materialId = new MaterialId(Util.getResource("multiple"));
    MaterialStatsId statId1 = new MaterialStatsId("test", "stat1");
    materialStatsManager.registerMaterialStat(statId1, BaseMaterialStats.class);
    MaterialStatsId statId2 = new MaterialStatsId("test", "stat2");
    materialStatsManager.registerMaterialStat(statId2, BaseMaterialStats.class);
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    assertThat(materialStatsManager.getStats(materialId, statId1)).isNotEmpty();
    assertThat(materialStatsManager.getStats(materialId, statId2)).isNotEmpty();
  }

  @Test
  void testLoadFileWithoutStats_ok() {
    // material id doubles as filename
    MaterialId materialId = new MaterialId(Util.getResource("empty"));
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    // ensure that we get this far and that querying the missing material causes no errors
    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(materialId, new MaterialStatsId("dont", "care"));
    assertThat(optionalStats).isEmpty();
  }

  @Test
  void loadMissingFile_ignored() {
    MaterialId materialId = new MaterialId(Util.getResource("nonexistant"));
    Map<ResourceLocation, JsonObject> splashList = ImmutableMap.of(materialId, new JsonObject());

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    // ensure that we get this far and that querying the missing material causes no errors
    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(materialId, new MaterialStatsId("dont", "care"));
    assertThat(optionalStats).isEmpty();
  }

  @Test
  void loadFileWithOnlyUnregisteredStats_doNothing() {
    MaterialId materialId = new MaterialId(Util.getResource("invalid"));
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(materialId, new MaterialStatsId("test", "fails"));
    assertThat(optionalStats).isEmpty();
  }

  @Test
  void loadStatsWithMissingId_doNothing() {
    MaterialId materialId = new MaterialId(Util.getResource("missing_stat_id"));
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(materialId, new MaterialStatsId("test", "fails"));
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
