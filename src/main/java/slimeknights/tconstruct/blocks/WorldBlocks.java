package slimeknights.tconstruct.blocks;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.BlockDeferredRegister;
import slimeknights.tconstruct.library.registration.ItemDeferredRegister;
import slimeknights.tconstruct.library.registration.object.BlockItemObject;
import slimeknights.tconstruct.library.registration.object.EnumObject;
import slimeknights.tconstruct.shared.block.CongealedSlimeBlock;
import slimeknights.tconstruct.shared.block.OverlayBlock;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.world.block.SlimeDirtBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeLeavesBlock;
import slimeknights.tconstruct.world.block.SlimeSaplingBlock;
import slimeknights.tconstruct.world.block.SlimeTallGrassBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.client.SlimeColorizer;
import slimeknights.tconstruct.world.worldgen.trees.SlimeTree;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorldBlocks {

  private static final Item.Properties worldProps = new Item.Properties().group(TinkerRegistry.tabWorld);
  private static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(TConstruct.modID);

  public static void init() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    BLOCKS.register(modEventBus);
  }

  /* Ores */
  public static final BlockItemObject<OverlayBlock> cobalt_ore = BLOCKS.register("cobalt_ore", () -> new OverlayBlock(BlockProperties.ORE), worldProps);
  public static final BlockItemObject<OverlayBlock> ardite_ore = BLOCKS.register("ardite_ore", () -> new OverlayBlock(BlockProperties.ORE), worldProps);

  /* Slimestuff */
  public static final EnumObject<SlimeBlock.SlimeType, Block> slime;

  static {
    EnumObject<SlimeBlock.SlimeType, SlimeBlock> tinkerSlimeBlocks = BLOCKS.registerEnum(SlimeBlock.SlimeType.TINKER, "slime", (type) -> new SlimeBlock(BlockProperties.SLIME, (type == SlimeBlock.SlimeType.PINK)), worldProps);
    Map<SlimeBlock.SlimeType, Supplier<? extends Block>> map = new EnumMap(SlimeBlock.SlimeType.class);
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.TINKER) {
      map.put(slime, tinkerSlimeBlocks.getSupplier(slime));
    }
    map.put(SlimeBlock.SlimeType.GREEN, Blocks.SLIME_BLOCK.delegate);
    slime = new EnumObject<>(map);
  }

  public static final EnumObject<SlimeBlock.SlimeType, CongealedSlimeBlock> congealed_slime = BLOCKS.registerEnum(SlimeBlock.SlimeType.values(), "congealed_slime", (type) -> new CongealedSlimeBlock(BlockProperties.SLIME, (type == SlimeBlock.SlimeType.PINK)), worldProps);

  public static final EnumObject<SlimeDirtBlock.SlimeDirtType, SlimeDirtBlock> slime_dirt = BLOCKS.registerEnum(SlimeDirtBlock.SlimeDirtType.values(), "slime_dirt", (type) -> new SlimeDirtBlock(BlockProperties.SLIME_DIRT), worldProps);

  public static final EnumObject<SlimeGrassBlock.FoliageType, SlimeGrassBlock> vanilla_slime_grass = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "vanilla_slime_grass", (type) -> new SlimeGrassBlock(BlockProperties.SLIME_GRASS, type), worldProps);
  public static final EnumObject<SlimeGrassBlock.FoliageType, SlimeGrassBlock> green_slime_grass = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "green_slime_grass", (type) -> new SlimeGrassBlock(BlockProperties.SLIME_GRASS, type), worldProps);
  public static final EnumObject<SlimeGrassBlock.FoliageType, SlimeGrassBlock> blue_slime_grass = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "blue_slime_grass", (type) -> new SlimeGrassBlock(BlockProperties.SLIME_GRASS, type), worldProps);
  public static final EnumObject<SlimeGrassBlock.FoliageType, SlimeGrassBlock> purple_slime_grass = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "purple_slime_grass", (type) -> new SlimeGrassBlock(BlockProperties.SLIME_GRASS, type), worldProps);
  public static final EnumObject<SlimeGrassBlock.FoliageType, SlimeGrassBlock> magma_slime_grass = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "magma_slime_grass", (type) -> new SlimeGrassBlock(BlockProperties.SLIME_GRASS, type), worldProps);

  public static final EnumObject<SlimeGrassBlock.FoliageType, SlimeLeavesBlock> slime_leaves = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "slime_leaves", (type) -> new SlimeLeavesBlock(BlockProperties.SLIME_LEAVES, type), worldProps);

  public static final EnumObject<SlimeGrassBlock.FoliageType, SlimeTallGrassBlock> slime_fern = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "slime_fern", (type) -> new SlimeTallGrassBlock(BlockProperties.TALL_GRASS, type, SlimeTallGrassBlock.SlimePlantType.FERN), worldProps);

  public static final EnumObject<SlimeGrassBlock.FoliageType, SlimeTallGrassBlock> slime_tall_grass = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "slime_tall_grass", (type) -> new SlimeTallGrassBlock(BlockProperties.TALL_GRASS, type, SlimeTallGrassBlock.SlimePlantType.TALL_GRASS), worldProps);

  public static final EnumObject<SlimeGrassBlock.FoliageType,SlimeSaplingBlock> slime_sapling = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "slime_sapling", (type) -> new SlimeSaplingBlock(new SlimeTree(type, false), BlockProperties.SAPLING), worldProps);

  public static final BlockItemObject<SlimeVineBlock> purple_slime_vine = BLOCKS.register("purple_slime_vine", () -> new SlimeVineBlock(BlockProperties.VINE, SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.START), worldProps);
  public static final BlockItemObject<SlimeVineBlock> purple_slime_vine_middle = BLOCKS.register("purple_slime_vine_middle", () -> new SlimeVineBlock(BlockProperties.VINE, SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.MIDDLE), worldProps);
  public static final BlockItemObject<SlimeVineBlock> purple_slime_vine_end = BLOCKS.register("purple_slime_vine_end", () -> new SlimeVineBlock(BlockProperties.VINE, SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.END), worldProps);
  public static final BlockItemObject<SlimeVineBlock> blue_slime_vine = BLOCKS.register("blue_slime_vine", () -> new SlimeVineBlock(BlockProperties.VINE, SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.START), worldProps);
  public static final BlockItemObject<SlimeVineBlock> blue_slime_vine_middle = BLOCKS.register("blue_slime_vine_middle", () -> new SlimeVineBlock(BlockProperties.VINE, SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.MIDDLE), worldProps);
  public static final BlockItemObject<SlimeVineBlock> blue_slime_vine_end = BLOCKS.register("blue_slime_vine_end", () -> new SlimeVineBlock(BlockProperties.VINE, SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.END), worldProps);

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(cobalt_ore.get(), (layer) -> layer == RenderType.getCutoutMipped());
    RenderTypeLookup.setRenderLayer(ardite_ore.get(), (layer) -> layer == RenderType.getCutoutMipped());

    for (SlimeGrassBlock.FoliageType type : SlimeGrassBlock.FoliageType.values()) {
      RenderTypeLookup.setRenderLayer(slime_leaves.get(type), (layer) -> layer == RenderType.getCutoutMipped());
      RenderTypeLookup.setRenderLayer(vanilla_slime_grass.get(type), (layer) -> layer == RenderType.getCutoutMipped());
      RenderTypeLookup.setRenderLayer(green_slime_grass.get(type), (layer) -> layer == RenderType.getCutoutMipped());
      RenderTypeLookup.setRenderLayer(blue_slime_grass.get(type), (layer) -> layer == RenderType.getCutoutMipped());
      RenderTypeLookup.setRenderLayer(purple_slime_grass.get(type), (layer) -> layer == RenderType.getCutoutMipped());
      RenderTypeLookup.setRenderLayer(magma_slime_grass.get(type), (layer) -> layer == RenderType.getCutoutMipped());
      RenderTypeLookup.setRenderLayer(slime_fern.get(type), (layer) -> layer == RenderType.getCutout());
      RenderTypeLookup.setRenderLayer(slime_tall_grass.get(type), (layer) -> layer == RenderType.getCutout());
      RenderTypeLookup.setRenderLayer(slime_sapling.get(type), (layer) -> layer == RenderType.getCutout());
    }

    RenderTypeLookup.setRenderLayer(purple_slime_vine.get(), (layer) -> layer == RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(purple_slime_vine_middle.get(), (layer) -> layer == RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(purple_slime_vine_end.get(), (layer) -> layer == RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(blue_slime_vine.get(), (layer) -> layer == RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(blue_slime_vine_middle.get(), (layer) -> layer == RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(blue_slime_vine_end.get(), (layer) -> layer == RenderType.getCutout());
  }

  @SubscribeEvent
  static void registerColorHandlers(ColorHandlerEvent.Item event) {
    BlockColors blockColors = event.getBlockColors();
    ItemColors itemColors = event.getItemColors();

    for (SlimeGrassBlock.FoliageType type : SlimeGrassBlock.FoliageType.values()) {
      blockColors.register((state, reader, pos, index) -> getSlimeColorByPos(pos, type, null),
        WorldBlocks.vanilla_slime_grass.get(type), WorldBlocks.green_slime_grass.get(type), WorldBlocks.blue_slime_grass.get(type),
        WorldBlocks.purple_slime_grass.get(type), WorldBlocks.magma_slime_grass.get(type));

      itemColors.register((stack, index) -> {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
        return blockColors.getColor(state, null, null, index);
      }, vanilla_slime_grass.get(type), green_slime_grass.get(type), blue_slime_grass.get(type), purple_slime_grass.get(type), magma_slime_grass.get(type));

      blockColors.register((state, reader, pos, index) -> getSlimeColorByPos(pos, type, SlimeColorizer.LOOP_OFFSET),
        slime_leaves.get(type));

      itemColors.register((stack, index) -> {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
        return blockColors.getColor(state, null, null, index);
      }, slime_leaves.get(type));

      blockColors.register((state, reader, pos, index) -> getSlimeColorByPos(pos, type, null),
        slime_fern.get(type), slime_tall_grass.get(type));

      itemColors.register((stack, index) -> {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
        return blockColors.getColor(state, null, null, index);
      }, slime_fern.get(type), slime_tall_grass.get(type));
    }

    blockColors.register((state, reader, pos, index) -> {
        if (state.getBlock() instanceof SlimeVineBlock) {
          SlimeVineBlock block = (SlimeVineBlock) state.getBlock();
          return getSlimeColorByPos(pos, block.getFoliageType(), SlimeColorizer.LOOP_OFFSET);
        }

        MaterialColor materialColor = state.getMaterialColor(reader, pos);
        return materialColor != null ? materialColor.colorValue : -1;
      },
      blue_slime_vine.get(), blue_slime_vine_middle.get(), blue_slime_vine_end.get(),
      purple_slime_vine.get(), purple_slime_vine_middle.get(), purple_slime_vine_end.get()
    );

    itemColors.register((stack, index) -> {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
        return blockColors.getColor(state, null, null, index);
      },
      blue_slime_vine, blue_slime_vine_middle, blue_slime_vine_end,
      purple_slime_vine, purple_slime_vine_middle, purple_slime_vine_end
    );
  }

  private static int getSlimeColorByPos(@Nullable BlockPos pos, SlimeGrassBlock.FoliageType type, @Nullable BlockPos add) {
    if (pos == null) {
      return SlimeColorizer.getColorStatic(type);
    }
    if (add != null) {
      pos = pos.add(add);
    }

    return SlimeColorizer.getColorForPos(pos, type);
  }
}
