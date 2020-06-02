package slimeknights.tconstruct.blocks;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.BlockDeferredRegister;
import slimeknights.tconstruct.library.registration.object.BlockItemObject;
import slimeknights.tconstruct.library.registration.object.BuildingBlockObject;
import slimeknights.tconstruct.shared.block.BeaconBaseBlock;
import slimeknights.tconstruct.shared.block.ConsecratedSoilBlock;
import slimeknights.tconstruct.shared.block.GlowBlock;
import slimeknights.tconstruct.shared.block.GraveyardSoilBlock;
import slimeknights.tconstruct.shared.block.SlimyMudBlock;

import java.util.function.Function;

/**
 * General things and crafting related blocks
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonBlocks {

  private static final Item.Properties generalProps = new Item.Properties().group(TinkerRegistry.tabGeneral);
  private static final Function<Block,? extends BlockItem> defaultItemBlock = (b) -> new BlockTooltipItem(b, generalProps);
  private static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(TConstruct.modID);

  public static void init() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    BLOCKS.register(modEventBus);
  }

  /* Crafting related items */
  public static final BlockItemObject<Block> grout = BLOCKS.register("grout", () -> new Block(BlockProperties.GENERIC_SAND_BLOCK), defaultItemBlock);
  public static final BlockItemObject<GraveyardSoilBlock> graveyard_soil = BLOCKS.register("graveyard_soil", () -> new GraveyardSoilBlock(BlockProperties.GENERIC_SAND_BLOCK), defaultItemBlock);
  public static final BlockItemObject<ConsecratedSoilBlock> consecrated_soil = BLOCKS.register("consecrated_soil", () -> new ConsecratedSoilBlock(BlockProperties.GENERIC_SAND_BLOCK), defaultItemBlock);
  public static final BlockItemObject<SlimyMudBlock> slimy_mud_green = BLOCKS.register("slimy_mud_green", () -> new SlimyMudBlock(BlockProperties.GENERIC_SAND_BLOCK, SlimyMudBlock.MudType.SLIMY_MUD_GREEN), defaultItemBlock);
  public static final BlockItemObject<SlimyMudBlock> slimy_mud_blue = BLOCKS.register("slimy_mud_blue", () -> new SlimyMudBlock(BlockProperties.GENERIC_SAND_BLOCK, SlimyMudBlock.MudType.SLIMY_MUD_BLUE), defaultItemBlock);
  public static final BlockItemObject<SlimyMudBlock> slimy_mud_magma = BLOCKS.register("slimy_mud_magma", () -> new SlimyMudBlock(BlockProperties.GENERIC_SAND_BLOCK, SlimyMudBlock.MudType.SLIMY_MUD_MAGMA), defaultItemBlock);

  /* Metal Blocks */
  public static final BlockItemObject<BeaconBaseBlock> cobalt_block = BLOCKS.register("cobalt_block", () -> new BeaconBaseBlock(BlockProperties.GENERIC_METAL_BLOCK), defaultItemBlock);
  public static final BlockItemObject<BeaconBaseBlock> ardite_block = BLOCKS.register("ardite_block", () -> new BeaconBaseBlock(BlockProperties.GENERIC_METAL_BLOCK), defaultItemBlock);
  public static final BlockItemObject<BeaconBaseBlock> manyullyn_block = BLOCKS.register("manyullyn_block", () -> new BeaconBaseBlock(BlockProperties.GENERIC_METAL_BLOCK), defaultItemBlock);
  public static final BlockItemObject<BeaconBaseBlock> knightslime_block = BLOCKS.register("knightslime_block", () -> new BeaconBaseBlock(BlockProperties.GENERIC_METAL_BLOCK), defaultItemBlock);
  public static final BlockItemObject<BeaconBaseBlock> pigiron_block = BLOCKS.register("pigiron_block", () -> new BeaconBaseBlock(BlockProperties.GENERIC_METAL_BLOCK), defaultItemBlock);
  public static final BlockItemObject<BeaconBaseBlock> alubrass_block = BLOCKS.register("alubrass_block", () -> new BeaconBaseBlock(BlockProperties.GENERIC_METAL_BLOCK), defaultItemBlock);
  public static final BlockItemObject<BeaconBaseBlock> silky_jewel_block = BLOCKS.register("silky_jewel_block", () -> new BeaconBaseBlock(BlockProperties.GENERIC_GEM_BLOCK), defaultItemBlock);

  public static final BuildingBlockObject lavawood = BLOCKS.registerBuilding("lavawood", BlockProperties.LAVAWOOD, generalProps);
  public static final BuildingBlockObject firewood = BLOCKS.registerBuilding("firewood", BlockProperties.FIREWOOD, generalProps);

  public static final RegistryObject<GlowBlock> glow = BLOCKS.register("glow", () -> new GlowBlock(BlockProperties.GLOW));

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(glow.get(), (layer) -> layer == RenderType.getTranslucent());
  }
}
