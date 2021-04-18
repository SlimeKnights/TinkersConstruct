package slimeknights.tconstruct.shared;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.FontResourceManager;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;

public class CommonsClientEvents extends ClientEventBase implements ClientModInitializer {

  @Override
  public void onInitializeClient() {

    // glass
    RenderTypeLookup.setRenderLayer(TinkerCommons.clearGlass.get(), RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(TinkerCommons.clearGlassPane.get(), RenderType.getCutout());
    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      RenderTypeLookup.setRenderLayer(TinkerCommons.clearStainedGlass.get(color), RenderType.getTranslucent());
      RenderTypeLookup.setRenderLayer(TinkerCommons.clearStainedGlassPane.get(color), RenderType.getTranslucent());
    }
    RenderTypeLookup.setRenderLayer(TinkerCommons.soulGlass.get(), RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(TinkerCommons.soulGlassPane.get(), RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(TinkerMaterials.soulsteel.get(), RenderType.getTranslucent());

    FontRenderer unicode = unicodeFontRender();
    TinkerBook.MATERIALS_AND_YOU.fontRenderer = unicode;
    TinkerBook.TINKERS_GADGETRY.fontRenderer = unicode;
    TinkerBook.PUNY_SMELTING.fontRenderer = unicode;
    TinkerBook.MIGHTY_SMELTING.fontRenderer = unicode;
  }

  @SubscribeEvent
  static void registerColorHandlers(ColorHandlerEvent.Item event) {
    // colors apply a constant tint to make models easier
    ColorProviderRegistry<Block, BlockColorProvider> blockColors = ColorProviderRegistry.BLOCK;
    ColorProviderRegistry<ItemConvertible, ItemColorProvider> itemColors = ColorProviderRegistry.ITEM;
    for (GlassColor color : GlassColor.values()) {
      Block block = TinkerCommons.clearStainedGlass.get(color);
      Block pane = TinkerCommons.clearStainedGlassPane.get(color);
      blockColors.register((state, reader, pos, index) -> color.getColor(), block, pane);
      registerBlockItemColorAlias(blockColors, itemColors, block);
      registerBlockItemColorAlias(blockColors, itemColors, pane);
    }

    BlockRenderLayerMapImpl.INSTANCE.putBlock(TinkerCommons.glow, RenderLayer.getTranslucent());

    // glass
    BlockRenderLayerMapImpl.INSTANCE.putBlock(TinkerCommons.clearGlass.get(), RenderLayer.getCutout());
    BlockRenderLayerMapImpl.INSTANCE.putBlock(TinkerCommons.clearGlassPane.get(), RenderLayer.getCutout());
    for (GlassColor color : GlassColor.values()) {
      BlockRenderLayerMapImpl.INSTANCE.putBlock(TinkerCommons.clearStainedGlass.get(color), RenderLayer.getTranslucent());
      BlockRenderLayerMapImpl.INSTANCE.putBlock(TinkerCommons.clearStainedGlassPane.get(color), RenderLayer.getTranslucent());
    }
    BlockRenderLayerMapImpl.INSTANCE.putBlock(TinkerCommons.soulGlass.get(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putBlock(TinkerCommons.soulGlassPane.get(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putBlock(TinkerMaterials.soulsteel.get(), RenderLayer.getTranslucent());

    ArmorRenderingRegistry.registerSimpleTexture(new Identifier("tconstruct:piggyback"), TinkerGadgets.piggyBackpack.get());
  }

  private static FontRenderer unicodeRenderer;

  public static FontRenderer unicodeFontRender() {
    if (unicodeRenderer == null)
      unicodeRenderer = new FontRenderer(rl -> {
        FontResourceManager resourceManager = Minecraft.getInstance().fontResourceMananger;
        return resourceManager.field_238546_d_.get(Minecraft.UNIFORM_FONT_RENDERER_NAME);
      });

    return unicodeRenderer;
  }
}
