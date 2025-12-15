package slimeknights.tconstruct.common.data.loot;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.loot.AbstractLootTableInjectionProvider;
import slimeknights.mantle.loot.LootTableInjection;
import slimeknights.mantle.loot.function.SetFluidLootFunction;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.FakeRegistryEntry;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.json.loot.AddToolDataFunction;
import slimeknights.tconstruct.library.json.loot.ToolPartLootEntry;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.FoliageType;

/** Add all relevant loot to loot tables */
public class LootTableInjectionProvider extends AbstractLootTableInjectionProvider {
  public LootTableInjectionProvider(PackOutput packOutput) {
    super(packOutput, TConstruct.MOD_ID);
  }

  @SuppressWarnings("removal")
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
    IJsonPredicate<MaterialVariantId> includeInLoot = MaterialPredicate.tag(TinkerTags.Materials.EXCLUDE_FROM_LOOT).inverted();
    RandomMaterial random = RandomMaterial.ancient();
    AddToolDataFunction.Builder ancientToolData2 = AddToolDataFunction.builder().addMaterial(random).addMaterial(random);
    injectGameplay("piglin_bartering")
      .addToPool("main", LootItem.lootTableItem(TinkerSmeltery.scorchedLantern).setWeight(20)
                                 .apply(SetFluidLootFunction.builder(new FluidStack(TinkerFluids.blazingBlood.get(), FluidValues.LANTERN_CAPACITY)))
                                 .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                                 .build())
      .addToPool("main", LootItem.lootTableItem(TinkerTools.battlesign.get())
                                 .setWeight(5)
                                 .apply(ancientToolData2)
                                 .build())
      .addToPool("main", ToolPartLootEntry.entry(TinkerTags.Items.BARTERED_PARTS, RandomMaterial.random().tag(TinkerTags.Materials.BARTERED).allowHidden().build())
                                 .setWeight(8) // same weight as soulspeed boots
                                 .build());

    // spawn chest
    RandomMaterial randomTier1 = RandomMaterial.random().tier(0, 1).material(includeInLoot).build();
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
                                 .apply(ancientToolData2)
                                 .build());
    injectChest("igloo_chest")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.meltingPan.get())
                                 .setWeight(2) // common as a stone axe
                                 .apply(ancientToolData2)
                                 .build());
    inject("hero_of_the_armorer", "gameplay/hero_of_the_village/armorer_gift")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.meltingPan.get())
                                 .setWeight(1) // 1 in 5 chance of a melting pan compared to the chainmail
                                 .apply(ancientToolData2)
                                 .build());

    // find warpicks in pillager outputs, 50% chance to replace the crossbow
    AddToolDataFunction.Builder ancientToolData3 = AddToolDataFunction.builder().addMaterial(random).addMaterial(random).addMaterial(random);
    injectChest("pillager_outpost")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.warPick.get())
                                 .apply(ancientToolData3)
                                 .build());
    // also find them in mineshafts, same pool as iron picks
    injectChest("abandoned_mineshaft")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.warPick.get())
                                 .setWeight(5) // about as often as both diamond swords
                                 .apply(ancientToolData3)
                                 .build());
    injectChest("woodland_mansion")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.warPick.get())
                                 .setWeight(10) // about as often as both diamond swords
                                 .apply(ancientToolData3)
                                 .build());
    inject("hero_of_the_weaponsmith", "gameplay/hero_of_the_village/weaponsmith_gift")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.warPick.get())
                                 .setWeight(1) // makes it a 1 in 4 chance of a war pick
                                 .apply(ancientToolData3)
                                 .build());

    LootTableInjection.Builder bastion = injectChest("bastion_treasure")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.battlesign.get())
                                 .setWeight(12) // about as often as both diamond swords
                                 .apply(ancientToolData2)
                                .build());
    injectChest("bastion_other")
      .addToPool("pool1", LootItem.lootTableItem(TinkerTools.battlesign.get())
                                 .setWeight(3) // bit more common than an iron sword
                                 .apply(ancientToolData2)
                                 .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.1f, 0.9f)))
                                 .build());
    // diamond armor shows in bastions, add in some plate with similar weight to enchanted version
    RandomMaterial randomHighTier = RandomMaterial.random().allowHidden().tier(3, 4).material(includeInLoot).build();
    for (ArmorItem.Type slot : ArmorItem.Type.values()) {
      bastion.addToPool("main", LootItem.lootTableItem(TinkerTools.plateArmor.get(slot))
                                        .setWeight(6)
                                        .apply(AddToolDataFunction.builder()
                                                                  .addMaterial(randomHighTier)
                                                                  .addMaterial(randomHighTier))
                                        .build());
    }

    // swashers are found in the ocean in all sorts of places, maybe there were pirates once
    LootItemConditionalFunction.Builder<?> setFluid = SetFluidLootFunction.builder(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME));
    injectChest("buried_treasure")
      .addToPool("pool3", LootItem.lootTableItem(TinkerTools.swasher.get())
                                  .setWeight(2) // 50% chance because the vanilla stuff in that table is trash anyways
                                  .apply(ancientToolData3)
                                  .apply(setFluid)
                                  .build());
    injectChest("shipwreck_treasure")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.swasher.get())
                                  .setWeight(10) // as common as gold, less common than emerald, twice as common as diamond
                                  .apply(ancientToolData3)
                                 .apply(setFluid)
                                  .build());
    inject("fishing_treasure", new ResourceLocation("gameplay/fishing/treasure"))
      .addToPool("main", LootItem.lootTableItem(TinkerTools.swasher.get())
                                 .setWeight(1) // all treasure from fishing is the same weight
                                 .apply(ancientToolData3)
                                 .apply(setFluid)
                                 .build());

    // fletchers give you some arrows
    inject("hero_of_the_fletcher", "gameplay/hero_of_the_village/fletcher_gift")
      .addToPool("main", LootItem.lootTableItem(TinkerTools.arrow.get())
        .setWeight(10) // bit more rare than tipped arrows
        .apply(ancientToolData2)
        .build());

    // twilight forest - minotaur axe
    String tf = "twilightforest";
    ICondition tfLoaded = new ModLoadedCondition(tf);
    LootPoolEntryContainer minotaurAxe = LootItem.lootTableItem(FakeRegistryEntry.item(TinkerTools.minotaurAxe.getId()))
      .setWeight(1) // TF tends to use 1 for its weight
      .apply(ancientToolData3)
      .build();
    inject("labyrinth_room", new ResourceLocation(tf, "chests/labyrinth_room"), tfLoaded)
      .addToPool("pool1", minotaurAxe)
      .addToPool("pool2", minotaurAxe);
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
