package slimeknights.tconstruct.library.traits;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.test.JsonFileLoader;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MaterialTraitsManagerTest  extends BaseMcTest {

  private static final ResourceLocation SIMPLE_TRAIT = Util.getResource("traitstest");
  private static final ResourceLocation MULTIPLE_TRAITS = Util.getResource("multiple");

  private MaterialTraitsManager traitsManager = new MaterialTraitsManager();
  private JsonFileLoader fileLoader = new JsonFileLoader(MaterialTraitsManager.GSON, MaterialTraitsManager.FOLDER);

  @Test
  void defaultTraitsAreLoaded() {
    // material id doubles as filename
    ResourceLocation materialId = SIMPLE_TRAIT;
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<ResourceLocation> defaultTraits = traitsManager.getDefaultTraits(materialId);
    assertThat(defaultTraits).isNotEmpty();
    assertThat(defaultTraits).contains(new ResourceLocation("test1:trait1"));
  }

  @Test
  void perStatsTraitsAreLoaded() {
    // material id doubles as filename
    ResourceLocation materialId = SIMPLE_TRAIT;
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<ResourceLocation> statTraits = traitsManager.getTraitsForStats(materialId, Util.getResource("teststat"));
    assertThat(statTraits).isNotEmpty();
    assertThat(statTraits).contains(new ResourceLocation("test2:trait2"));
  }

  @Test
  void defaultWithMultipleTraits() {
    // material id doubles as filename
    ResourceLocation materialId = MULTIPLE_TRAITS;
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<ResourceLocation> defaultTraits = traitsManager.getDefaultTraits(materialId);
    assertThat(defaultTraits).isNotEmpty();
    assertThat(defaultTraits).contains(
      new ResourceLocation("a1:foo"),
      new ResourceLocation("a2:bar"),
      new ResourceLocation("a3:baz"));
  }

  @Test
  void perStatsTraitsWithMultipleTraits() {
    // material id doubles as filename
    ResourceLocation materialId = MULTIPLE_TRAITS;
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<ResourceLocation> statTraits = traitsManager.getTraitsForStats(materialId, new ResourceLocation("test1:stat1"));
    assertThat(statTraits).isNotEmpty();
    assertThat(statTraits).contains(
      new ResourceLocation("b1:foo"),
      new ResourceLocation("b2:bar"),
      new ResourceLocation("b3:baz"));
  }

  @Test
  void multiplePerStatsTraits() {
    // material id doubles as filename
    ResourceLocation materialId = MULTIPLE_TRAITS;
    Map<ResourceLocation, JsonObject> splashList = fileLoader.loadFilesAsSplashlist(materialId);

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    List<ResourceLocation> statTraits1 = traitsManager.getTraitsForStats(materialId, new ResourceLocation("test2:stat2"));
    assertThat(statTraits1).isNotEmpty();
    assertThat(statTraits1).contains(
      new ResourceLocation("c1:foo"),
      new ResourceLocation("c2:bar"),
      new ResourceLocation("c3:baz"));

    List<ResourceLocation> statTraits2 = traitsManager.getTraitsForStats(materialId, new ResourceLocation("test3:stat3"));
    assertThat(statTraits2).isNotEmpty();
    assertThat(statTraits2).contains(
      new ResourceLocation("d1:foo"),
      new ResourceLocation("d2:bar"),
      new ResourceLocation("d3:baz"));
  }

  @Test
  void loadMissingFile_ignored() {
    ResourceLocation materialId = Util.getResource("nonexistant");
    Map<ResourceLocation, JsonObject> splashList = ImmutableMap.of(materialId, new JsonObject());

    traitsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    // ensure that we get this far and that querying the missing material causes no errors
    List<ResourceLocation> defaultTraits = traitsManager.getDefaultTraits(materialId);
    assertThat(defaultTraits).isEmpty();
  }
}
