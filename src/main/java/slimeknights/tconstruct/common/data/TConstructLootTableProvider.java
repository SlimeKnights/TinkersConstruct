package slimeknights.tconstruct.common.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.ValidationTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TConstructLootTableProvider implements IDataProvider {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
  private final DataGenerator dataGenerator;
  private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> field_218444_e = ImmutableList.of(
    Pair.of(TConstructBlockLootTables::new, LootParameterSets.BLOCK));

  public TConstructLootTableProvider(DataGenerator dataGeneratorIn) {
    this.dataGenerator = dataGeneratorIn;
  }

  /**
   * Performs this provider's action.
   */
  @Override
  public void act(DirectoryCache cache) {
    Path path = this.dataGenerator.getOutputFolder();
    Map<ResourceLocation, LootTable> map = Maps.newHashMap();
    this.field_218444_e.forEach((p_218438_1_) -> {
      p_218438_1_.getFirst().get().accept((p_218437_2_, p_218437_3_) -> {
        if (map.put(p_218437_2_, p_218437_3_.setParameterSet(p_218438_1_.getSecond()).build()) != null) {
          throw new IllegalStateException("Duplicate loot table " + p_218437_2_);
        }
      });
    });
    ValidationTracker validationtracker = new ValidationTracker(LootParameterSets.GENERIC, (p_229442_0_) -> {
      return null;
    }, map::get);

    for (ResourceLocation resourcelocation : Sets.difference(LootTables.func_215796_a(), map.keySet())) {
      validationtracker.addProblem("Missing built-in table: " + resourcelocation);
    }

    map.forEach((p_218436_2_, p_218436_3_) -> {
      LootTableManager.func_227508_a_(validationtracker, p_218436_2_, p_218436_3_);
    });

    Multimap<String, String> multimap = validationtracker.getProblems();
    if (!multimap.isEmpty()) {
      multimap.forEach((p_229440_0_, p_229440_1_) -> {
        LOGGER.warn("Found validation problem in " + p_229440_0_ + ": " + p_229440_1_);
      });
      throw new IllegalStateException("Failed to validate loot tables, see logs");
    } else {
      map.forEach((p_229441_2_, p_229441_3_) -> {
        Path path1 = getPath(path, p_229441_2_);

        try {
          IDataProvider.save(GSON, cache, LootTableManager.toJson(p_229441_3_), path1);
        } catch (IOException ioexception) {
          LOGGER.error("Couldn't save loot table {}", path1, ioexception);
        }

      });
    }
  }

  private static Path getPath(Path pathIn, ResourceLocation id) {
    return pathIn.resolve("data/" + id.getNamespace() + "/loot_tables/" + id.getPath() + ".json");
  }

  /**
   * Gets a name for this provider, to use in logging.
   */
  @Override
  public String getName() {
    return "TConstruct LootTables";
  }
}
