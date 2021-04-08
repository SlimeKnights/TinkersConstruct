package slimeknights.tconstruct.common.data.loot;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.LootTablesProvider;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import slimeknights.tconstruct.TConstruct;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TConstructLootTableProvider extends LootTablesProvider {

  private LootTablesProvider x;
  private final List<Pair<Supplier<Consumer<BiConsumer<Identifier, LootTable.Builder>>>, LootContextType>> lootTables = ImmutableList.of(Pair.of(TConstructBlockLootTables::new, LootContextTypes.BLOCK), Pair.of(TConstructEntityLootTables::new, LootContextTypes.ENTITY));

  public TConstructLootTableProvider(DataGenerator gen) {
    super(gen);
  }

  @Override
  protected List<Pair<Supplier<Consumer<BiConsumer<Identifier, LootTable.Builder>>>, LootContextType>> getTables() {
    return lootTables;
  }

  @Override
  protected void validate(Map<Identifier,LootTable> map, LootTableReporter validationtracker) {
    map.forEach((loc, table) -> LootManager.validate(validationtracker, loc, table));
    // Remove vanilla's tables, which we also loaded so we can redirect stuff to them.
    // This ensures the remaining generator logic doesn't write those to files.
    map.keySet().removeIf((loc) -> !loc.getNamespace().equals(TConstruct.modID));
  }

  /**
   * Gets a name for this provider, to use in logging.
   */
  @Override
  public String getName() {
    return "TConstruct LootTables";
  }
}
