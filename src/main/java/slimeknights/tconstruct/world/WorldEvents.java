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
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToFindFieldException;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldEvents {

  /*@SubscribeEvent
  static void extraSlimeSpawn(WorldEvent.PotentialSpawns event) {
    if (event.getWorld() instanceof ServerWorld) {
      ServerWorld serverWorld = (ServerWorld) event.getWorld();

      if (event.getType() == EntityClassification.MONSTER) {
        // inside a magma slime island?
        if (serverWorld.func_241112_a_().func_235010_a_(event.getPos().down(3), true, TinkerStructures.netherSlimeIsland.get()).isValid() && shouldSpawn(event.getWorld(), event.getPos())) {
          // spawn magma slime, pig zombies have weight 100
          event.getList().clear();
          event.getList().add(new MobSpawnInfo.Spawners(EntityType.MAGMA_CUBE, 150, 4, 6));
        }

        // inside a slime island?
        if (serverWorld.func_241112_a_().func_235010_a_(event.getPos().down(3), true, TinkerStructures.overworldSlimeIsland.get()).isValid() && shouldSpawn(event.getWorld(), event.getPos())) {
          // spawn blue slime, most regular mobs have weight 10
          event.getList().clear();
          event.getList().add(new MobSpawnInfo.Spawners(TinkerWorld.blueSlimeEntity.get(), 15, 2, 4));
        }
      }
    }
  }

  public static boolean shouldSpawn(IWorld worldIn, BlockPos pos) {
    FluidState ifluidstate = worldIn.getFluidState(pos);
    BlockPos down = pos.down();

    if (ifluidstate.isTagged(TinkerTags.Fluids.SLIME) && worldIn.getFluidState(down).isTagged(TinkerTags.Fluids.SLIME)) {
      return true;
    }

    return worldIn.getBlockState(pos.down()).getBlock() instanceof SlimeGrassBlock;
  }*/

  @SubscribeEvent
  static void addDimensionalSpacing(WorldEvent.Load event) {
    if (event.getWorld() instanceof ServerWorld) {
      ServerWorld serverWorld = (ServerWorld) event.getWorld();
      Map<Structure<?>, StructureSeparationSettings> configuredStructures = serverWorld.getChunkProvider().generator.func_235957_b_().func_236195_a_();
      BiomeProvider provider = serverWorld.getChunkProvider().generator.getBiomeProvider();
      try {
        if (provider instanceof NetherBiomeProvider) {
          if (!configuredStructures.containsKey(TinkerStructures.netherSlimeIsland.get())) {
            configuredStructures.put(TinkerStructures.netherSlimeIsland.get(), new StructureSeparationSettings(15, 11, 14357800));
          }
        } else if (provider instanceof EndBiomeProvider) {
          if (!configuredStructures.containsKey(TinkerStructures.endSlimeIsland.get())) {
            configuredStructures.put(TinkerStructures.endSlimeIsland.get(), new StructureSeparationSettings(30, 22, 14357800));
          }
        }
      } catch (UnsupportedOperationException ex) {
        // everywhere in vanilla uses hashmaps, yet somehow I keep getting reports of an immutable map ending up in the stream
        // so just catch and log the exception, not sure what else we can do
        // TODO: can we just add this to the default configs instead?
        TConstruct.log.error("Failed to add slime island placement to world", ex);
      }
    }
  }

  @SubscribeEvent
  static void onBiomeLoad(BiomeLoadingEvent event) {
    BiomeGenerationSettingsBuilder generation = event.getGeneration();

    if (event.getCategory() == Biome.Category.NETHER) {
      if (Config.COMMON.generateSlimeIslands.get()) {
        generation.withStructure(TinkerStructures.NETHER_SLIME_ISLAND);
      }

      if (Config.COMMON.generateCobalt.get()) {
        generation.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TinkerWorld.COBALT_ORE_FEATURE_SMALL);
        generation.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TinkerWorld.COBALT_ORE_FEATURE_LARGE);
      }
    }
    else if (event.getCategory() != Biome.Category.THEEND) {
      if (Config.COMMON.generateSlimeIslands.get()) {
        generation.withStructure(TinkerStructures.SLIME_ISLAND);
        event.getSpawns().withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TinkerWorld.skySlimeEntity.get(), 15, 2, 4));
      }

      if (Config.COMMON.generateCopper.get()) {
        generation.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, TinkerWorld.COPPER_ORE_FEATURE);
      }
    }
    else if (event.getCategory() == Biome.Category.THEEND && doesNameMatchBiomes(event.getName(), Biomes.END_MIDLANDS, Biomes.END_HIGHLANDS, Biomes.END_BARRENS, Biomes.SMALL_END_ISLANDS)) {
      if (Config.COMMON.generateSlimeIslands.get()) {
        generation.withStructure(TinkerStructures.END_SLIME_ISLAND);
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
    BiFunction<FoliageType, Integer, LootEntry> makeSeed = (type, weight) ->
      ItemLootEntry.builder(TinkerWorld.slimeGrassSeeds.get(type)).weight(weight)
                   .acceptFunction(SetCount.builder(new RandomValueRange(2, 4))).build();
    BiFunction<FoliageType, Integer, LootEntry> makeSapling = (type, weight) -> ItemLootEntry.builder(TinkerWorld.slimeSapling.get(type)).weight(weight).build();
    // sky
    injectInto(event, "chests/simple_dungeon", "pool1", () -> makeSeed.apply(FoliageType.SKY, 10));
    injectInto(event, "chests/simple_dungeon", "main", () -> makeSapling.apply(FoliageType.SKY, 10));
    // ichor
    injectInto(event, "chests/nether_bridge", "main", () -> makeSeed.apply(FoliageType.BLOOD, 5));
    injectInto(event, "chests/bastion_bridge", "pool2", () -> makeSapling.apply(FoliageType.BLOOD, 1));
    // ender
    injectInto(event, "chests/end_city_treasure", "main", () -> makeSeed.apply(FoliageType.ENDER, 5));
    injectInto(event, "chests/end_city_treasure", "main", () -> makeSapling.apply(FoliageType.ENDER, 3));
  }
}
