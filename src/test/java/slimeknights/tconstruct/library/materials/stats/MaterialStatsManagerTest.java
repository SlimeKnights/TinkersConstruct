package slimeknights.tconstruct.library.materials.stats;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.world.item.Tiers;
import slimeknights.mantle.data.listener.MergingJsonFileLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fixture.MaterialStatTypesFixture;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.dynamic.DynamicMaterialStats;
import slimeknights.tconstruct.library.materials.stats.dynamic.DynamicMaterialStatType;
import slimeknights.tconstruct.library.materials.stats.dynamic.DynamicStatField;
import slimeknights.tconstruct.library.materials.stats.dynamic.FloatDynamicStatField;
import slimeknights.tconstruct.library.materials.stats.dynamic.MaterialStatTypesLoader;
import slimeknights.tconstruct.library.materials.stats.dynamic.RepairableDynamicMaterialStats;
import slimeknights.tconstruct.library.materials.stats.dynamic.TierDynamicStatField;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.test.JsonFileLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MaterialStatsManagerTest extends BaseMcTest {
  private static final MaterialStatsId STATS_ID_SIMPLE = new MaterialStatsId("test", "stat");
  private static final MaterialStatType<ComplexTestStats> STATS_TYPE_SIMPLE = ComplexTestStats.makeType(STATS_ID_SIMPLE);
  private static final MaterialStatsId STATS_ID_DONT_CARE = new MaterialStatsId("dont", "care");

  private final MaterialStatsManager materialStatsManager = new MaterialStatsManager(() -> {});
  private final MergingJsonFileLoader<?> fileLoader = new MergingJsonFileLoader<>(materialStatsManager);
  private final MaterialStatTypesLoader anoFileLoader = new MaterialStatTypesLoader();

  @Test
  void testLoadFile_statsExist() {
    materialStatsManager.registerStatType(STATS_TYPE_SIMPLE);

    MaterialId material = new MaterialId(TConstruct.getResource("teststat"));
    fileLoader.loadAndParseFiles(null, material);

    Optional<IMaterialStats> optionalStats = materialStatsManager.getStats(material, STATS_ID_SIMPLE);
    assertThat(optionalStats).isPresent();
  }

  @Test
  void testLoadFile_complexStats() {
    materialStatsManager.registerStatType(STATS_TYPE_SIMPLE);

    MaterialId material = new MaterialId(TConstruct.getResource("teststat"));
    fileLoader.loadAndParseFiles(null, material);

    Optional<ComplexTestStats> optionalStats = materialStatsManager.getStats(material, STATS_ID_SIMPLE);
    assertThat(optionalStats).isPresent();
    ComplexTestStats stats = optionalStats.get();
    assertThat(stats.num()).isEqualTo(123);
    assertThat(stats.floating()).isEqualTo(12.34f);
    assertThat(stats.text()).isEqualTo("why would you ever do this for stats");
  }

  @Test
  void testLoadFile_multipleStatsInOneFile() {
    MaterialId material = new MaterialId(TConstruct.getResource("multiple"));
    MaterialStatsId statId1 = new MaterialStatsId("test", "stat1");
    materialStatsManager.registerStatType(ComplexTestStats.makeType(statId1, 1, 1f, "one"));
    MaterialStatsId statId2 = new MaterialStatsId("test", "stat2");
    materialStatsManager.registerStatType(ComplexTestStats.makeType(statId2, 2, 2f, "two"));

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
    materialStatsManager.registerStatType(STATS_TYPE_SIMPLE);
    materialStatsManager.registerStatType(ComplexTestStats.makeType(otherStatId, 5, 8, "other"));

    MaterialId material = new MaterialId(TConstruct.getResource("teststat"));
    fileLoader.loadAndParseFiles("extrastats", material);

    assertThat(materialStatsManager.getStats(material, STATS_ID_SIMPLE)).isNotEmpty();
    assertThat(materialStatsManager.getStats(material, otherStatId)).isNotEmpty();
  }

  // the top data pack should override lower ones, meaning the duplicate stats are kept
  @Test
  void testLoadMultipleFiles_addSameStatsFromDifferentSources_useFirst() {
    materialStatsManager.registerStatType(STATS_TYPE_SIMPLE);

    MaterialId material = new MaterialId(TConstruct.getResource("teststat"));
    fileLoader.loadAndParseFiles("duplicate", material);

    Optional<ComplexTestStats> stats = materialStatsManager.getStats(material, STATS_ID_SIMPLE);
    assertThat(stats).isNotEmpty();
    // ensure loadable context set the stat type
    assertThat(stats.get().getType()).isEqualTo(STATS_TYPE_SIMPLE);
    // top pack value replaces the bottom one
    assertThat(stats.get().num()).isEqualTo(321);
    // top pack unspecified value defaults to the bottom one
    assertThat(stats.get().floating()).isCloseTo(12.34f, Offset.strictOffset(0.01f));
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

  @Test
  void testLoadFile_removeStatOverride() {
    MaterialId material = new MaterialId(TConstruct.getResource("multiple"));
    MaterialStatsId statId1 = new MaterialStatsId("test", "stat1");
    materialStatsManager.registerStatType(ComplexTestStats.makeType(statId1, 1, 1f, "one"));
    MaterialStatsId statId2 = new MaterialStatsId("test", "stat2");
    materialStatsManager.registerStatType(ComplexTestStats.makeType(statId2, 2, 2f, "two"));

    fileLoader.loadAndParseFiles("remove", material);

    assertThat(materialStatsManager.getStats(material, statId1)).isPresent();
    assertThat(materialStatsManager.getStats(material, statId2)).isNotPresent();
  }

  @Test
  void testLoadFile_withDynamicStats() {
    try{
      DynamicStatField.REGISTRY.register(new TierDynamicStatField.TierDynamicStatDecoder());
      DynamicStatField.REGISTRY.register(new FloatDynamicStatField.FloatDynamicStatDecoder());
      ToolStats.register(MaterialStatTypesFixture.TestFloatStat);
      ToolStats.register(MaterialStatTypesFixture.TestTierStat);
    }
    catch(Exception t){
      //do nothing
    }
    finally{
      materialStatsManager.getStatTypesLoader().setStatTypes(new HashMap<>());
      MaterialStatsId statId1 = new MaterialStatsId(TConstruct.getResource("testtype1"));
      MaterialStatsId statId2 = new MaterialStatsId(TConstruct.getResource("testtype2"));
      MaterialId testStatType = new MaterialId(TConstruct.getResource("teststattype"));

      JsonFileLoader testFileLoader = new JsonFileLoader(MaterialStatTypesLoader.GSON, MaterialStatTypesLoader.FOLDER);
      Map<ResourceLocation, JsonElement> fakePrepareResult = testFileLoader.loadFilesAsSplashlist(statId1, statId2);
      anoFileLoader.apply(fakePrepareResult);;
      fileLoader.loadAndParseFiles(null, testStatType);

      assertThat(materialStatsManager.getStatType(statId1)).isNotNull();
      assertThat(materialStatsManager.getStatType(statId1)).isExactlyInstanceOf(DynamicMaterialStatType.class);
      assertThat(materialStatsManager.getStatType(statId1).getDefaultStats()).isExactlyInstanceOf(RepairableDynamicMaterialStats.class);
      RepairableDynamicMaterialStats defaultStats = (RepairableDynamicMaterialStats)materialStatsManager.getStatType(statId1).getDefaultStats();
      assertThat(defaultStats).isNotNull();
      assertThat(defaultStats.getStat("durability")).isExactlyInstanceOf(FloatDynamicStatField.FloatDynamicStat.class);
      FloatDynamicStatField.FloatDynamicStat durabilityStat = (FloatDynamicStatField.FloatDynamicStat)defaultStats.getStat("durability");
      assertThat(durabilityStat.getValue()).isEqualTo(1.0f);
      assertThat(defaultStats.getStat("mining_tier")).isExactlyInstanceOf(TierDynamicStatField.TierDynamicStat.class);
      TierDynamicStatField.TierDynamicStat miningTierStat = (TierDynamicStatField.TierDynamicStat)defaultStats.getStat("mining_tier");
      assertThat(miningTierStat.getValue()).isEqualTo(Tiers.DIAMOND);
      assertThat(materialStatsManager.getStatType(statId2)).isNotNull();
      assertThat(materialStatsManager.getStatType(statId2)).isExactlyInstanceOf(DynamicMaterialStatType.class);
      assertThat(materialStatsManager.getStats(testStatType, statId1)).isPresent();
      RepairableDynamicMaterialStats stats1 = (RepairableDynamicMaterialStats)materialStatsManager.getStats(testStatType, statId1).get();
      assertThat(stats1.getStat("durability")).isExactlyInstanceOf(FloatDynamicStatField.FloatDynamicStat.class);
      FloatDynamicStatField.FloatDynamicStat durabilityStat1 = (FloatDynamicStatField.FloatDynamicStat)stats1.getStat("durability");
      assertThat(durabilityStat1.getValue()).isEqualTo(123f);
      assertThat(stats1.getStat("mining_tier")).isExactlyInstanceOf(TierDynamicStatField.TierDynamicStat.class);
      TierDynamicStatField.TierDynamicStat miningTierStat1 = (TierDynamicStatField.TierDynamicStat)stats1.getStat("mining_tier");
      assertThat(miningTierStat1.getValue()).isEqualTo(Tiers.STONE);
      assertThat(stats1.durability()).isEqualTo(123);

      assertThat(materialStatsManager.getStats(testStatType, statId2)).isPresent();
      DynamicMaterialStats stats2 = (DynamicMaterialStats)materialStatsManager.getStats(testStatType, statId2).get();
      assertThat(stats2.getStat("mining_speed")).isExactlyInstanceOf(FloatDynamicStatField.FloatDynamicStat.class);
      FloatDynamicStatField.FloatDynamicStat miningSpeedStat2 = (FloatDynamicStatField.FloatDynamicStat)stats2.getStat("mining_speed");
      assertThat(miningSpeedStat2.getValue()).isEqualTo(45.67f);
      assertThat(stats2.getStat("durability")).isExactlyInstanceOf(FloatDynamicStatField.FloatDynamicStat.class);
      FloatDynamicStatField.FloatDynamicStat durabilityStat2 = (FloatDynamicStatField.FloatDynamicStat)stats2.getStat("durability");
      assertThat(durabilityStat2.getValue()).isEqualTo(0f);
    }
  }
}
