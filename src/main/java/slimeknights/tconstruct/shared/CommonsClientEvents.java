package slimeknights.tconstruct.shared;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;

public class CommonsClientEvents extends ClientEventBase implements ClientModInitializer {

  @Override
  public void onInitializeClient() {

    // glass
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.clearGlass.get(), RenderLayer.getCutout());
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.clearGlass.get(), RenderLayer.getCutout());
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.clearGlassPane.get(), RenderLayer.getCutout());
    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.clearStainedGlass.get(color), RenderLayer.getTranslucent());
      BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.clearStainedGlassPane.get(color), RenderLayer.getTranslucent());
    }
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.soulGlass.get(), RenderLayer.getTranslucent());
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.soulGlassPane.get(), RenderLayer.getTranslucent());
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerMaterials.soulsteel.get(), RenderLayer.getTranslucent());

    TextRenderer unicode = unicodeFontRender();
    TinkerBook.MATERIALS_AND_YOU.fontRenderer = unicode;
    TinkerBook.TINKERS_GADGETRY.fontRenderer = unicode;
    TinkerBook.PUNY_SMELTING.fontRenderer = unicode;
    TinkerBook.MIGHTY_SMELTING.fontRenderer = unicode;
  }

  private static TextRenderer unicodeRenderer;

  public static TextRenderer unicodeFontRender() {
    if (unicodeRenderer == null)
      unicodeRenderer = new TextRenderer(rl -> {
        FontManager resourceManager = MinecraftClient.getInstance().fontManager;
        return resourceManager.fontStorages.get(MinecraftClient.UNICODE_FONT_ID);
      });

    return unicodeRenderer;
  }
}
