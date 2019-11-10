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
import net.minecraft.util.ResourceLocation;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.BaseMcTest;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.util.JsonFileLoader;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MaterialManagerTest extends BaseMcTest {

  private MaterialManager materialManager = new MaterialManager();
  private JsonFileLoader fileLoader = new JsonFileLoader(MaterialManager.GSON, MaterialManager.FOLDER);

  @Test
  void loadFullMaterial_allStatsPresent() {
    ResourceLocation file = Util.getResource("full");
    JsonObject jsonObject = fileLoader.loadJson(file);


    ImmutableMap<ResourceLocation, JsonObject> splashList = ImmutableMap.of(file, jsonObject);
    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));


    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).hasSize(1);
    IMaterial testMaterial = allMaterials.iterator().next();
    assertThat(testMaterial.getIdentifier()).isEqualByComparingTo(new ResourceLocation("tconstruct", "full"));
    assertThat(testMaterial.getFluid()).isEqualTo(Fluids.WATER);
    assertThat(testMaterial.isCraftable()).isTrue();
    assertThat(testMaterial.getShard()).matches(itemStack -> ItemStack.areItemStacksEqual(itemStack, new ItemStack(Items.STICK)));
  }

  @Test
  void loadMinimalMaterial_succeedWithDefaults() {
    ResourceLocation file = Util.getResource("minimal");
    JsonObject jsonObject = fileLoader.loadJson(file);


    ImmutableMap<ResourceLocation, JsonObject> splashList = ImmutableMap.of(file, jsonObject);
    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));


    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).hasSize(1);
    IMaterial testMaterial = allMaterials.iterator().next();
    assertThat(testMaterial.getIdentifier()).isEqualByComparingTo(new ResourceLocation("tconstruct", "minimal"));
    assertThat(testMaterial.getFluid()).extracting(Fluid::getDefaultState).matches(IFluidState::isEmpty);
    assertThat(testMaterial.isCraftable()).isFalse();
    assertThat(testMaterial.getShard()).matches(ItemStack::isEmpty);
    assertThat(testMaterial.getAllStats()).isEmpty();
    assertThat(testMaterial.getAllTraits()).isEmpty();
  }

  @Test
  void invalidFluid_useDefault() {
    ResourceLocation file = Util.getResource( "invalid");
    JsonObject jsonObject = fileLoader.loadJson(file);


    ImmutableMap<ResourceLocation, JsonObject> splashList = ImmutableMap.of(file, jsonObject);
    materialManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));


    Collection<IMaterial> allMaterials = materialManager.getAllMaterials();
    assertThat(allMaterials).hasSize(1);
    IMaterial testMaterial = allMaterials.iterator().next();
    assertThat(testMaterial.getFluid()).extracting(Fluid::getDefaultState).matches(IFluidState::isEmpty);
  }

  @Test
  void invalidShard_useDefault() {
    ResourceLocation file = Util.getResource("invalid");
    JsonObject jsonObject = fileLoader.loadJson(file);


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
}
