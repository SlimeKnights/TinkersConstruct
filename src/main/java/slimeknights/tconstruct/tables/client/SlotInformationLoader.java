package slimeknights.tconstruct.tables.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.tables.client.inventory.library.slots.SlotInformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Log4j2
public class SlotInformationLoader extends JsonReloadListener {

  /** GSON instance for this */
  private static final Gson GSON = new GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /** Singleton instance */
  public static final SlotInformationLoader INSTANCE = new SlotInformationLoader();

  /** Map of Slot Information's */
  private final Map<ResourceLocation, SlotInformation> slotInformationMap = new HashMap<>();

  /** Sorted List of Slot Information's */
  private final List<SlotInformation> slotInformationList = new ArrayList<>();

  private SlotInformationLoader() {
    super(GSON, "tinker_station");
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager resourceManager, IProfiler profiler) {
    this.slotInformationMap.clear();
    this.slotInformationList.clear();

    for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
      ResourceLocation location = entry.getKey();
      try {
        JsonObject json = entry.getValue().getAsJsonObject();
        if (!json.entrySet().isEmpty()) {
          this.slotInformationMap.put(location, SlotInformation.fromJson(json));
        } else {
          this.slotInformationMap.remove(location);
        }
      }
      catch (Exception e) {
        log.warn("Exception loading slot information '{}': {}", location, e.getMessage());
      }
    }

    // fill the list with the new data
    this.slotInformationMap.entrySet().stream().sorted((entry1, entry2) -> {
      int sort1 = entry1.getValue().getSortIndex();
      int sort2 = entry2.getValue().getSortIndex();
      if (sort1 != sort2) {
        return Integer.compare(sort1, sort2);
      }
      return entry1.getKey().compareTo(entry2.getKey());
    }).map(Entry::getValue).forEach(this.slotInformationList::add);
  }

  /**
   * Fetches a Slot Information from the given name
   *
   * @param registryKey the name of the slot infomation to find
   * @return the slot information
   */
  public static SlotInformation get(ResourceLocation registryKey) {
    return INSTANCE.slotInformationMap.getOrDefault(registryKey, SlotInformation.EMPTY);
  }

  /**
   * Gets the full list of all Slot Information's
   *
   * @return a list of SlotInformation
   */
  public static Collection<SlotInformation> getSlotInformationList() {
    return INSTANCE.slotInformationList;
  }
}
