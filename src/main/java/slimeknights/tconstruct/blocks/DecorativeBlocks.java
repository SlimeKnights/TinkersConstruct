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
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.common.registry.BlockItemRegistryAdapter;
import slimeknights.tconstruct.common.registry.BlockRegistryAdapter;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.block.ClearGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DecorativeBlocks {

  /* Decorative Blocks */
  public static final ClearGlassBlock clear_glass = injected();
  public static final ClearStainedGlassBlock white_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock orange_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock magenta_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock light_blue_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock yellow_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock lime_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock pink_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock gray_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock light_gray_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock cyan_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock purple_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock blue_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock brown_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock green_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock red_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock black_clear_stained_glass = injected();

  public static final Block mud_bricks = injected();
  public static final SlabBlock mud_bricks_slab = injected();
  public static final StairsBlock mud_bricks_stairs = injected();

  public static final Block dried_clay = injected();
  public static final SlabBlock dried_clay_slab = injected();
  public static final StairsBlock dried_clay_stairs = injected();

  public static final Block dried_clay_bricks = injected();
  public static final SlabBlock dried_clay_bricks_slab = injected();
  public static final StairsBlock dried_clay_bricks_stairs = injected();

  @SubscribeEvent
  static void registerBlocks(final RegistryEvent.Register<Block> event) {
    BlockRegistryAdapter registry = new BlockRegistryAdapter(event.getRegistry());

    registerGlass(registry);

    registry.registerSlabsAndStairs(new Block(BlockProperties.MUD_BRICKS), "mud_bricks");
    registry.registerSlabsAndStairs(new Block(BlockProperties.DRIED_CLAY), "dried_clay");
    registry.registerSlabsAndStairs(new Block(BlockProperties.DRIED_CLAY_BRICKS), "dried_clay_bricks");
  }

  @SubscribeEvent
  static void registerBlockItems(final RegistryEvent.Register<Item> event) {
    BlockItemRegistryAdapter registry = new BlockItemRegistryAdapter(event.getRegistry(), TinkerRegistry.tabGeneral);

    registry.registerBlockItem(clear_glass);
    registry.registerBlockItem(white_clear_stained_glass);
    registry.registerBlockItem(orange_clear_stained_glass);
    registry.registerBlockItem(magenta_clear_stained_glass);
    registry.registerBlockItem(light_blue_clear_stained_glass);
    registry.registerBlockItem(yellow_clear_stained_glass);
    registry.registerBlockItem(lime_clear_stained_glass);
    registry.registerBlockItem(pink_clear_stained_glass);
    registry.registerBlockItem(gray_clear_stained_glass);
    registry.registerBlockItem(light_gray_clear_stained_glass);
    registry.registerBlockItem(cyan_clear_stained_glass);
    registry.registerBlockItem(purple_clear_stained_glass);
    registry.registerBlockItem(blue_clear_stained_glass);
    registry.registerBlockItem(brown_clear_stained_glass);
    registry.registerBlockItem(green_clear_stained_glass);
    registry.registerBlockItem(red_clear_stained_glass);
    registry.registerBlockItem(black_clear_stained_glass);

    registry.registerBlockItem(mud_bricks);
    registry.registerBlockItem(mud_bricks_slab);
    registry.registerBlockItem(mud_bricks_stairs);

    registry.registerBlockItem(dried_clay);
    registry.registerBlockItem(dried_clay_slab);
    registry.registerBlockItem(dried_clay_stairs);

    registry.registerBlockItem(dried_clay_bricks);
    registry.registerBlockItem(dried_clay_bricks_slab);
    registry.registerBlockItem(dried_clay_bricks_stairs);
  }

  private static void registerGlass(BaseRegistryAdapter<Block> registry) {
    registry.register(new ClearGlassBlock(BlockProperties.GENERIC_GLASS_BLOCK), "clear_glass");

    for (ClearStainedGlassBlock.GlassColor glassColor : ClearStainedGlassBlock.GlassColor.values()) {
      String registryName = glassColor.name().toLowerCase() + "_clear_stained_glass";
      registry.register(new ClearStainedGlassBlock(BlockProperties.GENERIC_GLASS_BLOCK, glassColor), registryName);
    }
  }

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(clear_glass, (layer) -> layer == RenderType.getCutout());

    RenderTypeLookup.setRenderLayer(white_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(orange_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(magenta_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(light_blue_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(yellow_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(lime_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(pink_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(gray_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(light_gray_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(cyan_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(purple_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(blue_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(brown_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(green_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(red_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(black_clear_stained_glass, (layer) -> layer == RenderType.getTranslucent());
  }

  private DecorativeBlocks() {}
}
