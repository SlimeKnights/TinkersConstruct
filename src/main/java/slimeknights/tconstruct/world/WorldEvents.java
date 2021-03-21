package slimeknights.tconstruct.world;

import net.minecraft.entity.EntityClassification;
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
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;

import javax.annotation.Nullable;
import java.util.Map;

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
            configuredStructures.put(TinkerStructures.netherSlimeIsland.get(), new StructureSeparationSettings(30, 22, 14357800));
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
}
