package slimeknights.tconstruct.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.world.data.StructureRepalleter;
import slimeknights.tconstruct.world.worldgen.islands.IslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.IslandStructure;
import slimeknights.tconstruct.world.worldgen.trees.ExtraRootVariantPlacer;
import slimeknights.tconstruct.world.worldgen.trees.LeaveVineDecorator;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeFungusConfig;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeTreeConfig;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeFungusFeature;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeature;

/**
 * Contains any logic relevant to structure generation, including trees and islands
 */
@SuppressWarnings("unused")
public final class TinkerStructures extends TinkerModule {
  static final Logger log = Util.getLogger("tinker_structures");
  private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, TConstruct.MOD_ID);
  private static final DeferredRegister<StructureType<?>> STRUCTURE_TYPE = DeferredRegister.create(Registries.STRUCTURE_TYPE, TConstruct.MOD_ID);
  private static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE = DeferredRegister.create(Registries.STRUCTURE_PIECE, TConstruct.MOD_ID);
  private static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, TConstruct.MOD_ID);
  private static final DeferredRegister<RootPlacerType<?>> ROOT_PLACERS = DeferredRegister.create(Registries.ROOT_PLACER_TYPE, TConstruct.MOD_ID);


  public TinkerStructures() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    FEATURES.register(bus);
    STRUCTURE_TYPE.register(bus);
    STRUCTURE_PIECE.register(bus);
    TREE_DECORATORS.register(bus);
    ROOT_PLACERS.register(bus);
  }


  /*
   * Misc
   */
  public static final RegistryObject<TreeDecoratorType<LeaveVineDecorator>> leaveVineDecorator = TREE_DECORATORS.register("leave_vines", () -> new TreeDecoratorType<>(LeaveVineDecorator.CODEC));
  public static final RegistryObject<RootPlacerType<ExtraRootVariantPlacer>> extraRootVariantPlacer = ROOT_PLACERS.register("extra_root_variants", () -> new RootPlacerType<>(ExtraRootVariantPlacer.CODEC));

  /*
   * Features
   */
  /** Overworld variant of slimy trees */
  public static final RegistryObject<SlimeTreeFeature> slimeTree = FEATURES.register("slime_tree", () -> new SlimeTreeFeature(SlimeTreeConfig.CODEC));
  /** Nether variant of slimy trees */
  public static final RegistryObject<SlimeFungusFeature> slimeFungus = FEATURES.register("slime_fungus", () -> new SlimeFungusFeature(SlimeFungusConfig.CODEC));

  /* Greenheart trees */
  public static final ResourceKey<ConfiguredFeature<?,?>> earthSlimeTree = key(Registries.CONFIGURED_FEATURE, "earth_slime_tree");
  public static final ResourceKey<ConfiguredFeature<?,?>> earthSlimeIslandTree = key(Registries.CONFIGURED_FEATURE, "earth_slime_island_tree");
  /* Skyroot trees */
  public static final ResourceKey<ConfiguredFeature<?,?>> skySlimeTree = key(Registries.CONFIGURED_FEATURE, "sky_slime_tree");
  public static final ResourceKey<ConfiguredFeature<?,?>> skySlimeIslandTree = key(Registries.CONFIGURED_FEATURE, "sky_slime_island_tree");

  /* Enderslime trees */
  public static final ResourceKey<ConfiguredFeature<?,?>> enderSlimeTree = key(Registries.CONFIGURED_FEATURE, "ender_slime_tree");
  public static final ResourceKey<ConfiguredFeature<?,?>> enderSlimeTreeTall = key(Registries.CONFIGURED_FEATURE, "ender_slime_tree_tall");

  /* Bloodshroom trees */
  public static final ResourceKey<ConfiguredFeature<?,?>> bloodSlimeFungus = key(Registries.CONFIGURED_FEATURE, "blood_slime_fungus");
  public static final ResourceKey<ConfiguredFeature<?,?>> bloodSlimeIslandFungus = key(Registries.CONFIGURED_FEATURE, "blood_slime_island_fungus");

  /* Deprecated ichor tree */
  public static final ResourceKey<ConfiguredFeature<?,?>> ichorSlimeFungus = key(Registries.CONFIGURED_FEATURE, "ichor_slime_fungus");

  /*
   * Structures
   */
  public static final RegistryObject<StructurePieceType> islandPiece = STRUCTURE_PIECE.register("island", () -> IslandPiece::new);
  public static final RegistryObject<StructureType<IslandStructure>> island = STRUCTURE_TYPE.register("island", () -> () -> IslandStructure.CODEC);


  // island structures
  public static final ResourceKey<Structure> earthSlimeIsland = key(Registries.STRUCTURE, "earth_slime_island");
  public static final ResourceKey<Structure> skySlimeIsland = key(Registries.STRUCTURE, "sky_slime_island");
  public static final ResourceKey<Structure> clayIsland = key(Registries.STRUCTURE, "clay_island");
  public static final ResourceKey<Structure> bloodIsland = key(Registries.STRUCTURE, "blood_island");
  public static final ResourceKey<Structure> endSlimeIsland = key(Registries.STRUCTURE, "end_slime_island");

  // island structure sets
  public static final ResourceKey<StructureSet> overworldOceanIsland = key(Registries.STRUCTURE_SET, "overworld_ocean_island");
  public static final ResourceKey<StructureSet> overworldSkyIsland = key(Registries.STRUCTURE_SET, "overworld_sky_island");
  public static final ResourceKey<StructureSet> netherOceanIsland = key(Registries.STRUCTURE_SET, "nether_ocean_island");
  public static final ResourceKey<StructureSet> endSkyIsland = key(Registries.STRUCTURE_SET, "end_sky_island");


  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput packOutput = generator.getPackOutput();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    boolean server = event.includeServer();
    generator.addProvider(server, new StructureRepalleter(packOutput, existingFileHelper));
//    generator.addProvider(server, new StructureUpdater(packOutput, existingFileHelper, TConstruct.MOD_ID, Target.DATA_PACK, "structures"));
//    generator.addProvider(event.includeClient(), new StructureUpdater(packOutput, existingFileHelper, TConstruct.MOD_ID, Target.RESOURCE_PACK, "book/structures"));
  }
}
