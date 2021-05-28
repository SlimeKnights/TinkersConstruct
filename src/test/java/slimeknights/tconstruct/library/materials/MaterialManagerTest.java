package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
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
    assertThat(testMaterial.getFluid()).isEqualTo(Fluids.WATER);
    assertThat(testMaterial.isCraftable()).isTrue();
    assertThat(testMaterial.getColor().color).isEqualTo(0x1234ab);
    assertThat(testMaterial.getTemperature()).isEqualTo(1234);
  }

  @Test
  void loadMinimalMaterial_succeedWithDefaults() {
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist("minimal");

    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).hasSize(1);
    IMaterial testMaterial = allMaterials.iterator().next();
    assertThat(testMaterial.getIdentifier()).isEqualByComparingTo(new MaterialId("tconstruct", "minimal"));
    assertThat(testMaterial.getFluid()).extracting(Fluid::getDefaultState).matches(FluidState::isEmpty);
    assertThat(testMaterial.isCraftable()).isFalse();
    assertThat(testMaterial.getColor().color & 0xffffff).isEqualTo(0xffffff);
    assertThat(testMaterial.getTemperature()).isEqualTo(0);
  }

  @Test
  void invalidFluid_useDefault() {
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist("invalid");

    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).hasSize(1);
    IMaterial testMaterial = allMaterials.iterator().next();
    assertThat(testMaterial.getFluid()).extracting(Fluid::getDefaultState).matches(FluidState::isEmpty);
    assertThat(testMaterial.getColor().color & 0xffffff).isEqualTo(0xffffff);
    assertThat(testMaterial.getTemperature()).isEqualTo(0);
  }

  @Test
  void craftableIsRequired_failOnMissing() {
    ResourceLocation materialId = Util.getResource("nonexistant");
    Map<ResourceLocation, JsonElement> splashList = ImmutableMap.of(materialId, new JsonObject());

    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).isEmpty();
  }
}
