package slimeknights.tconstruct.common.data.loot;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.loot.AbstractLootTableInjectionProvider;
import slimeknights.mantle.loot.LootTableInjection;
import slimeknights.mantle.loot.function.SetFluidLootFunction;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.json.loot.AddToolDataFunction;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
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
    RandomMaterial random = RandomMaterial.random().build();
    AddToolDataFunction.Builder ancientToolData = AddToolDataFunction.builder().addMaterial(random).addMaterial(random);
    injectGameplay("piglin_bartering")
      .addToPool("main", LootItem.lootTableItem(TinkerSmeltery.scorchedLantern).setWeight(20)
                                 .apply(SetFluidLootFunction.builder(new FluidStack(TinkerFluids.blazingBlood.get(), FluidValues.LANTERN_CAPACITY)))
                                 .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                                 .build())
      .addToPool("main", LootItem.lootTableItem(TinkerTools.battlesign.get())
                                 .setWeight(5)
                                 .apply(ancientToolData)
                                 .build());

    // spawn chest
    RandomMaterial randomTier1 = RandomMaterial.random().tier(1).build();
    RandomMaterial firstWithStat = RandomMaterial.firstWithStat(); // should be wood
    injectChest("spawn_bonus_chest")
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

    // ruined portals give a free flint and brick, because you need one of course
    AddToolDataFunction.Builder buildData = AddToolDataFunction.builder();
    injectChest("ruined_portal").addToPool("main", LootItem.lootTableItem(TinkerTools.flintAndBrick.get())
                                                           .apply(buildData)
                                                           .setWeight(30).build());
    // nether fortress bridge is another place to get flint and brick
    injectChest("nether_bridge").addToPool("main", LootItem.lootTableItem(TinkerTools.flintAndBrick.get())
                                                           .apply(buildData)
                                                           .setWeight(5).build());

    // frypans just show up in some assorted locations
    injectChest("simple_dungeon")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.meltingPan.get())
                                 .setWeight(10) // about as often as both diamond swords
                                 .apply(ancientToolData)
                                 .build());
    injectChest("igloo_chest")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.meltingPan.get())
                                 .setWeight(2) // common as a stone axe
                                 .apply(ancientToolData)
                                 .build());
    inject("hero_of_the_toolsmith", "gameplay/hero_of_the_village/toolsmith_gift")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.meltingPan.get())
                                 .setWeight(2) // makes it a 40% chance of frypan as opposed to an axe variant
                                 .apply(ancientToolData)
                                 .build());

    // find warpicks in pillager outputs, 50% chance to replace the crossbow
    AddToolDataFunction.Builder warPickData = AddToolDataFunction.builder().addMaterial(random).addMaterial(random).addMaterial(random);
    injectChest("pillager_outpost")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.warPick.get())
                                 .apply(warPickData)
                                 .build());
    // also find them in mineshafts, same pool as iron picks
    injectChest("abandoned_mineshaft")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.warPick.get())
                                 .setWeight(5) // about as often as both diamond swords
                                 .apply(warPickData)
                                 .build());
    injectChest("woodland_mansion")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.warPick.get())
                                 .setWeight(10) // about as often as both diamond swords
                                 .apply(warPickData)
                                 .build());
    inject("hero_of_the_weaponsmith", "gameplay/hero_of_the_village/weaponsmith_gift")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.warPick.get())
                                 .setWeight(2) // makes it a 33% chance of war pick as opposed to a stone tool
                                 .apply(warPickData)
                                 .build());

    LootTableInjection.Builder bastion = injectChest("bastion_treasure")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.battlesign.get())
                                 .setWeight(12) // about as often as both diamond swords
                                 .apply(ancientToolData)
                                .build());
    injectChest("bastion_other")
      .addToPool("pool1", LootItem.lootTableItem(TinkerTools.battlesign.get())
                                 .setWeight(3) // bit more common than an iron sword
                                 .apply(ancientToolData)
                                 .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.1f, 0.9f)))
                                 .build());
    // diamond armor shows in bastions, add in some plate with similar weight to enchanted version
    RandomMaterial randomHighTier = RandomMaterial.random().tier(3, 4).build();
    for (ArmorSlotType slot : ArmorSlotType.values()) {
      bastion.addToPool("main", LootItem.lootTableItem(TinkerTools.plateArmor.get(slot))
                                        .setWeight(6)
                                        .apply(AddToolDataFunction.builder()
                                                                  .addMaterial(randomHighTier)
                                                                  .addMaterial(randomHighTier))
                                        .build());
    }

    // TODO: igloo - frypan?
    // TODO: simple dungeon - frypan - main - weight 15
    // TODO: shipwreck treasure - swasher?
    // TODO: buried treasure - swasher - pool3 - weight 1
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
