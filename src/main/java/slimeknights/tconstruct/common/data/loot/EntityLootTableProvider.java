package slimeknights.tconstruct.common.data.loot;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.SlimePredicate;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
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
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.entity.ArmoredSlimeEntity;

import javax.annotation.Nullable;
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
    this.add(TinkerWorld.skySlimeEntity.get(), dropSlimeballs(SlimeType.SKY, TinkerMaterials.steel.getNugget()));
    this.add(TinkerWorld.enderSlimeEntity.get(), dropSlimeballs(SlimeType.ENDER, TinkerMaterials.knightmetal.getNugget()));
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

  /** Drops an item using the same chances as slimeballs */
  private static LootPoolEntryContainer.Builder<?> slimeball(Item item) {
    return LootItem.lootTableItem(item)
      .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
      .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)));
  }

  private LootTable.Builder dropSlimeballs(SlimeType type, @Nullable Item nugget) {
    LootItemCondition.Builder killedByFrog = killedByFrog();
    Item slimeball = TinkerCommons.slimeball.get(type);
    AlternativesEntry.Builder builder = AlternativesEntry.alternatives(
      // killed by frog, drop exactly 1 slimeball
      LootItem.lootTableItem(slimeball)
        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
        .when(killedByFrog)
    );
    // if given a nugget, add that drop when metal
    if (nugget != null) {
      CompoundTag isMetal = new CompoundTag();
      isMetal.putBoolean(ArmoredSlimeEntity.TAG_METAL, true);
      builder.otherwise(slimeball(nugget).when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, EntityPredicate.Builder.entity().nbt(new NbtPredicate(isMetal)))));
    }
    // otherwise, drop the slimeball
    builder.otherwise(slimeball(slimeball));
    return LootTable.lootTable().withPool(LootPool.lootPool()
      .setRolls(ConstantValue.exactly(1))
      .add(builder)
      .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(SlimePredicate.sized(MinMaxBounds.Ints.exactly(1))))));
  }
}
