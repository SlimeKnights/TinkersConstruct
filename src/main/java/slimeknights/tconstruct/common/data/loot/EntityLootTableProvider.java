package slimeknights.tconstruct.common.data.loot;

import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.EntityHasProperty;
import net.minecraft.loot.functions.LootingEnchantBonus;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.loot.functions.Smelt;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Objects;
import java.util.stream.Collectors;

public class EntityLootTableProvider extends EntityLootTables {

  @Override
  protected Iterable<EntityType<?>> getKnownEntities() {
    return ForgeRegistries.ENTITIES.getValues().stream()
                                   .filter((block) -> TConstruct.MOD_ID.equals(Objects.requireNonNull(block.getRegistryName()).getNamespace()))
                                   .collect(Collectors.toList());
  }

  @Override
  protected void addTables() {
    this.add(TinkerWorld.earthSlimeEntity.get(), dropSlimeballs(SlimeType.EARTH));
    this.add(TinkerWorld.skySlimeEntity.get(), dropSlimeballs(SlimeType.SKY));
    this.add(TinkerWorld.enderSlimeEntity.get(), dropSlimeballs(SlimeType.ENDER));
    this.add(TinkerWorld.terracubeEntity.get(),
                           LootTable.lootTable().withPool(LootPool.lootPool()
                                                                   .setRolls(ConstantRange.exactly(1))
                                                                   .add(ItemLootEntry.lootTableItem(Items.CLAY_BALL)
                                                                                          .apply(SetCount.setCount(RandomValueRange.between(-2.0F, 1.0F)))
                                                                                          .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 1.0F)))
                                                                                          .apply(Smelt.smelted().when(EntityHasProperty.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE))))));
  }

  private static LootTable.Builder dropSlimeballs(SlimeType type) {
    return LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                                         .setRolls(ConstantRange.exactly(1))
                                         .add(ItemLootEntry.lootTableItem(TinkerCommons.slimeball.get(type))
                                                                .apply(SetCount.setCount(RandomValueRange.between(0.0F, 2.0F)))
                                                                .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 1.0F)))));
  }
}
