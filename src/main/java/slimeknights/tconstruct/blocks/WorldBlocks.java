package slimeknights.tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.common.registry.BlockItemRegistryAdapter;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.block.CongealedSlimeBlock;
import slimeknights.tconstruct.shared.block.OverlayBlock;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.world.block.SlimeDirtBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeLeavesBlock;
import slimeknights.tconstruct.world.block.SlimeSaplingBlock;
import slimeknights.tconstruct.world.block.SlimeTallGrassBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.worldgen.BlueSlimeTree;
import slimeknights.tconstruct.world.worldgen.MagmaSlimeTree;
import slimeknights.tconstruct.world.worldgen.PurpleSlimeTree;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class WorldBlocks {

  /* Ores */
  public static final Block cobalt_ore = injected();
  public static final Block ardite_ore = injected();

  /* Slimestuff */
  public static final SlimeBlock blue_slime = injected();
  public static final SlimeBlock purple_slime = injected();
  public static final SlimeBlock blood_slime = injected();
  public static final SlimeBlock magma_slime = injected();
  public static final SlimeBlock pink_slime = injected();

  public static final CongealedSlimeBlock congealed_green_slime = injected();
  public static final CongealedSlimeBlock congealed_blue_slime = injected();
  public static final CongealedSlimeBlock congealed_purple_slime = injected();
  public static final CongealedSlimeBlock congealed_blood_slime = injected();
  public static final CongealedSlimeBlock congealed_magma_slime = injected();
  public static final CongealedSlimeBlock congealed_pink_slime = injected();

  public static final SlimeDirtBlock green_slime_dirt = injected();
  public static final SlimeDirtBlock blue_slime_dirt = injected();
  public static final SlimeDirtBlock purple_slime_dirt = injected();
  public static final SlimeDirtBlock magma_slime_dirt = injected();

  public static final SlimeGrassBlock blue_vanilla_slime_grass = injected();
  public static final SlimeGrassBlock purple_vanilla_slime_grass = injected();
  public static final SlimeGrassBlock orange_vanilla_slime_grass = injected();
  public static final SlimeGrassBlock blue_green_slime_grass = injected();
  public static final SlimeGrassBlock purple_green_slime_grass = injected();
  public static final SlimeGrassBlock orange_green_slime_grass = injected();
  public static final SlimeGrassBlock blue_blue_slime_grass = injected();
  public static final SlimeGrassBlock purple_blue_slime_grass = injected();
  public static final SlimeGrassBlock orange_blue_slime_grass = injected();
  public static final SlimeGrassBlock blue_purple_slime_grass = injected();
  public static final SlimeGrassBlock purple_purple_slime_grass = injected();
  public static final SlimeGrassBlock orange_purple_slime_grass = injected();
  public static final SlimeGrassBlock blue_magma_slime_grass = injected();
  public static final SlimeGrassBlock purple_magma_slime_grass = injected();
  public static final SlimeGrassBlock orange_magma_slime_grass = injected();

  public static final SlimeLeavesBlock blue_slime_leaves = injected();
  public static final SlimeLeavesBlock purple_slime_leaves = injected();
  public static final SlimeLeavesBlock orange_slime_leaves = injected();

  public static final SlimeTallGrassBlock blue_slime_fern = injected();
  public static final SlimeTallGrassBlock purple_slime_fern = injected();
  public static final SlimeTallGrassBlock orange_slime_fern = injected();

  public static final SlimeTallGrassBlock blue_slime_tall_grass = injected();
  public static final SlimeTallGrassBlock purple_slime_tall_grass = injected();
  public static final SlimeTallGrassBlock orange_slime_tall_grass = injected();

  public static final SlimeSaplingBlock blue_slime_sapling = injected();
  public static final SlimeSaplingBlock orange_slime_sapling = injected();
  public static final SlimeSaplingBlock purple_slime_sapling = injected();

  public static final SlimeVineBlock purple_slime_vine = injected();
  public static final SlimeVineBlock purple_slime_vine_middle = injected();
  public static final SlimeVineBlock purple_slime_vine_end = injected();
  public static final SlimeVineBlock blue_slime_vine = injected();
  public static final SlimeVineBlock blue_slime_vine_middle = injected();
  public static final SlimeVineBlock blue_slime_vine_end = injected();

  @SubscribeEvent
  static void registerBlocks(final RegistryEvent.Register<Block> event) {
    BaseRegistryAdapter<Block> registry = new BaseRegistryAdapter<>(event.getRegistry());

    // Ores
    registry.register(new OverlayBlock(BlockProperties.ORE), "cobalt_ore");
    registry.register(new OverlayBlock(BlockProperties.ORE), "ardite_ore");

    // Slimestuff
    registry.register(new SlimeBlock(BlockProperties.SLIME, false), "blue_slime");
    registry.register(new SlimeBlock(BlockProperties.SLIME, false), "purple_slime");
    registry.register(new SlimeBlock(BlockProperties.SLIME, false), "blood_slime");
    registry.register(new SlimeBlock(BlockProperties.SLIME, false), "magma_slime");
    registry.register(new SlimeBlock(BlockProperties.SLIME, true), "pink_slime");

    registry.register(new CongealedSlimeBlock(BlockProperties.CONGEALED_SLIME, false), "congealed_green_slime");
    registry.register(new CongealedSlimeBlock(BlockProperties.CONGEALED_SLIME, false), "congealed_blue_slime");
    registry.register(new CongealedSlimeBlock(BlockProperties.CONGEALED_SLIME, false), "congealed_purple_slime");
    registry.register(new CongealedSlimeBlock(BlockProperties.CONGEALED_SLIME, false), "congealed_blood_slime");
    registry.register(new CongealedSlimeBlock(BlockProperties.CONGEALED_SLIME, false), "congealed_magma_slime");
    registry.register(new CongealedSlimeBlock(BlockProperties.CONGEALED_SLIME, true), "congealed_pink_slime");

    registry.register(new SlimeDirtBlock(BlockProperties.SLIME_DIRT), "green_slime_dirt");
    registry.register(new SlimeDirtBlock(BlockProperties.SLIME_DIRT), "blue_slime_dirt");
    registry.register(new SlimeDirtBlock(BlockProperties.SLIME_DIRT), "purple_slime_dirt");
    registry.register(new SlimeDirtBlock(BlockProperties.SLIME_DIRT), "magma_slime_dirt");

    for (SlimeGrassBlock.FoliageType type : SlimeGrassBlock.FoliageType.values()) {
      registry.register(new SlimeGrassBlock(BlockProperties.SLIME_GRASS, type), type.getName() + "_vanilla_slime_grass");
      registry.register(new SlimeGrassBlock(BlockProperties.SLIME_GRASS, type), type.getName() + "_green_slime_grass");
      registry.register(new SlimeGrassBlock(BlockProperties.SLIME_GRASS, type), type.getName() + "_blue_slime_grass");
      registry.register(new SlimeGrassBlock(BlockProperties.SLIME_GRASS, type), type.getName() + "_purple_slime_grass");
      registry.register(new SlimeGrassBlock(BlockProperties.SLIME_GRASS, type), type.getName() + "_magma_slime_grass");
    }

    registry.register(new SlimeLeavesBlock(BlockProperties.SLIME_LEAVES, SlimeGrassBlock.FoliageType.BLUE), "blue_slime_leaves");
    registry.register(new SlimeLeavesBlock(BlockProperties.SLIME_LEAVES, SlimeGrassBlock.FoliageType.PURPLE), "purple_slime_leaves");
    registry.register(new SlimeLeavesBlock(BlockProperties.SLIME_LEAVES, SlimeGrassBlock.FoliageType.ORANGE), "orange_slime_leaves");

    for (SlimeGrassBlock.FoliageType foliageType : SlimeGrassBlock.FoliageType.values()) {
      for (SlimeTallGrassBlock.SlimePlantType plantType : SlimeTallGrassBlock.SlimePlantType.values()) {
        registry.register(new SlimeTallGrassBlock(BlockProperties.TALL_GRASS, foliageType, plantType), foliageType.getName() + "_slime_" + plantType.getName());
      }
    }

    registry.register(new SlimeSaplingBlock(new BlueSlimeTree(false), BlockProperties.SAPLING), "blue_slime_sapling");
    registry.register(new SlimeSaplingBlock(new MagmaSlimeTree(), BlockProperties.SAPLING), "orange_slime_sapling");
    registry.register(new SlimeSaplingBlock(new PurpleSlimeTree(false), BlockProperties.SAPLING), "purple_slime_sapling");

    registry.register(new SlimeVineBlock(BlockProperties.VINE, SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.START), "purple_slime_vine");
    registry.register(new SlimeVineBlock(BlockProperties.VINE, SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.MIDDLE), "purple_slime_vine_middle");
    registry.register(new SlimeVineBlock(BlockProperties.VINE, SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.END), "purple_slime_vine_end");

    registry.register(new SlimeVineBlock(BlockProperties.VINE, SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.START), "blue_slime_vine");
    registry.register(new SlimeVineBlock(BlockProperties.VINE, SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.MIDDLE), "blue_slime_vine_middle");
    registry.register(new SlimeVineBlock(BlockProperties.VINE, SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.END), "blue_slime_vine_end");
  }

  @SubscribeEvent
  static void registerBlockItems(final RegistryEvent.Register<Item> event) {
    BlockItemRegistryAdapter registry = new BlockItemRegistryAdapter(event.getRegistry(), TinkerRegistry.tabWorld);

    // Ores
    registry.registerBlockItem(cobalt_ore);
    registry.registerBlockItem(ardite_ore);

    // Slimes
    registry.registerBlockItem(blue_slime);
    registry.registerBlockItem(purple_slime);
    registry.registerBlockItem(blood_slime);
    registry.registerBlockItem(magma_slime);
    registry.registerBlockItem(pink_slime);

    registry.registerBlockItem(congealed_green_slime);
    registry.registerBlockItem(congealed_blue_slime);
    registry.registerBlockItem(congealed_purple_slime);
    registry.registerBlockItem(congealed_blood_slime);
    registry.registerBlockItem(congealed_magma_slime);
    registry.registerBlockItem(congealed_pink_slime);

    registry.registerBlockItem(green_slime_dirt);
    registry.registerBlockItem(blue_slime_dirt);
    registry.registerBlockItem(purple_slime_dirt);
    registry.registerBlockItem(magma_slime_dirt);

    registry.registerBlockItem(blue_vanilla_slime_grass);
    registry.registerBlockItem(purple_vanilla_slime_grass);
    registry.registerBlockItem(orange_vanilla_slime_grass);
    registry.registerBlockItem(blue_green_slime_grass);
    registry.registerBlockItem(purple_green_slime_grass);
    registry.registerBlockItem(orange_green_slime_grass);
    registry.registerBlockItem(blue_blue_slime_grass);
    registry.registerBlockItem(purple_blue_slime_grass);
    registry.registerBlockItem(orange_blue_slime_grass);
    registry.registerBlockItem(blue_purple_slime_grass);
    registry.registerBlockItem(purple_purple_slime_grass);
    registry.registerBlockItem(orange_purple_slime_grass);
    registry.registerBlockItem(blue_magma_slime_grass);
    registry.registerBlockItem(purple_magma_slime_grass);
    registry.registerBlockItem(orange_magma_slime_grass);

    registry.registerBlockItem(blue_slime_leaves);
    registry.registerBlockItem(purple_slime_leaves);
    registry.registerBlockItem(orange_slime_leaves);

    registry.registerBlockItem(blue_slime_fern);
    registry.registerBlockItem(purple_slime_fern);
    registry.registerBlockItem(orange_slime_fern);

    registry.registerBlockItem(blue_slime_tall_grass);
    registry.registerBlockItem(purple_slime_tall_grass);
    registry.registerBlockItem(orange_slime_tall_grass);

    registry.registerBlockItem(blue_slime_sapling);
    registry.registerBlockItem(orange_slime_sapling);
    registry.registerBlockItem(purple_slime_sapling);

    registry.registerBlockItem(purple_slime_vine);
    registry.registerBlockItem(purple_slime_vine_middle);
    registry.registerBlockItem(purple_slime_vine_end);

    registry.registerBlockItem(blue_slime_vine);
    registry.registerBlockItem(blue_slime_vine_middle);
    registry.registerBlockItem(blue_slime_vine_end);
  }

  private WorldBlocks() {}
}
