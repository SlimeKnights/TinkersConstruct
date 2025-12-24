package slimeknights.tconstruct.library.tools.definition;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.typed.TypedMapBuilder;
import slimeknights.tconstruct.common.network.TinkerNetwork;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/** JSON loader that loads tool definitions from JSON */
@Log4j2
public class ToolDefinitionLoader extends SimpleJsonResourceReloadListener {
  public static final String FOLDER = "tinkering/tool_definitions";
  private static final ToolDefinitionLoader INSTANCE = new ToolDefinitionLoader();

  /** Map of loaded tool definition data */
  private Map<ResourceLocation,ToolDefinitionData> dataMap = Collections.emptyMap();

  /** Tool definitions registered to be loaded */
  private final Map<ResourceLocation,ToolDefinition> definitions = new HashMap<>();

  /** Condition context */
  private IContext conditionContext = IContext.EMPTY;

  private ToolDefinitionLoader() {
    super(JsonHelper.DEFAULT_GSON, FOLDER);
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
        definition.clearData();
      }
    }
  }

  /** Creates context for modifier parsing */
  public static TypedMapBuilder contextBuilder(ResourceLocation key) {
    return TypedMapBuilder.builder().put(ContextKey.ID, key).put(ContextKey.DEBUG, "Tool Definition " + key);
  }

  @Override
  protected void apply(Map<ResourceLocation,JsonElement> splashList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
    long time = System.nanoTime();
    ImmutableMap.Builder<ResourceLocation, ToolDefinitionData> builder = ImmutableMap.builder();
    for (Entry<ResourceLocation,ToolDefinition> entry : definitions.entrySet()) {
      ResourceLocation key = entry.getKey();
      ToolDefinition definition = entry.getValue();
      // first, need to have a json for the given name
      JsonElement element = splashList.get(key);
      if (element == null) {
        log.error("Missing tool definition for tool {}", key);
        definition.clearData();
        continue;
      }
      try {
        // TODO: do we want to allow load conditions for tool definitions? might make merging harder should we go that route instead
        ToolDefinitionData data = ToolDefinitionData.LOADABLE.convert(element, key.toString(), contextBuilder(key).put(ContextKey.CONDITION_CONTEXT, conditionContext).build());
        builder.put(key, data);
        definition.setData(data);
      } catch (Exception e) {
        log.error("Failed to load tool definition for tool {}", key, e);
        definition.clearData();
      }
    }
    this.dataMap = builder.build();
    log.info("Loaded {} tool definitions in {} ms", this.dataMap.size(), (System.nanoTime() - time) / 1000000f);
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
    conditionContext = event.getConditionContext();
  }

  /** Registers a tool definition with the loader */
  public synchronized void registerToolDefinition(ToolDefinition definition) {
    ResourceLocation name = definition.getId();
    if (definitions.containsKey(name)) {
      throw new IllegalArgumentException("Duplicate tool definition " + name);
    }
    definitions.put(name, definition);
  }
}
