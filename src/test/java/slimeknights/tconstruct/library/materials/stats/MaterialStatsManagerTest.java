package slimeknights.tconstruct.library.materials.stats;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.test.JsonFileLoader;

import java.util.Optional;

import static org.mockito.Mockito.mock;

class MaterialStatsManagerTest extends BaseMcTest {

  private MaterialStatsManager materialStatsManager = new MaterialStatsManager();
  private JsonFileLoader fileLoader = new JsonFileLoader(MaterialStatsManager.GSON, MaterialStatsManager.FOLDER);

  // todo: actual tests

  @Test
  void name() {
    ResourceLocation file = Util.getResource("teststat");
    JsonObject jsonObject = fileLoader.loadJson(file);
    ResourceLocation statId = new ResourceLocation("test", "stat");
    materialStatsManager.registerMaterialStat(statId, BaseMaterialStats.class);

    // todo: extract JsonReloadableListenerBaseTest..
    ImmutableMap<ResourceLocation, JsonObject> splashList = ImmutableMap.of(file, jsonObject);
    materialStatsManager.apply(splashList, mock(IResourceManager.class), mock(IProfiler.class));

    Optional<BaseMaterialStats> stats = materialStatsManager.getStats(new ResourceLocation("test", "foobar"), statId);
  }
}
