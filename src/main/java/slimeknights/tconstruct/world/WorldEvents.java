package slimeknights.tconstruct.world;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldEvents {

  @SubscribeEvent
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
        if (serverWorld.func_241112_a_().func_235010_a_(event.getPos().down(3), true, TinkerStructures.slimeIsland.get()).isValid() && shouldSpawn(event.getWorld(), event.getPos())) {
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
  }

  @SubscribeEvent
  static void onBiomeLoad(BiomeLoadingEvent event) {
    BiomeGenerationSettingsBuilder generation = event.getGeneration();

    if (event.getCategory() == Biome.Category.NETHER) {
      if (Config.COMMON.generateSlimeIslands.get()) {
        generation.withStructure(TinkerStructures.NETHER_SLIME_ISLAND);
      }

      if (Config.COMMON.generateCobalt.get()) {
        addNetherOre(generation, TinkerWorld.cobaltOre, Config.COMMON.veinCountCobalt);
      }

      if (Config.COMMON.generateArdite.get()) {
        addNetherOre(generation, TinkerWorld.arditeOre, Config.COMMON.veinCountArdite);
      }
    }
    else if (event.getCategory() != Biome.Category.THEEND) {
      if (Config.COMMON.generateSlimeIslands.get()) {
        generation.withStructure(TinkerStructures.SLIME_ISLAND);
        event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(TinkerWorld.blueSlimeEntity.get(), 15, 2, 4));
      }

      if (Config.COMMON.generateCopper.get()) {
        generation.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
          Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241882_a, TinkerWorld.copperOre.get().getDefaultState(), 17))
            .withPlacement(Placement.field_242907_l.configure(new TopSolidRangeConfig(30, 0, 90))).func_242728_a().func_242731_b(Config.COMMON.veinCountCopper.get()));
      }
    }
  }

  /**
   * Adds an ore with nether placement
   * @param generation  biome generation settings
   * @param block  Block to generate
   * @param count  Vein side config
   */
  private static void addNetherOre(BiomeGenerationSettingsBuilder generation, Supplier<? extends Block> block, ForgeConfigSpec.ConfigValue<Integer> count) {
    // FIXME: constant config
    int veinCount = count.get() / 2;

    generation.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION,
      Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241883_b, block.get().getDefaultState(), 5))
        .withPlacement(Placement.field_242907_l.configure(new TopSolidRangeConfig(32, 0, 64))).func_242728_a().func_242731_b(veinCount));

    generation.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION,
      Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241883_b, block.get().getDefaultState(), 5))
        .func_242733_d(128).func_242728_a().func_242731_b(veinCount));
  }
}
