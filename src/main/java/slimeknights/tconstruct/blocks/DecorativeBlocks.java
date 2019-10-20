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
import slimeknights.tconstruct.shared.block.ClearGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

@ObjectHolder(TConstruct.modID)
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
    BaseRegistryAdapter<Block> registry = new BaseRegistryAdapter<>(event.getRegistry());

    registerGlass(registry);

    registerSlabsAndStairs(registry, "mud_bricks", BlockProperties.MUD_BRICKS);
    registerSlabsAndStairs(registry, "dried_clay", BlockProperties.DRIED_CLAY);
    registerSlabsAndStairs(registry, "dried_clay_bricks", BlockProperties.DRIED_CLAY_BRICKS);
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

  static void registerSlabsAndStairs(BaseRegistryAdapter<Block> registry, String name, Block.Properties properties) {
    Block baseBlock = new Block(properties);
    registry.register(baseBlock, name);
    registry.register(new SlabBlock(properties), name + "_slab");
    registry.register(new StairsBlock(baseBlock::getDefaultState, properties), name + "_stairs");
  }

  private static void registerGlass(BaseRegistryAdapter<Block> registry) {
    registry.register(new ClearGlassBlock(), "clear_glass");

    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.WHITE), "white_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.ORANGE), "orange_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.MAGENTA), "magenta_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.LIGHT_BLUE), "light_blue_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.YELLOW), "yellow_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.LIME), "lime_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.PINK), "pink_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.GRAY), "gray_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.LIGHT_GRAY), "light_gray_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.CYAN), "cyan_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.PURPLE), "purple_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.BLUE), "blue_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.BROWN), "brown_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.GREEN), "green_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.RED), "red_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.BLACK), "black_clear_stained_glass");
  }

  private DecorativeBlocks() {}
}
