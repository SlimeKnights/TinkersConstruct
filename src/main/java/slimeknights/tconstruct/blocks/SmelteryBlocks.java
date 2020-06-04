package slimeknights.tconstruct.blocks;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registry.BlockItemRegistryAdapter;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.BlockDeferredRegister;
import slimeknights.tconstruct.library.registration.object.BlockItemObject;
import slimeknights.tconstruct.library.registration.object.BuildingBlockObject;
import slimeknights.tconstruct.smeltery.block.SearedGlassBlock;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SmelteryBlocks {

  private static final Item.Properties smelteryProps = new Item.Properties().group(TinkerRegistry.tabSmeltery);
  private static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(TConstruct.modID);

  public static void init() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    BLOCKS.register(modEventBus);
  }

  // TODO: registerBuilding does not handle BlockTooltipItem yet.
  public static final BuildingBlockObject seared_stone = BLOCKS.registerBuilding("seared_stone", BlockProperties.SMELTERY, smelteryProps);
  public static final BuildingBlockObject seared_cobble = BLOCKS.registerBuilding("seared_cobble", BlockProperties.SMELTERY, smelteryProps);
  public static final BuildingBlockObject seared_paver = BLOCKS.registerBuilding("seared_paver", BlockProperties.SMELTERY, smelteryProps);
  public static final BuildingBlockObject seared_bricks = BLOCKS.registerBuilding("seared_bricks", BlockProperties.SMELTERY, smelteryProps);
  public static final BuildingBlockObject seared_cracked_bricks = BLOCKS.registerBuilding("seared_cracked_bricks", BlockProperties.SMELTERY, smelteryProps);
  public static final BuildingBlockObject seared_fancy_bricks = BLOCKS.registerBuilding("seared_fancy_bricks", BlockProperties.SMELTERY, smelteryProps);
  public static final BuildingBlockObject seared_square_bricks = BLOCKS.registerBuilding("seared_square_bricks", BlockProperties.SMELTERY, smelteryProps);
  public static final BuildingBlockObject seared_small_bricks = BLOCKS.registerBuilding("seared_small_bricks", BlockProperties.SMELTERY, smelteryProps);
  public static final BuildingBlockObject seared_triangle_bricks = BLOCKS.registerBuilding("seared_triangle_bricks", BlockProperties.SMELTERY, smelteryProps);
  public static final BuildingBlockObject seared_creeper = BLOCKS.registerBuilding("seared_creeper", BlockProperties.SMELTERY, smelteryProps);
  public static final BuildingBlockObject seared_road = BLOCKS.registerBuilding("seared_road", BlockProperties.SMELTERY, smelteryProps);
  public static final BuildingBlockObject seared_tile = BLOCKS.registerBuilding("seared_tile", BlockProperties.SMELTERY, smelteryProps);

  public static final BlockItemObject<SearedGlassBlock> seared_glass = BLOCKS.register("seared_glass", () -> new SearedGlassBlock(BlockProperties.SMELTERY_GLASS), (b) -> new BlockTooltipItem(b, smelteryProps));

  public static ImmutableSet<Block> validSmelteryBlocks;
  public static ImmutableSet<Block> searedStairsSlabs;
  public static ImmutableSet<Block> validTinkerTankBlocks;
  public static ImmutableSet<Block> validTinkerTankFloorBlocks;

  @SubscribeEvent
  static void registerBlockItems(final FMLCommonSetupEvent event) {
    ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
    builder.add(seared_stone.get());
    builder.add(seared_cobble.get());
    builder.add(seared_bricks.get());
    builder.add(seared_cracked_bricks.get());
    builder.add(seared_fancy_bricks.get());
    builder.add(seared_square_bricks.get());
    builder.add(seared_small_bricks.get());
    builder.add(seared_triangle_bricks.get());
    builder.add(seared_creeper.get());
    builder.add(seared_paver.get());
    builder.add(seared_road.get());
    builder.add(seared_tile.get());

    //builder.add(searedTank);
    //builder.add(smelteryIO);
    builder.add(seared_glass.get());

    validSmelteryBlocks = builder.build();
    validTinkerTankBlocks = builder.build(); // same blocks right now
    validTinkerTankFloorBlocks = ImmutableSet.of(seared_stone.get(), seared_cobble.get(), seared_bricks.get(), seared_cracked_bricks.get(), seared_fancy_bricks.get(), seared_square_bricks.get(), seared_small_bricks.get(), seared_triangle_bricks.get(), seared_creeper.get(), seared_paver.get(), seared_road.get(), seared_tile.get(), seared_glass.get());//, smelteryIO);

    // seared furnace ceiling blocks, no smelteryIO or seared glass
    // does not affect sides, those are forced to use seared blocks/tanks where relevant
    builder = ImmutableSet.builder();
    builder.add(seared_stone.get());
    builder.add(seared_cobble.get());
    builder.add(seared_bricks.get());
    builder.add(seared_cracked_bricks.get());
    builder.add(seared_fancy_bricks.get());
    builder.add(seared_square_bricks.get());
    builder.add(seared_small_bricks.get());
    builder.add(seared_triangle_bricks.get());
    builder.add(seared_creeper.get());
    builder.add(seared_paver.get());
    builder.add(seared_road.get());
    builder.add(seared_tile.get());

    builder.add(seared_stone.getSlab());
    builder.add(seared_cobble.getSlab());
    builder.add(seared_bricks.getSlab());
    builder.add(seared_cracked_bricks.getSlab());
    builder.add(seared_fancy_bricks.getSlab());
    builder.add(seared_square_bricks.getSlab());
    builder.add(seared_small_bricks.getSlab());
    builder.add(seared_triangle_bricks.getSlab());
    builder.add(seared_creeper.getSlab());
    builder.add(seared_paver.getSlab());
    builder.add(seared_road.getSlab());
    builder.add(seared_tile.getSlab());

    builder.add(seared_stone.getStairs());
    builder.add(seared_cobble.getStairs());
    builder.add(seared_bricks.getStairs());
    builder.add(seared_cracked_bricks.getStairs());
    builder.add(seared_fancy_bricks.getStairs());
    builder.add(seared_square_bricks.getStairs());
    builder.add(seared_small_bricks.getStairs());
    builder.add(seared_triangle_bricks.getStairs());
    builder.add(seared_creeper.getStairs());
    builder.add(seared_paver.getStairs());
    builder.add(seared_road.getStairs());
    builder.add(seared_tile.getStairs());

    searedStairsSlabs = builder.build();
  }

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(seared_glass.get(), (layer) -> layer == RenderType.getCutout());
  }
}
