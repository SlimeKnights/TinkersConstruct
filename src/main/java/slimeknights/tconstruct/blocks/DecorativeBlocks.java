package slimeknights.tconstruct.blocks;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.BlockDeferredRegister;
import slimeknights.tconstruct.library.registration.object.BlockItemObject;
import slimeknights.tconstruct.library.registration.object.BuildingBlockObject;
import slimeknights.tconstruct.library.registration.object.EnumObject;
import slimeknights.tconstruct.shared.block.ClearGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DecorativeBlocks {

  private static final Item.Properties generalProps = new Item.Properties().group(TinkerRegistry.tabGeneral);
  private static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(TConstruct.modID);

  public static void init() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    BLOCKS.register(modEventBus);
  }

  /* Decorative Blocks */
  public static final BlockItemObject<ClearGlassBlock> clear_glass = BLOCKS.register("clear_glass", () -> new ClearGlassBlock(BlockProperties.GENERIC_GLASS_BLOCK), generalProps);
  public static final EnumObject<ClearStainedGlassBlock.GlassColor,ClearStainedGlassBlock> clear_stained_glass = BLOCKS.registerEnum(ClearStainedGlassBlock.GlassColor.values(), "clear_stained_glass", (color) -> new ClearStainedGlassBlock(BlockProperties.GENERIC_GLASS_BLOCK, color), generalProps);

  public static final BuildingBlockObject mud_bricks = BLOCKS.registerBuilding("mud_bricks", BlockProperties.MUD_BRICKS, generalProps);
  public static final BuildingBlockObject dried_clay = BLOCKS.registerBuilding("dried_clay", BlockProperties.DRIED_CLAY, generalProps);
  public static final BuildingBlockObject dried_clay_bricks = BLOCKS.registerBuilding("dried_clay_bricks", BlockProperties.DRIED_CLAY_BRICKS, generalProps);

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(clear_glass.get(), (layer) -> layer == RenderType.getCutout());
    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      RenderTypeLookup.setRenderLayer(clear_stained_glass.get(color), (layer) -> layer == RenderType.getTranslucent());
    }
  }

  @SubscribeEvent
  static void registerColorHandlers(ColorHandlerEvent.Item event) {
    BlockColors blockColors = event.getBlockColors();
    ItemColors itemColors = event.getItemColors();

    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      blockColors.register((state, reader, pos, index) -> {
        if (state != null && state.getBlock() instanceof ClearStainedGlassBlock) {
          ClearStainedGlassBlock block = (ClearStainedGlassBlock) state.getBlock();
          return block.getGlassColor().getColor();
        }

        MaterialColor materialColor = state.getMaterialColor(reader, pos);
        return materialColor != null ? materialColor.colorValue : -1;
      }, clear_stained_glass.get(color));
      itemColors.register((stack, index) -> {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
        return blockColors.getColor(state, null,  null, index);
      }, clear_stained_glass.get(color));
    }
  }
}
