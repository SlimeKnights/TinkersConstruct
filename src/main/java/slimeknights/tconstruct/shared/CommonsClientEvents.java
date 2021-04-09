package slimeknights.tconstruct.shared;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.item.ItemConvertible;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;

public class CommonsClientEvents extends ClientEventBase implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
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
  }

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderLayers.setRenderLayer(TinkerCommons.glow.get(), RenderLayer.getTranslucent());

    // glass
    RenderLayers.setRenderLayer(TinkerCommons.clearGlass.get(), RenderLayer.getCutout());
    RenderLayers.setRenderLayer(TinkerCommons.clearGlassPane.get(), RenderLayer.getCutout());
    for (GlassColor color : GlassColor.values()) {
      RenderLayers.setRenderLayer(TinkerCommons.clearStainedGlass.get(color), RenderLayer.getTranslucent());
      RenderLayers.setRenderLayer(TinkerCommons.clearStainedGlassPane.get(color), RenderLayer.getTranslucent());
    }
    RenderLayers.setRenderLayer(TinkerCommons.soulGlass.get(), RenderLayer.getTranslucent());
    RenderLayers.setRenderLayer(TinkerCommons.soulGlassPane.get(), RenderLayer.getTranslucent());
    RenderLayers.setRenderLayer(TinkerMaterials.soulsteel.get(), RenderLayer.getTranslucent());
  }
}
