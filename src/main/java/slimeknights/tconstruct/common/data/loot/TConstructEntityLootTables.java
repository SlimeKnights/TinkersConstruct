package slimeknights.tconstruct.common.data.loot;

import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.functions.LootingEnchantBonus;
import net.minecraft.loot.functions.SetCount;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Objects;
import java.util.stream.Collectors;

public class TConstructEntityLootTables extends EntityLootTables {

  @Override
  protected Iterable<EntityType<?>> getKnownEntities() {
    return ForgeRegistries.ENTITIES.getValues().stream()
                                   .filter((block) -> TConstruct.modID.equals(Objects.requireNonNull(block.getRegistryName()).getNamespace()))
                                   .collect(Collectors.toList());
  }

  @Override
  protected void addTables() {
    this.registerLootTable(TinkerWorld.skySlimeEntity.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(TinkerCommons.slimeball.get(SlimeType.SKY)).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
  }
}
