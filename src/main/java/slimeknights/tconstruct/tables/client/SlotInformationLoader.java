package slimeknights.tconstruct.tables.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import slimeknights.tconstruct.tables.client.inventory.library.slots.SlotInformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class SlotInformationLoader extends JsonDataLoader {

  /** GSON instance for this */
  private static final Gson GSON = new GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /** Singleton instance */
  public static final SlotInformationLoader INSTANCE = new SlotInformationLoader();

  /** Map of Slot Information's */
  private final Map<Identifier, SlotInformation> slotInformationMap = new HashMap<>();

  /** Sorted List of Slot Information's */
  private final List<SlotInformation> slotInformationList = new ArrayList<>();

  private SlotInformationLoader() {
    super(GSON, "tinker_station");
  }

  @Override
  protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
    this.slotInformationMap.clear();
    this.slotInformationList.clear();

    for (Map.Entry<Identifier, JsonElement> entry : map.entrySet()) {
      Identifier location = entry.getKey();
      try {
        JsonObject json = entry.getValue().getAsJsonObject();

        this.slotInformationMap.put(location, SlotInformation.fromJson(json));
      }
      catch (Exception e) {
        log.warn("Exception loading slot information '{}': {}", location, e.getMessage());
      }
    }

    this.slotInformationList.addAll(this.slotInformationMap.values());

    this.slotInformationList.sort(Comparator.comparing(SlotInformation::getSortIndex));
  }

  /**
   * Fetches a Slot Information from the given name
   *
   * @param registryKey the name of the slot infomation to find
   * @return the slot information
   */
  public static SlotInformation get(Identifier registryKey) {
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
