package slimeknights.tconstruct.library.loot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.mantle.data.listener.IEarlyReloadListener;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.loot.LootTableInjection.LootPoolInjection;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Class handling injecting additional entries into loot tables */
public enum LootTableInjector implements IEarlyReloadListener {
  INSTANCE;

  /** Datapack folder for the injector */
  public static final String FOLDER = "tinkering/loot_injectors";

  /** Initializes the loot table injector */
  public static void init() {
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, AddReloadListenerEvent.class, event -> {
      event.addListener(INSTANCE);
      INSTANCE.context = event.getConditionContext();
    });
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LootTableLoadEvent.class, INSTANCE::lootTableLoad);
  }

  /** Condition context for preventing load */
  private IContext context = IContext.EMPTY;
  /** Map of injections to use on loot table load */
  private Map<ResourceLocation,LootTableInjection> injections = Collections.emptyMap();

  @Override
  public void onResourceManagerReload(ResourceManager manager) {
    long time = System.nanoTime();
    Map<ResourceLocation,LootTableInjection.Builder> builders = new HashMap<>();
    int loaded = 0;
    for (Entry<ResourceLocation,Resource> entry : manager.listResources(FOLDER, loc -> loc.getPath().endsWith(".json")).entrySet()) {
      try (Reader reader = entry.getValue().openAsReader()) {
        JsonObject json = GsonHelper.fromJson(JsonHelper.DEFAULT_GSON, reader, JsonObject.class);
        if (json != null) {
          // skip if empty for easy removals
          if (!json.keySet().isEmpty() && CraftingHelper.processConditions(json, "conditions", context)) {
            // the builder allows us to merge from multiple sources, for efficiency
            // ensures a given table name and pool name both show just once
            LootTableInjection injection = LootTableInjection.LOADABLE.deserialize(json);
            LootTableInjection.Builder builder = builders.computeIfAbsent(injection.name(), id -> new LootTableInjection.Builder());
            for (LootPoolInjection pool : injection.pools()) {
              builder.addToPool(pool);
            }
            loaded++;
          }
        } else {
          TConstruct.LOG.error("Couldn't parse loot table injection from {} as it's null or empty", entry.getKey());
        }
      } catch (IllegalArgumentException | IOException | JsonParseException ex) {
        TConstruct.LOG.error("Couldn't parse loot injection from {}", entry.getKey(), ex);
      }
    }
    // build final map
    injections = builders.entrySet().stream().map(entry -> entry.getValue().build(entry.getKey()))
                         .collect(Collectors.toUnmodifiableMap(LootTableInjection::name, Function.identity()));
    // log timer
    TConstruct.LOG.info("Loaded {} loot table injectors injecting into {} tables in {} ms", loaded, injections.size(), (System.nanoTime() - time) / 1000000f);
  }

  /** Called on loot table load to handle the actual injection */
  private void lootTableLoad(LootTableLoadEvent event) {
    LootTableInjection injection = injections.get(event.getName());
    if (injection != null) {
      TConstruct.LOG.debug("Injecting into {} pools in the table {}", injection.pools().size(), injection.name());
      LootTable table = event.getTable();
      for (LootPoolInjection pool : injection.pools()) {
        pool.inject(table);
      }
    }
  }
}
