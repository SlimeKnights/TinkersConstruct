package slimeknights.tconstruct.library.traits;

import com.google.common.collect.ImmutableMap;
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

class MaterialTraitsManagerTest  extends BaseMcTest {

  private static final MaterialId SIMPLE_TRAIT = new MaterialId(Util.getResource("traitstest"));
  private static final MaterialId MULTIPLE_TRAITS = new MaterialId(Util.getResource("multiple"));

  private MaterialTraitsManager traitsManager = new MaterialTraitsManager();
  private JsonFileLoader fileLoader = new JsonFileLoader(MaterialTraitsManager.GSON, MaterialTraitsManager.FOLDER);

  @Test
  void defaultTraitsAreLoaded() {
    // material id doubles as filename
    MaterialId materialId = SIMPLE_TRAIT;
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<TraitId> defaultTraits = traitsManager.getDefaultTraits(materialId);
    assertThat(defaultTraits).isNotEmpty();
    assertThat(defaultTraits).contains(new TraitId("test1", "trait1"));
  }

  @Test
  void perStatsTraitsAreLoaded() {
    // material id doubles as filename
    MaterialId materialId = SIMPLE_TRAIT;
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<TraitId> statTraits = traitsManager.getTraitsForStats(materialId, new MaterialStatsId(Util.getResource("teststat")));
    assertThat(statTraits).isNotEmpty();
    assertThat(statTraits).contains(new TraitId("test2", "trait2"));
  }

  @Test
  void defaultWithMultipleTraits() {
    // material id doubles as filename
    MaterialId materialId = MULTIPLE_TRAITS;
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<TraitId> defaultTraits = traitsManager.getDefaultTraits(materialId);
    assertThat(defaultTraits).isNotEmpty();
    assertThat(defaultTraits).contains(
      new TraitId("a1", "foo"),
      new TraitId("a2", "bar"),
      new TraitId("a3", "baz"));
  }

  @Test
  void perStatsTraitsWithMultipleTraits() {
    // material id doubles as filename
    MaterialId materialId = MULTIPLE_TRAITS;
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<TraitId> statTraits = traitsManager.getTraitsForStats(materialId, new MaterialStatsId("test1", "stat1"));
    assertThat(statTraits).isNotEmpty();
    assertThat(statTraits).contains(
      new TraitId("b1", "foo"),
      new TraitId("b2", "bar"),
      new TraitId("b3", "baz"));
  }

  @Test
  void multiplePerStatsTraits() {
    // material id doubles as filename
    MaterialId materialId = MULTIPLE_TRAITS;
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<TraitId> statTraits1 = traitsManager.getTraitsForStats(materialId, new MaterialStatsId("test2", "stat2"));
    assertThat(statTraits1).isNotEmpty();
    assertThat(statTraits1).contains(
      new TraitId("c1", "foo"),
      new TraitId("c2", "bar"),
      new TraitId("c3", "baz"));

    List<TraitId> statTraits2 = traitsManager.getTraitsForStats(materialId, new MaterialStatsId("test3", "stat3"));
    assertThat(statTraits2).isNotEmpty();
    assertThat(statTraits2).contains(
      new TraitId("d1", "foo"),
      new TraitId("d2", "bar"),
      new TraitId("d3", "baz"));
  }

  @Test
  void loadMissingFile_ignored() {
    MaterialId materialId = new MaterialId(Util.getResource("nonexistant"));
    Map<ResourceLocation, JsonObject> splashList = ImmutableMap.of(materialId, new JsonObject());

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    // ensure that we get this far and that querying the missing material causes no errors
    List<TraitId> defaultTraits = traitsManager.getDefaultTraits(materialId);
    assertThat(defaultTraits).isEmpty();
  }
}
