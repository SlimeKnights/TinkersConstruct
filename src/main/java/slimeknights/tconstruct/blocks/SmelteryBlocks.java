package slimeknights.tconstruct.blocks;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registry.BlockItemRegistryAdapter;
import slimeknights.tconstruct.common.registry.BlockRegistryAdapter;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.block.SearedBlock;
import slimeknights.tconstruct.smeltery.block.SearedGlassBlock;
import slimeknights.tconstruct.smeltery.block.SearedSlabBlock;
import slimeknights.tconstruct.smeltery.block.SearedStairsBlock;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SmelteryBlocks {

  public static final SearedBlock seared_stone = injected();
  public static final SearedSlabBlock seared_stone_slab = injected();
  public static final SearedStairsBlock seared_stone_stairs = injected();

  public static final SearedBlock seared_cobble = injected();
  public static final SearedSlabBlock seared_cobble_slab = injected();
  public static final SearedStairsBlock seared_cobble_stairs = injected();

  public static final SearedBlock seared_paver = injected();
  public static final SearedSlabBlock seared_paver_slab = injected();
  public static final SearedStairsBlock seared_paver_stairs = injected();

  public static final SearedBlock seared_bricks = injected();
  public static final SearedSlabBlock seared_bricks_slab = injected();
  public static final SearedStairsBlock seared_bricks_stairs = injected();

  public static final SearedBlock seared_cracked_bricks = injected();
  public static final SearedSlabBlock seared_cracked_bricks_slab = injected();
  public static final SearedStairsBlock seared_cracked_bricks_stairs = injected();

  public static final SearedBlock seared_fancy_bricks = injected();
  public static final SearedSlabBlock seared_fancy_bricks_slab = injected();
  public static final SearedStairsBlock seared_fancy_bricks_stairs = injected();

  public static final SearedBlock seared_square_bricks = injected();
  public static final SearedSlabBlock seared_square_bricks_slab = injected();
  public static final SearedStairsBlock seared_square_bricks_stairs = injected();

  public static final SearedBlock seared_small_bricks = injected();
  public static final SearedSlabBlock seared_small_bricks_slab = injected();
  public static final SearedStairsBlock seared_small_bricks_stairs = injected();

  public static final SearedBlock seared_triangle_bricks = injected();
  public static final SearedSlabBlock seared_triangle_bricks_slab = injected();
  public static final SearedStairsBlock seared_triangle_bricks_stairs = injected();

  public static final SearedBlock seared_creeper = injected();
  public static final SearedSlabBlock seared_creeper_slab = injected();
  public static final SearedStairsBlock seared_creeper_stairs = injected();

  public static final SearedBlock seared_road = injected();
  public static final SearedSlabBlock seared_road_slab = injected();
  public static final SearedStairsBlock seared_road_stairs = injected();

  public static final SearedBlock seared_tile = injected();
  public static final SearedSlabBlock seared_tile_slab = injected();
  public static final SearedStairsBlock seared_tile_stairs = injected();

  public static final SearedGlassBlock seared_glass = injected();

  public static ImmutableSet<Block> validSmelteryBlocks;
  public static ImmutableSet<Block> searedStairsSlabs;
  public static ImmutableSet<Block> validTinkerTankBlocks;
  public static ImmutableSet<Block> validTinkerTankFloorBlocks;

  @SubscribeEvent
  static void registerBlocks(final RegistryEvent.Register<Block> event) {
    BlockRegistryAdapter registry = new BlockRegistryAdapter(event.getRegistry());

    registry.registerSmelterySlabsAndStairs(new SearedBlock(BlockProperties.SMELTERY), "seared_stone");
    registry.registerSmelterySlabsAndStairs(new SearedBlock(BlockProperties.SMELTERY), "seared_cobble");
    registry.registerSmelterySlabsAndStairs(new SearedBlock(BlockProperties.SMELTERY), "seared_bricks");
    registry.registerSmelterySlabsAndStairs(new SearedBlock(BlockProperties.SMELTERY), "seared_cracked_bricks");
    registry.registerSmelterySlabsAndStairs(new SearedBlock(BlockProperties.SMELTERY), "seared_fancy_bricks");
    registry.registerSmelterySlabsAndStairs(new SearedBlock(BlockProperties.SMELTERY), "seared_square_bricks");
    registry.registerSmelterySlabsAndStairs(new SearedBlock(BlockProperties.SMELTERY), "seared_small_bricks");
    registry.registerSmelterySlabsAndStairs(new SearedBlock(BlockProperties.SMELTERY), "seared_triangle_bricks");
    registry.registerSmelterySlabsAndStairs(new SearedBlock(BlockProperties.SMELTERY), "seared_creeper");
    registry.registerSmelterySlabsAndStairs(new SearedBlock(BlockProperties.SMELTERY), "seared_paver");
    registry.registerSmelterySlabsAndStairs(new SearedBlock(BlockProperties.SMELTERY), "seared_road");
    registry.registerSmelterySlabsAndStairs(new SearedBlock(BlockProperties.SMELTERY), "seared_tile");

    registry.register(new SearedGlassBlock(BlockProperties.SMELTERY_GLASS), "seared_glass");
  }

  @SubscribeEvent
  static void registerBlockItems(final RegistryEvent.Register<Item> event) {
    BlockItemRegistryAdapter registry = new BlockItemRegistryAdapter(event.getRegistry(), TinkerRegistry.tabSmeltery);

    registry.registerBlockItem(seared_stone);
    registry.registerBlockItem(seared_stone_slab);
    registry.registerBlockItem(seared_stone_stairs);

    registry.registerBlockItem(seared_cobble);
    registry.registerBlockItem(seared_cobble_slab);
    registry.registerBlockItem(seared_cobble_stairs);

    registry.registerBlockItem(seared_bricks);
    registry.registerBlockItem(seared_bricks_slab);
    registry.registerBlockItem(seared_bricks_stairs);

    registry.registerBlockItem(seared_cracked_bricks);
    registry.registerBlockItem(seared_cracked_bricks_slab);
    registry.registerBlockItem(seared_cracked_bricks_stairs);

    registry.registerBlockItem(seared_fancy_bricks);
    registry.registerBlockItem(seared_fancy_bricks_slab);
    registry.registerBlockItem(seared_fancy_bricks_stairs);

    registry.registerBlockItem(seared_square_bricks);
    registry.registerBlockItem(seared_square_bricks_slab);
    registry.registerBlockItem(seared_square_bricks_stairs);

    registry.registerBlockItem(seared_small_bricks);
    registry.registerBlockItem(seared_small_bricks_slab);
    registry.registerBlockItem(seared_small_bricks_stairs);

    registry.registerBlockItem(seared_triangle_bricks);
    registry.registerBlockItem(seared_triangle_bricks_slab);
    registry.registerBlockItem(seared_triangle_bricks_stairs);

    registry.registerBlockItem(seared_creeper);
    registry.registerBlockItem(seared_creeper_slab);
    registry.registerBlockItem(seared_creeper_stairs);

    registry.registerBlockItem(seared_paver);
    registry.registerBlockItem(seared_paver_slab);
    registry.registerBlockItem(seared_paver_stairs);

    registry.registerBlockItem(seared_road);
    registry.registerBlockItem(seared_road_slab);
    registry.registerBlockItem(seared_road_stairs);

    registry.registerBlockItem(seared_tile);
    registry.registerBlockItem(seared_tile_slab);
    registry.registerBlockItem(seared_tile_stairs);

    registry.registerBlockItem(seared_glass);

    ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
    builder.add(seared_stone);
    builder.add(seared_cobble);
    builder.add(seared_bricks);
    builder.add(seared_cracked_bricks);
    builder.add(seared_fancy_bricks);
    builder.add(seared_square_bricks);
    builder.add(seared_small_bricks);
    builder.add(seared_triangle_bricks);
    builder.add(seared_creeper);
    builder.add(seared_paver);
    builder.add(seared_road);
    builder.add(seared_tile);

    //builder.add(searedTank);
    //builder.add(smelteryIO);
    builder.add(seared_glass);

    validSmelteryBlocks = builder.build();
    validTinkerTankBlocks = builder.build(); // same blocks right now
    validTinkerTankFloorBlocks = ImmutableSet.of(seared_stone, seared_cobble, seared_bricks, seared_cracked_bricks, seared_fancy_bricks, seared_square_bricks, seared_small_bricks, seared_triangle_bricks, seared_creeper, seared_paver, seared_road, seared_tile, seared_glass);//, smelteryIO);

    // seared furnace ceiling blocks, no smelteryIO or seared glass
    // does not affect sides, those are forced to use seared blocks/tanks where relevant
    builder = ImmutableSet.builder();
    builder.add(seared_stone);
    builder.add(seared_cobble);
    builder.add(seared_bricks);
    builder.add(seared_cracked_bricks);
    builder.add(seared_fancy_bricks);
    builder.add(seared_square_bricks);
    builder.add(seared_small_bricks);
    builder.add(seared_triangle_bricks);
    builder.add(seared_creeper);
    builder.add(seared_paver);
    builder.add(seared_road);
    builder.add(seared_tile);

    builder.add(seared_stone_slab);
    builder.add(seared_cobble_slab);
    builder.add(seared_bricks_slab);
    builder.add(seared_cracked_bricks_slab);
    builder.add(seared_fancy_bricks_slab);
    builder.add(seared_square_bricks_slab);
    builder.add(seared_small_bricks_slab);
    builder.add(seared_triangle_bricks_slab);
    builder.add(seared_creeper_slab);
    builder.add(seared_paver_slab);
    builder.add(seared_road_slab);
    builder.add(seared_tile_slab);

    builder.add(seared_stone_stairs);
    builder.add(seared_cobble_stairs);
    builder.add(seared_bricks_stairs);
    builder.add(seared_cracked_bricks_stairs);
    builder.add(seared_fancy_bricks_stairs);
    builder.add(seared_square_bricks_stairs);
    builder.add(seared_small_bricks_stairs);
    builder.add(seared_triangle_bricks_stairs);
    builder.add(seared_creeper_stairs);
    builder.add(seared_paver_stairs);
    builder.add(seared_road_stairs);
    builder.add(seared_tile_stairs);

    searedStairsSlabs = builder.build();
  }

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(seared_glass, (layer) -> layer == RenderType.getCutout());
  }
}
