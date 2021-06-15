package slimeknights.tconstruct.world;

import net.minecraft.entity.EntityClassification;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToFindFieldException;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.json.SetFluidLootFunction;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldEvents {
  @SubscribeEvent
  static void onBiomeLoad(BiomeLoadingEvent event) {
    BiomeGenerationSettingsBuilder generation = event.getGeneration();

    if (event.getCategory() == Biome.Category.NETHER) {
      if (Config.COMMON.generateBloodIslands.get()) {
        generation.withStructure(TinkerStructures.BLOOD_SLIME_ISLAND);
      }

      if (Config.COMMON.generateCobalt.get()) {
        generation.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TinkerWorld.COBALT_ORE_FEATURE_SMALL);
        generation.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TinkerWorld.COBALT_ORE_FEATURE_LARGE);
      }
    }
    else if (event.getCategory() != Biome.Category.THEEND) {
      // normal sky islands
      if (Config.COMMON.generateSkySlimeIslands.get()) {
        generation.withStructure(TinkerStructures.SKY_SLIME_ISLAND);
        event.getSpawns().withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TinkerWorld.skySlimeEntity.get(), 15, 2, 4));
      }
      if (Config.COMMON.generateSkySlimeIslands.get()) {
        generation.withStructure(TinkerStructures.CLAY_ISLAND);
      }
      // ocean islands
      if (event.getCategory() == Category.OCEAN && Config.COMMON.generateEarthSlimeIslands.get()) {
        generation.withStructure(TinkerStructures.EARTH_SLIME_ISLAND);
        event.getSpawns().withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TinkerWorld.earthSlimeEntity.get(), 15, 2, 4));
      }

      if (Config.COMMON.generateCopper.get()) {
        generation.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, TinkerWorld.COPPER_ORE_FEATURE);
      }
    }
    else if (event.getCategory() == Biome.Category.THEEND && !doesNameMatchBiomes(event.getName(), Biomes.THE_END, Biomes.THE_VOID)) {
      if (Config.COMMON.generateEndSlimeIslands.get()) {
        generation.withStructure(TinkerStructures.END_SLIME_ISLAND);
        event.getSpawns().withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TinkerWorld.enderSlimeEntity.get(), 15, 2, 4));
      }
    }
  }

  /**
   * Helper method to determine the the given Name matches that of any of the given Biomes
   * @param name - The Name that will be compared to the given Biomes names
   * @param biomes - The Biome that will be used for the check
   */
  private static boolean doesNameMatchBiomes(@Nullable ResourceLocation name, RegistryKey<?>... biomes) {
    for (RegistryKey<?> biome : biomes) {
      if (biome.getLocation().equals(name)) {
        return true;
      }
    }
    return false;
  }


  /* Loot injection */
  private static boolean foundField = false;
  private static Field lootEntries = null;

  /**
   * Adds a loot entry to the given loot pool
   * @param pool   Pool
   * @param entry  Entry
   */
  @SuppressWarnings("unchecked")
  private static void addEntry(LootPool pool, LootEntry entry) {
    // fetch field
    if (!foundField) {
      try {
        lootEntries = ObfuscationReflectionHelper.findField(LootPool.class, "field_186453_a");
        lootEntries.setAccessible(true);
        foundField = true;
      } catch (UnableToFindFieldException ex) {
        TConstruct.log.error("Failed to find field", ex);
        foundField = true;
        return;
      }
    }
    // access field
    try {
      Object field = lootEntries.get(pool);
      if (field instanceof List) {
        List<LootEntry> entries = (List<LootEntry>) field;
        entries.add(entry);
      }
    } catch (IllegalAccessException|ClassCastException ex) {
      TConstruct.log.error("Failed to access field", ex);
      lootEntries = null;
    }
  }

  /**
   * Injects an entry into a loot pool
   * @param event      Loot table evnet
   * @param tableName  Loot table name
   * @param poolName   Pool name
   * @param entry      Entry to inject
   */
  private static void injectInto(LootTableLoadEvent event, String tableName, String poolName, Supplier<LootEntry> entry) {
    ResourceLocation name = event.getName();
    if ("minecraft".equals(name.getNamespace()) && tableName.equals(name.getPath())) {
      LootPool pool = event.getTable().getPool(poolName);
      //noinspection ConstantConditions method is annotated wrongly
      if (pool != null) {
        addEntry(pool, entry.get());
      }
    }
  }

  @SubscribeEvent
  static void onLootTableLoad(LootTableLoadEvent event) {
    BiFunction<SlimeType, Integer, LootEntry> makeSeed = (type, weight) ->
      ItemLootEntry.builder(TinkerWorld.slimeGrassSeeds.get(type)).weight(weight)
                   .acceptFunction(SetCount.builder(new RandomValueRange(2, 4))).build();
    BiFunction<SlimeType, Integer, LootEntry> makeSapling = (type, weight) -> ItemLootEntry.builder(TinkerWorld.slimeSapling.get(type)).weight(weight).build();
    // sky
    injectInto(event, "chests/simple_dungeon", "pool1", () -> makeSeed.apply(SlimeType.EARTH, 3));
    injectInto(event, "chests/simple_dungeon", "pool1", () -> makeSeed.apply(SlimeType.SKY, 7));
    injectInto(event, "chests/simple_dungeon", "main", () -> makeSapling.apply(SlimeType.EARTH, 3));
    injectInto(event, "chests/simple_dungeon", "main", () -> makeSapling.apply(SlimeType.SKY, 7));
    // ichor
    injectInto(event, "chests/nether_bridge", "main", () -> makeSeed.apply(SlimeType.BLOOD, 5));
    injectInto(event, "chests/bastion_bridge", "pool2", () -> makeSapling.apply(SlimeType.BLOOD, 1));
    // ender
    injectInto(event, "chests/end_city_treasure", "main", () -> makeSeed.apply(SlimeType.ENDER, 5));
    injectInto(event, "chests/end_city_treasure", "main", () -> makeSapling.apply(SlimeType.ENDER, 3));
    // barter for molten blaze lanterns
    injectInto(event, "gameplay/piglin_bartering", "main",
               () -> ItemLootEntry.builder(TinkerSmeltery.scorchedLantern).weight(20)
                                  .acceptFunction(SetFluidLootFunction.builder(new FluidStack(TinkerFluids.blazingBlood.get(), FluidAttributes.BUCKET_VOLUME / 10)))
                                  .build());
  }
}
