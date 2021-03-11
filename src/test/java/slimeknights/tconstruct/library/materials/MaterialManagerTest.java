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
import slimeknights.tconstruct.fixture.ModifierFixture;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.test.JsonFileLoader;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MaterialManagerTest extends BaseMcTest {

  private static MaterialManager materialManager;
  private final JsonFileLoader fileLoader = new JsonFileLoader(MaterialManager.GSON, MaterialManager.FOLDER);

  @BeforeAll
  static void setUp() {
    TinkerNetwork mock = mock(TinkerNetwork.class);
    materialManager = new MaterialManager(mock);
    ModifierFixture.init();
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

    List<ModifierEntry> traits = testMaterial.getTraits();
    assertThat(traits).hasSize(1);
    assertThat(traits.get(0).getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(traits.get(0).getLevel()).isEqualTo(2);
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
    assertThat(testMaterial.getTraits()).isEmpty();
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
  void invalidTrait_throwSyntaxException() {
    Map<ResourceLocation, JsonElement> splashList = fileLoader.loadFilesAsSplashlist("bad_trait");

    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));
    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).hasSize(0);
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
