package slimeknights.tconstruct.library.traits;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.test.JsonFileLoader;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MaterialTraitsManagerTest extends BaseMcTest {

  private static final MaterialId MATERIAL_ID = new MaterialId(Util.getResource("material"));
  private static final ResourceLocation MULTIPLE_TRAITS_FILE = Util.getResource("multiple");
  private static final ResourceLocation SIMPLE_TRAIT_FILE = Util.getResource("traitstest");
  private static final ResourceLocation SIMPLE_TRAIT_FILE2 = Util.getResource("traitstest2");

  private MaterialTraitsManager traitsManager = new MaterialTraitsManager();
  private JsonFileLoader fileLoader = new JsonFileLoader(MaterialTraitsManager.GSON, MaterialTraitsManager.FOLDER);

  @Test
  void defaultTraitsAreLoaded() {
    Map<ResourceLocation,JsonElement> splashList = fileLoader.loadFilesAsSplashlist(SIMPLE_TRAIT_FILE);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<TraitId> defaultTraits = traitsManager.getDefaultTraits(MATERIAL_ID);
    assertThat(defaultTraits).isNotEmpty();
    assertThat(defaultTraits).contains(new TraitId("test1", "trait1"));
  }

  @Test
  void perStatsTraitsAreLoaded() {
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist(SIMPLE_TRAIT_FILE);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<TraitId> statTraits = traitsManager.getTraitsForStats(MATERIAL_ID, new MaterialStatsId(Util.getResource("teststat")));
    assertThat(statTraits).isNotEmpty();
    assertThat(statTraits).contains(new TraitId("test2", "trait2"));
  }

  @Test
  void defaultWithMultipleTraits_singleFile() {
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist(MULTIPLE_TRAITS_FILE);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<TraitId> defaultTraits = traitsManager.getDefaultTraits(MATERIAL_ID);
    assertThat(defaultTraits).isNotEmpty();
    assertThat(defaultTraits).contains(
      new TraitId("a1", "foo"),
      new TraitId("a2", "bar"),
      new TraitId("a3", "baz"));
  }

  @Test
  void perStatsTraitsWithMultipleTraits_singleFile() {
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist(MULTIPLE_TRAITS_FILE);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<TraitId> statTraits = traitsManager.getTraitsForStats(MATERIAL_ID, new MaterialStatsId("test1", "stat1"));
    assertThat(statTraits).isNotEmpty();
    assertThat(statTraits).contains(
      new TraitId("b1", "foo"),
      new TraitId("b2", "bar"),
      new TraitId("b3", "baz"));
  }

  @Test
  void multipleTraitsPerStats_singleFile() {
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist(MULTIPLE_TRAITS_FILE);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<TraitId> statTraits1 = traitsManager.getTraitsForStats(MATERIAL_ID, new MaterialStatsId("test2", "stat2"));
    assertThat(statTraits1).isNotEmpty();
    assertThat(statTraits1).contains(
      new TraitId("c1", "foo"),
      new TraitId("c2", "bar"),
      new TraitId("c3", "baz"));

    List<TraitId> statTraits2 = traitsManager.getTraitsForStats(MATERIAL_ID, new MaterialStatsId("test3", "stat3"));
    assertThat(statTraits2).isNotEmpty();
    assertThat(statTraits2).contains(
      new TraitId("d1", "foo"),
      new TraitId("d2", "bar"),
      new TraitId("d3", "baz"));
  }

  @Test
  void combineDefaultTraits_multipleFiles() {
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist(SIMPLE_TRAIT_FILE, SIMPLE_TRAIT_FILE2);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<TraitId> defaultTraits = traitsManager.getDefaultTraits(MATERIAL_ID);
    assertThat(defaultTraits).isNotEmpty();
    assertThat(defaultTraits).contains(
      new TraitId("test1", "trait1"),
      new TraitId("test1", "othertrait1"));
  }

  @Test
  void combineStatsTraits_multipleFiles() {
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist(SIMPLE_TRAIT_FILE, SIMPLE_TRAIT_FILE2);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<TraitId> statTraits = traitsManager.getTraitsForStats(MATERIAL_ID, new MaterialStatsId(Util.getResource("teststat")));
    assertThat(statTraits).isNotEmpty();
    assertThat(statTraits).contains(
      new TraitId("test2", "trait2"),
      new TraitId("test2", "othertrait2"));
  }

  @Test
  void loadMissingFile_ignored() {
    Map<ResourceLocation, JsonElement> splashList = ImmutableMap.of(Util.getResource("nonexistant"), new JsonObject());

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    // ensure that we get this far and that querying the missing material causes no errors
    List<TraitId> defaultTraits = traitsManager.getDefaultTraits(MATERIAL_ID);
    assertThat(defaultTraits).isEmpty();
  }
}
