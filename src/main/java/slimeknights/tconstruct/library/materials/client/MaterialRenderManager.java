package slimeknights.tconstruct.library.materials.client;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.library.materials.IMaterial;

import java.util.Map;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

/**
 * This class takes care of loading all the rendering information for materials
 */
public class MaterialRenderManager extends JsonDataLoader {

  private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
  private static final Logger LOGGER = LogManager.getLogger();

  private Map<Identifier, IMaterial> materials = ImmutableMap.of();

  public MaterialRenderManager() {
    super(GSON, "tic_materials");
  }

  @Override
  protected void apply(Map<Identifier,JsonElement> splashList, ResourceManager resourceManagerIn, Profiler profilerIn) {
    // todo: actually keep the data
    splashList.forEach((resourceLocation, json) -> LOGGER.info("{}: {}", resourceLocation, json));
  }
}
