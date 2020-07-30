package slimeknights.tconstruct.tables.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tables.client.inventory.library.slots.SlotInformation;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class ToolSlotInformationLoader extends JsonReloadListener {

  /** GSON instance for this */
  private static final Gson GSON = new GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /** Singleton instance */
  public static final ToolSlotInformationLoader INSTANCE = new ToolSlotInformationLoader();

  public static final ResourceLocation REPAIR_NAME = Util.getResource("repair");

  /** Map of slots */
  private final Map<ResourceLocation, SlotInformation> slotMap = new HashMap<>();

  private ToolSlotInformationLoader() {
    super(GSON, "tool_station");
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonObject> map, IResourceManager resourceManager, IProfiler profiler) {
    for (Map.Entry<ResourceLocation, JsonObject> entry : map.entrySet()) {
      ResourceLocation location = entry.getKey();
      try {
        JsonObject json = entry.getValue();

        this.slotMap.put(location, SlotInformation.fromJson(json));
      }
      catch (Exception e) {
        log.warn("Exception loading slot information '{}': {}", location, e.getMessage());
      }
    }
  }

  public static SlotInformation get(ResourceLocation registryKey) {
    return INSTANCE.slotMap.get(registryKey);
  }
}
