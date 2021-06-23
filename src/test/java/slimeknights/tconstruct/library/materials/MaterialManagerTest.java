package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.test.JsonFileLoader;

import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MaterialManagerTest extends BaseMcTest {

  private static MaterialManager materialManager;
  private final JsonFileLoader fileLoader = new JsonFileLoader(MaterialManager.GSON, MaterialManager.FOLDER);

  @BeforeAll
  static void setUp() {
    materialManager = new MaterialManager();
  }

  @Test
  void loadFullMaterial_allStatsPresent() {
    Map<ResourceLocation,JsonElement> splashList = fileLoader.loadFilesAsSplashlist("full");

    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).hasSize(1);
    IMaterial testMaterial = allMaterials.iterator().next();
    assertThat(testMaterial.getIdentifier()).isEqualByComparingTo(new MaterialId("tconstruct", "full"));
    assertThat(testMaterial.isCraftable()).isTrue();
    assertThat(testMaterial.getColor().color).isEqualTo(0x1234ab);
    assertThat(testMaterial.getTier()).isEqualTo(15);
    assertThat(testMaterial.getSortOrder()).isEqualTo(4);
    assertThat(testMaterial.isHidden()).isTrue();
  }

  @Test
  void loadMinimalMaterial_succeedWithDefaults() {
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist("minimal");

    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).hasSize(1);
    IMaterial testMaterial = allMaterials.iterator().next();
    assertThat(testMaterial.getIdentifier()).isEqualByComparingTo(new MaterialId("tconstruct", "minimal"));
    assertThat(testMaterial.isCraftable()).isFalse();
    assertThat(testMaterial.getColor().color & 0xffffff).isEqualTo(0xffffff);
    assertThat(testMaterial.getTier()).isEqualTo(0);
    assertThat(testMaterial.getSortOrder()).isEqualTo(100);
    assertThat(testMaterial.isHidden()).isFalse();
  }

  @Test
  void invalid_skipped() {
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist("invalid");

    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).hasSize(1);
    IMaterial testMaterial = allMaterials.iterator().next();
    assertThat(testMaterial.isCraftable()).isFalse();
    assertThat(testMaterial.getColor().color & 0xffffff).isEqualTo(0xffffff);
    assertThat(testMaterial.isHidden()).isFalse();
  }

  @Test
  void failOnMissing() {
    ResourceLocation materialId = Util.getResource("nonexistant");
    Map<ResourceLocation, JsonElement> splashList = ImmutableMap.of(materialId, new JsonObject());

    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).isEmpty();
  }
}
