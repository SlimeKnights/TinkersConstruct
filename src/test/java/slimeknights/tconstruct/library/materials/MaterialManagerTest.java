package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MaterialManagerTest {

  private MaterialManager materialManager = new MaterialManager();

  @BeforeAll
  static void setUpRegistries() {
    Bootstrap.register();
  }

  @Test
  void loadFullMaterial_allStatsPresent() throws Exception {
    ResourceLocation file = new ResourceLocation("test", "full");
    JsonObject jsonObject = loadMaterialJson(file.getPath());


    ImmutableMap<ResourceLocation, JsonObject> splashList = ImmutableMap.of(file, jsonObject);
    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));


    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).hasSize(1);
    IMaterial testMaterial = allMaterials.iterator().next();
    assertThat(testMaterial.getIdentifier()).isEqualByComparingTo(new ResourceLocation("test", "full"));
    assertThat(testMaterial.getFluid()).isEqualTo(Fluids.WATER);
    assertThat(testMaterial.isCraftable()).isTrue();
    assertThat(testMaterial.getShard()).matches(itemStack -> ItemStack.areItemStacksEqual(itemStack, new ItemStack(Items.STICK)));
  }

  @Test
  void loadMinimalMaterial_succeedWithDefaults() throws Exception {
    ResourceLocation file = new ResourceLocation("test", "minimal");
    JsonObject jsonObject = loadMaterialJson(file.getPath());


    ImmutableMap<ResourceLocation, JsonObject> splashList = ImmutableMap.of(file, jsonObject);
    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));


    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).hasSize(1);
    IMaterial testMaterial = allMaterials.iterator().next();
    assertThat(testMaterial.getIdentifier()).isEqualByComparingTo(new ResourceLocation("test", "minimal"));
    assertThat(testMaterial.getFluid()).extracting(Fluid::getDefaultState).matches(IFluidState::isEmpty);
    assertThat(testMaterial.isCraftable()).isFalse();
    assertThat(testMaterial.getShard()).matches(ItemStack::isEmpty);
    assertThat(testMaterial.getAllStats()).hasSize(1);
  }

  @Test
  void invalidFluid_useDefault() throws Exception {
    ResourceLocation file = new ResourceLocation("test", "invalid");
    JsonObject jsonObject = loadMaterialJson(file.getPath());


    ImmutableMap<ResourceLocation, JsonObject> splashList = ImmutableMap.of(file, jsonObject);
    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));


    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).hasSize(1);
    IMaterial testMaterial = allMaterials.iterator().next();
    assertThat(testMaterial.getFluid()).extracting(Fluid::getDefaultState).matches(IFluidState::isEmpty);
  }

  @Test
  void invalidShard_useDefault() throws Exception {
    ResourceLocation file = new ResourceLocation("test", "invalid");
    JsonObject jsonObject = loadMaterialJson(file.getPath());


    ImmutableMap<ResourceLocation, JsonObject> splashList = ImmutableMap.of(file, jsonObject);
    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));


    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).hasSize(1);
    IMaterial testMaterial = allMaterials.iterator().next();
    assertThat(testMaterial.getShard()).matches(ItemStack::isEmpty);
  }

  @Test
  void materialIdIsRequired_failOnMissing() {
    ResourceLocation file = new ResourceLocation("test", "nonexistant");
    JsonObject jsonObject = new JsonObject();


    ImmutableMap<ResourceLocation, JsonObject> splashList = ImmutableMap.of(file, jsonObject);
    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));


    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).isEmpty();
  }

  private JsonObject loadMaterialJson(String filename) throws IOException {
    String path = Paths.get("data/tconstruct/materials/", filename + ".json").toString();
    URL resource = materialManager.getClass().getClassLoader().getResource(path);
    if(resource == null) {
      throw new IllegalArgumentException("Resource with path " + path + " doesn't exist");
    }
    try (
      InputStream inputstream = resource.openStream();
      Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))
    ) {
      return Objects.requireNonNull(JSONUtils.fromJson(MaterialManager.GSON, reader, JsonObject.class));
    }
  }
}
