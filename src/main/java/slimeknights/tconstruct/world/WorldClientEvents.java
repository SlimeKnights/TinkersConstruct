package slimeknights.tconstruct.world;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemConvertible;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;
import slimeknights.tconstruct.world.client.SlimeColorReloadListener;
import slimeknights.tconstruct.world.client.SlimeColorizer;
import slimeknights.tconstruct.world.client.TinkerSlimeRenderer;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class WorldClientEvents extends ClientEventBase implements ClientModInitializer {
  /**
   * Called by TinkerClient to add the resource listeners, runs during constructor
   */
  public static void addResourceListener(ReloadableResourceManager manager) {
    for (FoliageType type : FoliageType.values()) {
      manager.registerListener(new SlimeColorReloadListener(type));
    }
  }

  @Override
  public void onInitializeClient() {
    EntityRendererRegistry.INSTANCE.register(TinkerWorld.skySlimeEntity, (entityRenderDispatcher, context) -> new TinkerSlimeRenderer(entityRenderDispatcher, Util.getResource("textures/entity/blue_slime.png")));

    // render types - ores
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerWorld.cobaltOre.get(), RenderLayer.getCutoutMipped());

    // render types - slime plants
    for (FoliageType type : FoliageType.values()) {

      BlockRenderLayerMap.INSTANCE.putBlock(TinkerWorld.slimeLeaves.get(type), RenderLayer.getCutoutMipped());
      BlockRenderLayerMap.INSTANCE.putBlock(TinkerWorld.vanillaSlimeGrass.get(type), RenderLayer.getCutoutMipped());
      BlockRenderLayerMap.INSTANCE.putBlock(TinkerWorld.earthSlimeGrass.get(type), RenderLayer.getCutoutMipped());
      BlockRenderLayerMap.INSTANCE.putBlock(TinkerWorld.skySlimeGrass.get(type), RenderLayer.getCutoutMipped());
      BlockRenderLayerMap.INSTANCE.putBlock(TinkerWorld.enderSlimeGrass.get(type), RenderLayer.getCutoutMipped());
      BlockRenderLayerMap.INSTANCE.putBlock(TinkerWorld.ichorSlimeGrass.get(type), RenderLayer.getCutoutMipped());
      BlockRenderLayerMap.INSTANCE.putBlock(TinkerWorld.slimeFern.get(type), RenderLayer.getCutout());
      BlockRenderLayerMap.INSTANCE.putBlock(TinkerWorld.slimeTallGrass.get(type), RenderLayer.getCutout());
      BlockRenderLayerMap.INSTANCE.putBlock(TinkerWorld.slimeSapling.get(type), RenderLayer.getCutout());
    }
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerWorld.enderSlimeVine.get(), RenderLayer.getCutout());
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerWorld.skySlimeVine.get(), RenderLayer.getCutout());

    // render types - slime blocks
    for (SlimeType type : SlimeType.TINKER) {
      BlockRenderLayerMap.INSTANCE.putBlock(TinkerWorld.slime.get(type), RenderLayer.getTranslucent());
    }

    ColorProviderRegistry<Block, BlockColorProvider> blockColors = ColorProviderRegistry.BLOCK;
    ColorProviderRegistry<ItemConvertible, ItemColorProvider> itemColors = ColorProviderRegistry.ITEM;

    // slime plants - blocks
    for (FoliageType type : FoliageType.values()) {
      blockColors.register(
        (state, reader, pos, index) -> getSlimeColorByPos(pos, type, null),
        TinkerWorld.vanillaSlimeGrass.get(type), TinkerWorld.earthSlimeGrass.get(type), TinkerWorld.skySlimeGrass.get(type),
        TinkerWorld.enderSlimeGrass.get(type), TinkerWorld.ichorSlimeGrass.get(type));
      blockColors.register(
        (state, reader, pos, index) -> getSlimeColorByPos(pos, type, SlimeColorizer.LOOP_OFFSET),
        TinkerWorld.slimeLeaves.get(type));
      blockColors.register(
        (state, reader, pos, index) -> getSlimeColorByPos(pos, type, null),
        TinkerWorld.slimeFern.get(type), TinkerWorld.slimeTallGrass.get(type));
    }

    // vines
    blockColors.register(
      (state, reader, pos, index) -> getSlimeColorByPos(pos, FoliageType.SKY, SlimeColorizer.LOOP_OFFSET),
      TinkerWorld.skySlimeVine.get());
    blockColors.register(
      (state, reader, pos, index) -> getSlimeColorByPos(pos, FoliageType.ENDER, SlimeColorizer.LOOP_OFFSET),
      TinkerWorld.enderSlimeVine.get());

    // slime grass items
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.vanillaSlimeGrass);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.earthSlimeGrass);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.skySlimeGrass);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.enderSlimeGrass);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.ichorSlimeGrass);
    // plant items
    registerBlockItemColorAlias(blockColors, itemColors, (Supplier<? extends Block>) TinkerWorld.slimeLeaves);
    registerBlockItemColorAlias(blockColors, itemColors, (Supplier<? extends Block>) TinkerWorld.slimeFern);
    registerBlockItemColorAlias(blockColors, itemColors, (Supplier<? extends Block>) TinkerWorld.slimeTallGrass);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.skySlimeVine);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.enderSlimeVine);
  }

  /**
   * Block colors for a slime type
   * @param pos   Block position
   * @param type  Slime foilage color
   * @param add   Offset position
   * @return  Color for the given position, or the default if position is null
   */
  private static int getSlimeColorByPos(@Nullable BlockPos pos, FoliageType type, @Nullable BlockPos add) {
    if (pos == null) {
      return SlimeColorizer.getColorStatic(type);
    }
    if (add != null) {
      pos = pos.add(add);
    }

    return SlimeColorizer.getColorForPos(pos, type);
  }
}
