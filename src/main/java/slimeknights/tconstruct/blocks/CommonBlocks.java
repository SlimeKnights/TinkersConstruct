package slimeknights.tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
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
import slimeknights.tconstruct.shared.block.BeaconBaseBlock;
import slimeknights.tconstruct.shared.block.ConsecratedSoilBlock;
import slimeknights.tconstruct.shared.block.GlowBlock;
import slimeknights.tconstruct.shared.block.GraveyardSoilBlock;
import slimeknights.tconstruct.shared.block.SlimyMudBlock;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

/**
 * General things and crafting related blocks
 */
@SuppressWarnings("unused")
@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonBlocks {

  /* Crafting related items */
  public static final Block grout = injected();
  public static final GraveyardSoilBlock graveyard_soil = injected();
  public static final ConsecratedSoilBlock consecrated_soil = injected();
  public static final SlimyMudBlock slimy_mud_green = injected();
  public static final SlimyMudBlock slimy_mud_blue = injected();
  public static final SlimyMudBlock slimy_mud_magma = injected();
  public static final Block lavawood = injected();
  public static final Block firewood = injected();

  /* Metal Blocks */
  public static final BeaconBaseBlock cobalt_block = injected();
  public static final BeaconBaseBlock ardite_block = injected();
  public static final BeaconBaseBlock manyullyn_block = injected();
  public static final BeaconBaseBlock knightslime_block = injected();
  public static final BeaconBaseBlock pigiron_block = injected();
  public static final BeaconBaseBlock alubrass_block = injected();
  public static final BeaconBaseBlock silky_jewel_block = injected();

  public static final SlabBlock lavawood_slab = injected();
  public static final SlabBlock firewood_slab = injected();
  public static final StairsBlock firewood_stairs = injected();
  public static final StairsBlock lavawood_stairs = injected();

  public static final GlowBlock glow = injected();

  @SubscribeEvent
  static void registerBlocks(final RegistryEvent.Register<Block> event) {
    BlockRegistryAdapter registry = new BlockRegistryAdapter(event.getRegistry());

    // crafting related
    registry.register(new Block(BlockProperties.GENERIC_SAND_BLOCK), "grout");

    registry.register(new GraveyardSoilBlock(BlockProperties.GENERIC_SAND_BLOCK), "graveyard_soil");
    registry.register(new ConsecratedSoilBlock(BlockProperties.GENERIC_SAND_BLOCK), "consecrated_soil");

    registry.register(new SlimyMudBlock(BlockProperties.GENERIC_SAND_BLOCK, SlimyMudBlock.MudType.SLIMY_MUD_GREEN), "slimy_mud_green");
    registry.register(new SlimyMudBlock(BlockProperties.GENERIC_SAND_BLOCK, SlimyMudBlock.MudType.SLIMY_MUD_BLUE), "slimy_mud_blue");
    registry.register(new SlimyMudBlock(BlockProperties.GENERIC_SAND_BLOCK, SlimyMudBlock.MudType.SLIMY_MUD_MAGMA), "slimy_mud_magma");

    registry.registerSlabsAndStairs(new Block(BlockProperties.LAVAWOOD), "lavawood");
    registry.registerSlabsAndStairs(new Block(BlockProperties.FIREWOOD), "firewood");

    // Metal Blocks
    registry.register(new BeaconBaseBlock(BlockProperties.GENERIC_METAL_BLOCK), "cobalt_block");
    registry.register(new BeaconBaseBlock(BlockProperties.GENERIC_METAL_BLOCK), "ardite_block");
    registry.register(new BeaconBaseBlock(BlockProperties.GENERIC_METAL_BLOCK), "manyullyn_block");
    registry.register(new BeaconBaseBlock(BlockProperties.GENERIC_METAL_BLOCK), "knightslime_block");
    registry.register(new BeaconBaseBlock(BlockProperties.GENERIC_METAL_BLOCK), "pigiron_block");
    registry.register(new BeaconBaseBlock(BlockProperties.GENERIC_METAL_BLOCK), "alubrass_block");
    registry.register(new BeaconBaseBlock(BlockProperties.GENERIC_GEM_BLOCK), "silky_jewel_block");

    registry.register(new GlowBlock(BlockProperties.GLOW), "glow");
  }

  @SubscribeEvent
  static void registerBlockItems(final RegistryEvent.Register<Item> event) {
    BlockItemRegistryAdapter registry = new BlockItemRegistryAdapter(event.getRegistry(), TinkerRegistry.tabGeneral);

    registry.registerBlockItem(grout);

    registry.registerBlockItem(graveyard_soil);
    registry.registerBlockItem(consecrated_soil);

    registry.registerBlockItem(slimy_mud_green);
    registry.registerBlockItem(slimy_mud_blue);
    registry.registerBlockItem(slimy_mud_magma);

    registry.registerBlockItem(lavawood);
    registry.registerBlockItem(firewood);

    registry.registerBlockItem(lavawood_slab);
    registry.registerBlockItem(firewood_slab);

    registry.registerBlockItem(lavawood_stairs);
    registry.registerBlockItem(firewood_stairs);

    registry.registerBlockItem(cobalt_block);
    registry.registerBlockItem(ardite_block);
    registry.registerBlockItem(manyullyn_block);
    registry.registerBlockItem(knightslime_block);
    registry.registerBlockItem(pigiron_block);
    registry.registerBlockItem(alubrass_block);
    registry.registerBlockItem(silky_jewel_block);
  }

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(glow, (layer) -> layer == RenderType.translucent());
  }

  private CommonBlocks() {
  }
}
