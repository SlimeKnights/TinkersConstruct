package slimeknights.tconstruct.shared;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemConvertible;
import slimeknights.tconstruct.common.ClientEventBase;
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
}
