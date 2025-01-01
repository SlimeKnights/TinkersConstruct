package slimeknights.tconstruct.common.data.loot;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerCommons;

import java.util.function.BiConsumer;

public class AdvancementLootTableProvider implements LootTableSubProvider {
  @Override
  public void generate(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
    consumer.accept(TConstruct.getResource("gameplay/starting_book"), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(TinkerCommons.materialsAndYou))));
  }
}
