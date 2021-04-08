package slimeknights.tconstruct.world;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.particle.SlimeParticle;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;
import slimeknights.tconstruct.world.client.SlimeColorReloadListener;
import slimeknights.tconstruct.world.client.SlimeColorizer;
import slimeknights.tconstruct.world.client.TinkerSlimeRenderer;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@EventBusSubscriber(modid=TConstruct.modID, value=Dist.CLIENT, bus=Bus.MOD)
public class WorldClientEvents extends ClientEventBase {
  /**
   * Called by TinkerClient to add the resource listeners, runs during constructor
   */
  public static void addResourceListener(ReloadableResourceManager manager) {
    for (FoliageType type : FoliageType.values()) {
      manager.registerListener(new SlimeColorReloadListener(type));
    }
  }

  @SubscribeEvent
  static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
    MinecraftClient.getInstance().particleManager.registerFactory(TinkerWorld.slimeParticle.get(), new SlimeParticle.Factory());
  }

  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(TinkerWorld.skySlimeEntity.get(), TinkerSlimeRenderer.BLUE_SLIME_FACTORY);

    // render types - ores
    RenderLayers.setRenderLayer(TinkerWorld.cobaltOre.get(), RenderLayer.getCutoutMipped());

    // render types - slime plants
    for (FoliageType type : FoliageType.values()) {
      RenderLayers.setRenderLayer(TinkerWorld.slimeLeaves.get(type), RenderLayer.getCutoutMipped());
      RenderLayers.setRenderLayer(TinkerWorld.vanillaSlimeGrass.get(type), RenderLayer.getCutoutMipped());
      RenderLayers.setRenderLayer(TinkerWorld.earthSlimeGrass.get(type), RenderLayer.getCutoutMipped());
      RenderLayers.setRenderLayer(TinkerWorld.skySlimeGrass.get(type), RenderLayer.getCutoutMipped());
      RenderLayers.setRenderLayer(TinkerWorld.enderSlimeGrass.get(type), RenderLayer.getCutoutMipped());
      RenderLayers.setRenderLayer(TinkerWorld.ichorSlimeGrass.get(type), RenderLayer.getCutoutMipped());
      RenderLayers.setRenderLayer(TinkerWorld.slimeFern.get(type), RenderLayer.getCutout());
      RenderLayers.setRenderLayer(TinkerWorld.slimeTallGrass.get(type), RenderLayer.getCutout());
      RenderLayers.setRenderLayer(TinkerWorld.slimeSapling.get(type), RenderLayer.getCutout());
    }
    RenderLayers.setRenderLayer(TinkerWorld.enderSlimeVine.get(), RenderLayer.getCutout());
    RenderLayers.setRenderLayer(TinkerWorld.skySlimeVine.get(), RenderLayer.getCutout());

    // render types - slime blocks
    for (SlimeType type : SlimeType.TINKER) {
      RenderLayers.setRenderLayer(TinkerWorld.slime.get(type), RenderLayer.getTranslucent());
    }
  }

  @SubscribeEvent
  static void registerBlockColorHandlers(ColorHandlerEvent.Block event) {
    BlockColors blockColors = event.getBlockColors();

    // slime plants - blocks
    for (FoliageType type : FoliageType.values()) {
      blockColors.registerColorProvider(
        (state, reader, pos, index) -> getSlimeColorByPos(pos, type, null),
        TinkerWorld.vanillaSlimeGrass.get(type), TinkerWorld.earthSlimeGrass.get(type), TinkerWorld.skySlimeGrass.get(type),
        TinkerWorld.enderSlimeGrass.get(type), TinkerWorld.ichorSlimeGrass.get(type));
      blockColors.registerColorProvider(
        (state, reader, pos, index) -> getSlimeColorByPos(pos, type, SlimeColorizer.LOOP_OFFSET),
        TinkerWorld.slimeLeaves.get(type));
      blockColors.registerColorProvider(
        (state, reader, pos, index) -> getSlimeColorByPos(pos, type, null),
        TinkerWorld.slimeFern.get(type), TinkerWorld.slimeTallGrass.get(type));
    }

    // vines
    blockColors.registerColorProvider(
      (state, reader, pos, index) -> getSlimeColorByPos(pos, FoliageType.SKY, SlimeColorizer.LOOP_OFFSET),
      TinkerWorld.skySlimeVine.get());
    blockColors.registerColorProvider(
      (state, reader, pos, index) -> getSlimeColorByPos(pos, FoliageType.ENDER, SlimeColorizer.LOOP_OFFSET),
      TinkerWorld.enderSlimeVine.get());
  }

  @SubscribeEvent
  static void registerItemColorHandlers(ColorHandlerEvent.Item event) {
    BlockColors blockColors = event.getBlockColors();
    ItemColors itemColors = event.getItemColors();
    // slime grass items
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.vanillaSlimeGrass);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.earthSlimeGrass);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.skySlimeGrass);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.enderSlimeGrass);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.ichorSlimeGrass);
    // plant items
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.slimeLeaves);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.slimeFern);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.slimeTallGrass);
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
