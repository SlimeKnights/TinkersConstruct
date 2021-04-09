package slimeknights.tconstruct.shared;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;

import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class CommonsClientEvents extends ClientEventBase {
  @Override
  public void onInitializeClient() {
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
  }

  static void registerColorHandlers(ColorHandlerEvent.Item event) {
    // colors apply a constant tint to make models easier
    BlockColors blockColors = event.getBlockColors();
    ItemColors itemColors = event.getItemColors();
    for (GlassColor color : GlassColor.values()) {
      Block block = TinkerCommons.clearStainedGlass.get(color);
      Block pane = TinkerCommons.clearStainedGlassPane.get(color);
      blockColors.registerColorProvider((state, reader, pos, index) -> color.getColor(), block, pane);
      registerBlockItemColorAlias(blockColors, itemColors, block);
      registerBlockItemColorAlias(blockColors, itemColors, pane);
    }
  }
}
