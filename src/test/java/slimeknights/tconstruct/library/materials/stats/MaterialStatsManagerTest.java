package slimeknights.tconstruct.library.materials.stats;

import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.MergingJsonFileLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MaterialStatsManagerTest {
  private static final MaterialStatsId STATS_ID_SIMPLE = new MaterialStatsId("test", "stat");
  private static final MaterialStatsId STATS_ID_DONT_CARE = new MaterialStatsId("dont", "care");

  private final MaterialStatsManager materialStatsManager = new MaterialStatsManager();
  private final MergingJsonFileLoader<?> fileLoader = new MergingJsonFileLoader<>(materialStatsManager);

  @Test
  void testLoadFile_statsExist() {
    materialStatsManager.registerMaterialStat(new ComplexTestStats(STATS_ID_SIMPLE), ComplexTestStats.class);

    MaterialId material = new MaterialId(TConstruct.getResource("teststat"));
    fileLoader.loadAndParseFiles(null, material);

    Optional<BaseMaterialStats> optionalStats = materialStatsManager.getStats(material, STATS_ID_SIMPLE);
    assertThat(optionalStats).isPresent();
  }

  @Test
  void testLoadFile_complexStats() {
    materialStatsManager.registerMaterialStat(new ComplexTestStats(STATS_ID_SIMPLE), ComplexTestStats.class);

    MaterialId material = new MaterialId(TConstruct.getResource("teststat"));
    fileLoader.loadAndParseFiles(null, material);

    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(material, STATS_ID_SIMPLE);
    assertThat(optionalStats).isPresent();
    ComplexTestStats stats = optionalStats.get();
    assertThat(stats.getNum()).isEqualTo(123);
    assertThat(stats.getFloating()).isEqualTo(12.34f);
    assertThat(stats.getText()).isEqualTo("why would you ever do this for stats");
  }

  @Test
  void testLoadFile_multipleStatsInOneFile() {
    MaterialId material = new MaterialId(TConstruct.getResource("multiple"));
    MaterialStatsId statId1 = new MaterialStatsId("test", "stat1");
    materialStatsManager.registerMaterialStat(new ComplexTestStats(statId1), ComplexTestStats.class);
    MaterialStatsId statId2 = new MaterialStatsId("test", "stat2");
    materialStatsManager.registerMaterialStat(new ComplexTestStats(statId2), ComplexTestStats.class);

    fileLoader.loadAndParseFiles(null, material);

    assertThat(materialStatsManager.getStats(material, statId1)).isPresent();
    assertThat(materialStatsManager.getStats(material, statId2)).isPresent();
  }

  @Test
  void testLoadFileWithEmptyStats_ok() {
    MaterialId material = new MaterialId(TConstruct.getResource("empty"));
    fileLoader.loadAndParseFiles(null, material);

    // ensure that we get this far and that querying the missing material causes no errors
    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(material, STATS_ID_DONT_CARE);
    assertThat(optionalStats).isEmpty();
  }

  @Test
  void testLoadFileWithoutStats_ok() {
    MaterialId material = new MaterialId(TConstruct.getResource("missing_stats"));
    fileLoader.loadAndParseFiles(null, material);

    // ensure that we get this far and that querying the missing material causes no errors
    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(material, STATS_ID_DONT_CARE);
    assertThat(optionalStats).isEmpty();
  }

  @Test
  void testLoadMultipleFiles_addDifferentStatsToSameMaterial() {
    MaterialStatsId otherStatId = new MaterialStatsId("test", "otherstat");
    materialStatsManager.registerMaterialStat(new ComplexTestStats(STATS_ID_SIMPLE), ComplexTestStats.class);
    materialStatsManager.registerMaterialStat(new ComplexTestStats(otherStatId), ComplexTestStats.class);

    MaterialId material = new MaterialId(TConstruct.getResource("teststat"));
    fileLoader.loadAndParseFiles("extrastats", material);

    assertThat(materialStatsManager.getStats(material, STATS_ID_SIMPLE)).isNotEmpty();
    assertThat(materialStatsManager.getStats(material, otherStatId)).isNotEmpty();
  }

  // Tests the behaviour when multiple mods try to add the same material
  // the top data pack should override lower ones, meaning the duplicate stats are kept
  @Test
  void testLoadMultipleFiles_addSameStatsFromDifferentSources_useFirst() {
    materialStatsManager.registerMaterialStat(new ComplexTestStats(STATS_ID_SIMPLE), ComplexTestStats.class);

    MaterialId material = new MaterialId(TConstruct.getResource("teststat"));
    fileLoader.loadAndParseFiles("duplicate", material);

    Optional<ComplexTestStats> stats = materialStatsManager.getStats(material, STATS_ID_SIMPLE);
    assertThat(stats).isNotEmpty();
    assertThat(stats.get().getNum()).isEqualTo(321);
  }

  @Test
  void loadMissingFile_ignored() {
    MaterialId material = new MaterialId(TConstruct.getResource("nonexistant"));
    fileLoader.loadAndParseFiles(null);

    // ensure that we get this far and that querying the missing material causes no errors
    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(material, STATS_ID_DONT_CARE);
    assertThat(optionalStats).isEmpty();
  }

  @Test
  void loadFileWithOnlyUnregisteredStats_doNothing() {
    MaterialId material = new MaterialId(TConstruct.getResource("invalid"));
    fileLoader.loadAndParseFiles(null, material);

    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(material, new MaterialStatsId("test", "fails"));
    assertThat(optionalStats).isEmpty();
  }
}
