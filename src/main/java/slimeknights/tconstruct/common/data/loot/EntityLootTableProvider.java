package slimeknights.tconstruct.common.data.loot;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SlimePredicate;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Map.Entry;
import java.util.stream.Stream;

public class EntityLootTableProvider extends EntityLootSubProvider {
  protected EntityLootTableProvider() {
    super(FeatureFlags.REGISTRY.allFlags());
  }

  @Override
  protected Stream<EntityType<?>> getKnownEntityTypes() {
    return ForgeRegistries.ENTITY_TYPES.getEntries().stream()
                                   // remove earth slime entity, we redirect to the vanilla loot table
                                   .filter(entry -> TConstruct.MOD_ID.equals(entry.getKey().location().getNamespace()))
                                   .map(Entry::getValue);
  }

  @Override
  public void generate() {
    this.add(TinkerWorld.skySlimeEntity.get(), dropSlimeballs(SlimeType.SKY));
    this.add(TinkerWorld.enderSlimeEntity.get(), dropSlimeballs(SlimeType.ENDER));
    this.add(TinkerWorld.terracubeEntity.get(),
                           LootTable.lootTable().withPool(LootPool.lootPool()
                                                                   .setRolls(ConstantValue.exactly(1))
                                                                   .add(LootItem.lootTableItem(Items.CLAY_BALL)
                                                                                          .apply(SetItemCountFunction.setCount(UniformGenerator.between(-2.0F, 1.0F)))
                                                                                          .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                                                                                          .apply(SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE))))));

    LootItemCondition.Builder killedByFrog = killedByFrog();
    this.add(TinkerWorld.terracubeEntity.get(),
             LootTable.lootTable()
                      .withPool(LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(LootItem.lootTableItem(Items.CLAY_BALL)
                                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(-2.0F, 1.0F)))
                                                     .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                                                     .when(killedByFrog.invert())
                                                     .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(SlimePredicate.sized(MinMaxBounds.Ints.atLeast(2))))))
                                        .add(LootItem.lootTableItem(TinkerSmeltery.searedCobble) // TODO: can I come up with something more exciting?
                                                     .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                                     .when(killedByFrog))
                                        .apply(SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))));



  }

  private LootTable.Builder dropSlimeballs(SlimeType type) {
    LootItemCondition.Builder killedByFrog = killedByFrog();
    Item slimeball = TinkerCommons.slimeball.get(type);
    return LootTable.lootTable().withPool(
      LootPool.lootPool()
              .setRolls(ConstantValue.exactly(1))
              .add(LootItem.lootTableItem(slimeball)
                           .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                           .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                           .when(killedByFrog().invert()))
              .add(LootItem.lootTableItem(slimeball)
                           .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                           .when(killedByFrog))
              .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(SlimePredicate.sized(MinMaxBounds.Ints.exactly(1))))));
  }
}
