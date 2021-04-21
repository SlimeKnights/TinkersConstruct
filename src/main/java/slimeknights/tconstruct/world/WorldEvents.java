package slimeknights.tconstruct.world;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.TConfig;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;

public class WorldEvents implements ModInitializer {

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

  /**
   * Helper method to determine the the given Name matches that of any of the given Biomes
   *
   * @param name   - The Name that will be compared to the given Biomes names
   * @param biomes - The Biome that will be used for the check
   */
  private static boolean doesNameMatchBiomes(@Nullable Identifier name, RegistryKey<?>... biomes) {
    for (RegistryKey<?> biome : biomes) {
      if (biome.getValue().equals(name)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void onInitialize() {
    ServerWorldEvents.LOAD.register((minecraftServer, serverWorld) -> {
      Map<StructureFeature<?>, StructureConfig> configuredStructures = serverWorld.getChunkManager().getChunkGenerator().getStructuresConfig().getStructures();
      BiomeSource provider = serverWorld.getChunkManager().getChunkGenerator().getBiomeSource();
      try {
        if (provider instanceof MultiNoiseBiomeSource) {
          if (!configuredStructures.containsKey(TinkerStructures.netherSlimeIsland)) {
            configuredStructures.put(TinkerStructures.netherSlimeIsland, new StructureConfig(15, 11, 14357800));
          }
        } else if (provider instanceof TheEndBiomeSource) {
          if (!configuredStructures.containsKey(TinkerStructures.endSlimeIsland)) {
            configuredStructures.put(TinkerStructures.endSlimeIsland, new StructureConfig(30, 22, 14357800));
          }
        }
      } catch (UnsupportedOperationException ex) {
        // everywhere in vanilla uses hashmaps, yet somehow I keep getting reports of an immutable map ending up in the stream
        // so just catch and log the exception, not sure what else we can do
        // TODO: can we just add this to the default configs instead?
        TConstruct.log.error("Failed to add slime island placement to world", ex);
      }
    });

    if(TConfig.common.generateSlimeIslands) {
      BiomeModifications.addStructure(this::isEnd, TinkerStructures.END_SLIME_ISLAND_STRUCTURE_KEY);
      BiomeModifications.addStructure(this::isNether, TinkerStructures.NETHER_SLIME_ISLAND_STRUCTURE_KEY);
      BiomeModifications.addStructure(this::isOverworld, TinkerStructures.SLIME_ISLAND_STRUCTURE_KEY);

      BiomeModifications.addSpawn(this::isEnd, SpawnGroup.MONSTER, TinkerWorld.skySlimeEntity, 15, 2, 4);
      BiomeModifications.addSpawn(this::isNether, SpawnGroup.MONSTER, EntityType.MAGMA_CUBE, 150, 4, 6);
      BiomeModifications.addSpawn(this::isOverworld, SpawnGroup.MONSTER, TinkerWorld.skySlimeEntity, 15, 2, 4);
    }

    BiomeModifications.addFeature(ctx -> TConfig.common.generateCobalt, GenerationStep.Feature.UNDERGROUND_ORES, TinkerWorld.COBALT_ORE_FEATURE_SMALL_KEY);
    BiomeModifications.addFeature(ctx -> TConfig.common.generateCobalt, GenerationStep.Feature.UNDERGROUND_ORES, TinkerWorld.COBALT_ORE_FEATURE_LARGE_KEY);
    BiomeModifications.addFeature(ctx -> TConfig.common.generateCopper, GenerationStep.Feature.UNDERGROUND_ORES, TinkerWorld.COPPER_ORE_FEATURE_KEY);
  }

  private boolean isOverworld(BiomeSelectionContext ctx) {
    return ctx.getBiome().getCategory() != Biome.Category.NETHER && ctx.getBiome().getCategory() != Biome.Category.THEEND;
  }

  private boolean isNether(BiomeSelectionContext ctx) {
    return ctx.getBiome().getCategory() == Biome.Category.NETHER;
  }

  private boolean isEnd(BiomeSelectionContext ctx) {
    return ctx.getBiome().getCategory() == Biome.Category.THEEND && doesNameMatchBiomes(ctx.getBiomeKey().getValue(), BiomeKeys.END_MIDLANDS, BiomeKeys.END_HIGHLANDS, BiomeKeys.END_BARRENS, BiomeKeys.SMALL_END_ISLANDS);
  }


  /* Loot injection */
  private static boolean foundField = false;
  private static Field lootEntries = null;

  /**
   * Adds a loot entry to the given loot pool
   */
/*  private static void addEntry(LootPool pool, LootPoolEntry entry) {
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
        List<LootPoolEntry> entries = (List<LootPoolEntry>) field;
        entries.add(entry);
      }
    } catch (IllegalAccessException|ClassCastException ex) {
      TConstruct.log.error("Failed to access field", ex);
      lootEntries = null;
    }
  }*/

  /**
   * Injects an entry into a loot pool
   * @param event      Loot table evnet
   * @param tableName  Loot table name
   * @param poolName   Pool name
   * @param entry      Entry to inject
   *//*
  private static void injectInto(LootTableLoadEvent event, String tableName, String poolName, Supplier<LootPoolEntry> entry) {
    Identifier name = event.getName();
    if ("minecraft".equals(name.getNamespace()) && tableName.equals(name.getPath())) {
      LootPool pool = event.getTable().getPool(poolName);
      //noinspection ConstantConditions method is annotated wrongly
      if (pool != null) {
        addEntry(pool, entry.get());
      }
    }
  }*/

/*  @SubscribeEvent
  static void onLootTableLoad(LootTableLoadEvent event) {
    BiFunction<FoliageType, Integer, LootPoolEntry> makeSeed = (type, weight) ->
      ItemEntry.builder(TinkerWorld.slimeGrassSeeds.get(type)).weight(weight)
                   .apply(SetCountLootFunction.builder(new UniformLootTableRange(2, 4))).build();
    BiFunction<FoliageType, Integer, LootPoolEntry> makeSapling = (type, weight) -> ItemEntry.builder(TinkerWorld.slimeSapling.get(type)).weight(weight).build();
    // sky
    injectInto(event, "chests/simple_dungeon", "pool1", () -> makeSeed.apply(FoliageType.SKY, 10));
    injectInto(event, "chests/simple_dungeon", "main", () -> makeSapling.apply(FoliageType.SKY, 10));
    // ichor
    injectInto(event, "chests/nether_bridge", "main", () -> makeSeed.apply(FoliageType.BLOOD, 5));
    injectInto(event, "chests/bastion_bridge", "pool2", () -> makeSapling.apply(FoliageType.BLOOD, 1));
    // ender
    injectInto(event, "chests/end_city_treasure", "main", () -> makeSeed.apply(FoliageType.ENDER, 5));
    injectInto(event, "chests/end_city_treasure", "main", () -> makeSapling.apply(FoliageType.ENDER, 3));
  }*/
}
