package slimeknights.tconstruct.shared;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.FontResourceManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;

@EventBusSubscriber(modid = TConstruct.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class CommonsClientEvents extends ClientEventBase {

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(TinkerCommons.glow.get(), RenderType.getTranslucent());

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
    RenderTypeLookup.setRenderLayer(TinkerMaterials.slimesteel.get(), RenderType.getTranslucent());

    FontRenderer unicode = unicodeFontRender();
    TinkerBook.MATERIALS_AND_YOU.fontRenderer = unicode;
    TinkerBook.TINKERS_GADGETRY.fontRenderer = unicode;
    TinkerBook.PUNY_SMELTING.fontRenderer = unicode;
    TinkerBook.MIGHTY_SMELTING.fontRenderer = unicode;
    TinkerBook.FANTASTIC_FOUNDRY.fontRenderer = unicode;
    TinkerBook.ENCYCLOPEDIA.fontRenderer = unicode;
  }

  @SubscribeEvent
  static void registerColorHandlers(ColorHandlerEvent.Item event) {
    // colors apply a constant tint to make models easier
    BlockColors blockColors = event.getBlockColors();
    ItemColors itemColors = event.getItemColors();
    for (GlassColor color : GlassColor.values()) {
      Block block = TinkerCommons.clearStainedGlass.get(color);
      Block pane = TinkerCommons.clearStainedGlassPane.get(color);
      blockColors.register((state, reader, pos, index) -> color.getColor(), block, pane);
      registerBlockItemColorAlias(blockColors, itemColors, block);
      registerBlockItemColorAlias(blockColors, itemColors, pane);
    }
  }

  private static FontRenderer unicodeRenderer;

  /** Gets the unicode font renderer */
  public static FontRenderer unicodeFontRender() {
    if (unicodeRenderer == null)
      unicodeRenderer = new FontRenderer(rl -> {
        FontResourceManager resourceManager = Minecraft.getInstance().fontResourceMananger;
        return resourceManager.field_238546_d_.get(Minecraft.UNIFORM_FONT_RENDERER_NAME);
      });

    return unicodeRenderer;
  }
}
