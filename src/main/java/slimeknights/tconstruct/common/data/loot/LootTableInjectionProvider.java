package slimeknights.tconstruct.common.data.loot;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.loot.function.SetFluidLootFunction;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.tinkering.AbstractLootTableInjectionProvider;
import slimeknights.tconstruct.library.json.loot.AddToolDataFunction;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.FoliageType;

/** Add all relevant loot to loot tables */
public class LootTableInjectionProvider extends AbstractLootTableInjectionProvider {
  public LootTableInjectionProvider(DataGenerator generator) {
    super(generator, TConstruct.MOD_ID);
  }

  @Override
  protected void addTables() {
    // slimy foliage injections
    // earth/sky
    inject("slimy_foliage_dungeon", "chests/simple_dungeon", ConfigEnabledCondition.SLIMY_LOOT_CHESTS)
      .addToPool("main", makeSapling(FoliageType.EARTH, 3), makeSapling(FoliageType.SKY, 7))
      .addToPool("pool1", makeSeed(FoliageType.EARTH, 3), makeSeed(FoliageType.SKY, 7));
    // blood
    inject("slimy_foliage_nether_fortress", "chests/nether_bridge", ConfigEnabledCondition.SLIMY_LOOT_CHESTS)
      .addToPool("main", makeSeed(FoliageType.BLOOD, 5));
    inject("slimy_foliage_bastion", "chests/bastion_bridge", ConfigEnabledCondition.SLIMY_LOOT_CHESTS)
      .addToPool("main", makeSapling(FoliageType.BLOOD, 1));
    // ender
    inject("slimy_foliage_end_city", "chests/end_city_treasure", ConfigEnabledCondition.SLIMY_LOOT_CHESTS)
      .addToPool("main", makeSeed(FoliageType.ENDER, 5), makeSapling(FoliageType.ENDER, 3));

    // bartering
    inject("piglin_bartering", "gameplay/piglin_bartering")
      .addToPool("main", LootItem.lootTableItem(TinkerSmeltery.scorchedLantern).setWeight(20)
                                 .apply(SetFluidLootFunction.builder(new FluidStack(TinkerFluids.blazingBlood.get(), FluidValues.LANTERN_CAPACITY)))
                                 .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                                 .build());

    // spawn chest
    RandomMaterial randomTier1 = RandomMaterial.random().tier(1).build();
    RandomMaterial firstWithStat = RandomMaterial.firstWithStat(); // should be wood
    inject("spawn_bonus_chest", "chests/spawn_bonus_chest")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.handAxe.get())
                                 .setWeight(2)
                                 .apply(AddToolDataFunction.builder()
                                                           .addMaterial(randomTier1)
                                                           .addMaterial(firstWithStat)
                                                           .addMaterial(randomTier1))
                                 .build())
    .addToPool("pool1", LootItem.lootTableItem(TinkerTools.pickaxe.get())
                                .setWeight(2)
                                .apply(AddToolDataFunction.builder()
                                                          .addMaterial(randomTier1)
                                                          .addMaterial(firstWithStat)
                                                          .addMaterial(randomTier1))
                                .build());
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Loot Table Injections";
  }

  /** Makes a seed injection loot entry */
  private static LootPoolEntryContainer makeSeed(FoliageType type, int weight) {
    return LootItem.lootTableItem(TinkerWorld.slimeGrassSeeds.get(type)).setWeight(weight)
                   .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))).build();
  }

  /** Makes a sapling injection loot entry */
  private static LootPoolEntryContainer makeSapling(FoliageType type, int weight) {
    return LootItem.lootTableItem(TinkerWorld.slimeSapling.get(type)).setWeight(weight).build();
  }
}
