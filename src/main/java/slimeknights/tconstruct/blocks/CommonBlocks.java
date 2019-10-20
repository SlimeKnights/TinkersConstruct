package slimeknights.tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.common.registry.BlockItemRegistryAdapter;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.block.ConsecratedSoilBlock;
import slimeknights.tconstruct.shared.block.GlowBlock;
import slimeknights.tconstruct.shared.block.GraveyardSoilBlock;
import slimeknights.tconstruct.shared.block.GroutBlock;
import slimeknights.tconstruct.shared.block.MetalBlock;
import slimeknights.tconstruct.shared.block.SlimyMudBlock;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

/** General things and crafting related blocks */
@SuppressWarnings("unused")
@ObjectHolder(TConstruct.modID)
public final class CommonBlocks {

  /* Crafting related items */
  public static final GroutBlock grout = injected();
  public static final GraveyardSoilBlock graveyard_soil = injected();
  public static final ConsecratedSoilBlock consecrated_soil = injected();
  public static final SlimyMudBlock slimy_mud_green = injected();
  public static final SlimyMudBlock slimy_mud_blue = injected();
  public static final SlimyMudBlock slimy_mud_magma = injected();
  public static final Block lavawood = injected();
  public static final Block firewood = injected();

  /* Metal Blocks */
  public static final MetalBlock cobalt_block = injected();
  public static final MetalBlock ardite_block = injected();
  public static final MetalBlock manyullyn_block = injected();
  public static final MetalBlock knightslime_block = injected();
  public static final MetalBlock pigiron_block = injected();
  public static final MetalBlock alubrass_block = injected();
  public static final MetalBlock silky_jewel_block = injected();

  public static final SlabBlock lavawood_slab = injected();
  public static final SlabBlock firewood_slab = injected();
  public static final StairsBlock firewood_stairs = injected();
  public static final StairsBlock lavawood_stairs = injected();

  public static final GlowBlock glow = injected();

  @SubscribeEvent
  static void registerBlocks(final RegistryEvent.Register<Block> event) {
    BaseRegistryAdapter<Block> registry = new BaseRegistryAdapter<>(event.getRegistry());

    // crafting related
    registry.register(new GroutBlock(), "grout");

    registry.register(new GraveyardSoilBlock(), "graveyard_soil");
    registry.register(new ConsecratedSoilBlock(), "consecrated_soil");

    registry.register(new SlimyMudBlock(SlimyMudBlock.MudType.SLIMY_MUD_GREEN), "slimy_mud_green");
    registry.register(new SlimyMudBlock(SlimyMudBlock.MudType.SLIMY_MUD_BLUE), "slimy_mud_blue");
    registry.register(new SlimyMudBlock(SlimyMudBlock.MudType.SLIMY_MUD_MAGMA), "slimy_mud_magma");

    DecorativeBlocks.registerSlabsAndStairs(registry, "lavawood", BlockProperties.LAVAWOOD);
    DecorativeBlocks.registerSlabsAndStairs(registry, "firewood", BlockProperties.FIREWOOD);

    // Metal Blocks
    registry.register(new MetalBlock(MetalBlock.MetalType.COBALT), "cobalt_block");
    registry.register(new MetalBlock(MetalBlock.MetalType.ARDITE), "ardite_block");
    registry.register(new MetalBlock(MetalBlock.MetalType.MANYULLYN), "manyullyn_block");
    registry.register(new MetalBlock(MetalBlock.MetalType.KNIGHTSLIME), "knightslime_block");
    registry.register(new MetalBlock(MetalBlock.MetalType.PIGIRON), "pigiron_block");
    registry.register(new MetalBlock(MetalBlock.MetalType.ALUBRASS), "alubrass_block");
    registry.register(new MetalBlock(MetalBlock.MetalType.SILKY_JEWEL), "silky_jewel_block");

    registry.register(new GlowBlock(), "glow");
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

  private CommonBlocks() {}
}
