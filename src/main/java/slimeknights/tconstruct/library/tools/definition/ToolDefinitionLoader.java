package slimeknights.tconstruct.library.tools.definition;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.ToolDefinition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/** JSON loader that loads tool definitions from JSON */
@Log4j2
public class ToolDefinitionLoader extends JsonReloadListener {
  public static final String FOLDER = "tinkering/tool_definitions";
  public static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .registerTypeAdapter(DefinitionToolStats.class, DefinitionToolStats.SERIALIZER)
    .registerTypeAdapter(PartRequirement.class, PartRequirement.SERIALIZER)
    .registerTypeAdapter(DefinitionModifierSlots.class, DefinitionModifierSlots.SERIALIZER)
    .registerTypeAdapter(ModifierEntry.class, ModifierEntry.SERIALIZER)
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();
  private static final ToolDefinitionLoader INSTANCE = new ToolDefinitionLoader();

  /** Map of loaded tool definition data */
  private Map<ResourceLocation,ToolDefinitionData> dataMap = Collections.emptyMap();

  /** Tool definitions registered to be loaded */
  private final Map<ResourceLocation,ToolDefinition> definitions = new HashMap<>();

  private ToolDefinitionLoader() {
    super(GSON, FOLDER);
  }

  /** Gets the instance of the definition loader */
  public static ToolDefinitionLoader getInstance() {
    return INSTANCE;
  }

  /** Initializes the tool definition loader */
  public static void init() {
    MinecraftForge.EVENT_BUS.addListener(INSTANCE::addDataPackListeners);
    MinecraftForge.EVENT_BUS.addListener(INSTANCE::onDatapackSync);
  }

  /**
   * Updates the tool data from the server.list. Should only be called client side
   * @param dataMap  Server data map
   */
  protected void updateDataFromServer(Map<ResourceLocation,ToolDefinitionData> dataMap) {
    this.dataMap = dataMap;
    for (Entry<ResourceLocation,ToolDefinition> entry : definitions.entrySet()) {
      ToolDefinitionData data = dataMap.get(entry.getKey());
      ToolDefinition definition = entry.getValue();
      // errored serverside, so resolve without error here
      if (data != null) {
        definition.setData(data);
      } else {
        definition.setDefaultData();
      }
    }
  }

  @Override
  protected void apply(Map<ResourceLocation,JsonElement> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
    ImmutableMap.Builder<ResourceLocation, ToolDefinitionData> builder = ImmutableMap.builder();
    for (Entry<ResourceLocation,ToolDefinition> entry : definitions.entrySet()) {
      ResourceLocation key = entry.getKey();
      ToolDefinition definition = entry.getValue();
      // first, need to have a json for the given name
      JsonElement element = splashList.get(key);
      if (element == null) {
        log.error("Missing tool definition for tool {}", key);
        definition.setDefaultData();
        continue;
      }
      try {
        ToolDefinitionData data = GSON.fromJson(JSONUtils.getJsonObject(element, "tool_definition"), ToolDefinitionData.class);
        definition.validate(data);
        builder.put(key, data);
        definition.setData(data);
      } catch (Exception e) {
        log.error("Failed to load tool definition for tool {}", key, e);
        definition.setDefaultData();
      }
    }
    this.dataMap = builder.build();
  }

  /** Gets a list of all tool definitions registered to the loader */
  public Collection<ToolDefinition> getRegisteredToolDefinitions() {
    return definitions.values();
  }

  /** Called on datapack sync to send the tool data to all players */
  private void onDatapackSync(OnDatapackSyncEvent event) {
    UpdateToolDefinitionDataPacket packet = new UpdateToolDefinitionDataPacket(dataMap);
    TinkerNetwork.getInstance().sendToPlayerList(event.getPlayer(), event.getPlayerList(), packet);
  }

  /** Adds the managers as datapack listeners */
  private void addDataPackListeners(final AddReloadListenerEvent event) {
    event.addListener(this);
  }

  /** Registers a tool definition with the loader */
  public void registerToolDefinition(ToolDefinition definition) {
    ResourceLocation name = definition.getId();
    if (definitions.containsKey(name)) {
      throw new IllegalArgumentException("Duplicate tool definition " + name);
    }
    definitions.put(name, definition);
  }
}
