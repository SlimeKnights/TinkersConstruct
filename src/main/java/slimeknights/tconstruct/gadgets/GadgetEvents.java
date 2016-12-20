package slimeknights.tconstruct.gadgets;

import com.google.common.collect.ImmutableSet;

import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Set;

import static net.minecraft.world.storage.loot.LootTableList.CHESTS_DESERT_PYRAMID;
import static net.minecraft.world.storage.loot.LootTableList.CHESTS_JUNGLE_TEMPLE;
import static net.minecraft.world.storage.loot.LootTableList.CHESTS_STRONGHOLD_CROSSING;

public class GadgetEvents {
  private static final Set<String> SPAGHETTI_LOCATIONS = ImmutableSet.<String>builder()
      .add(CHESTS_DESERT_PYRAMID.toString())
      .add(CHESTS_STRONGHOLD_CROSSING.toString())
      .add(CHESTS_JUNGLE_TEMPLE.toString())
      .build();

  private final LootPool pool;

  public GadgetEvents() {
    LootEntry entry = new LootEntryItem(TinkerGadgets.spaghetti, 1, 1, new LootFunction[0], new LootCondition[0], "moms_spaghetti");
    LootCondition chance = new RandomChance(0.05f);
    pool = new LootPool(new LootEntry[] {entry}, new LootCondition[] {chance}, new RandomValueRange(1), new RandomValueRange(0), "moms_spaghetti");
  }

  @SubscribeEvent
  public void onLootTableLoad(LootTableLoadEvent event) {
    if(SPAGHETTI_LOCATIONS.contains(event.getName().toString())) {
      event.getTable().addPool(pool);
    }
  }
}
