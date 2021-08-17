package slimeknights.tconstruct.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.item.Items;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
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
import slimeknights.tconstruct.world.client.HeadWithOverlayModel;
import slimeknights.tconstruct.world.client.SlimeColorReloadListener;
import slimeknights.tconstruct.world.client.SlimeColorizer;
import slimeknights.tconstruct.world.client.TerracubeRenderer;
import slimeknights.tconstruct.world.client.TinkerSlimeRenderer;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
@EventBusSubscriber(modid=TConstruct.MOD_ID, value=Dist.CLIENT, bus=Bus.MOD)
public class WorldClientEvents extends ClientEventBase {
  /**
   * Called by TinkerClient to add the resource listeners, runs during constructor
   */
  public static void addResourceListener(IReloadableResourceManager manager) {
    for (SlimeType type : SlimeType.values()) {
      manager.addReloadListener(new SlimeColorReloadListener(type));
    }
  }

  @SubscribeEvent
  static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
    Minecraft.getInstance().particles.registerFactory(TinkerWorld.skySlimeParticle.get(), new SlimeParticle.Factory(SlimeType.SKY));
    Minecraft.getInstance().particles.registerFactory(TinkerWorld.enderSlimeParticle.get(), new SlimeParticle.Factory(SlimeType.ENDER));
    Minecraft.getInstance().particles.registerFactory(TinkerWorld.terracubeParticle.get(), new SlimeParticle.Factory(Items.CLAY_BALL));
  }

  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(TinkerWorld.earthSlimeEntity.get(), SlimeRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(TinkerWorld.skySlimeEntity.get(), TinkerSlimeRenderer.SKY_SLIME_FACTORY);
    RenderingRegistry.registerEntityRenderingHandler(TinkerWorld.enderSlimeEntity.get(), TinkerSlimeRenderer.ENDER_SLIME_FACTORY);
    RenderingRegistry.registerEntityRenderingHandler(TinkerWorld.terracubeEntity.get(), TerracubeRenderer.TERRACUBE_RENDERER);

    RenderType cutout = RenderType.getCutout();
    RenderType cutoutMipped = RenderType.getCutoutMipped();

    // render types - slime plants
    for (SlimeType type : SlimeType.values()) {
      if (type != SlimeType.BLOOD) {
        RenderTypeLookup.setRenderLayer(TinkerWorld.slimeLeaves.get(type), cutoutMipped);
      }
      RenderTypeLookup.setRenderLayer(TinkerWorld.vanillaSlimeGrass.get(type), cutoutMipped);
      RenderTypeLookup.setRenderLayer(TinkerWorld.earthSlimeGrass.get(type), cutoutMipped);
      RenderTypeLookup.setRenderLayer(TinkerWorld.skySlimeGrass.get(type), cutoutMipped);
      RenderTypeLookup.setRenderLayer(TinkerWorld.enderSlimeGrass.get(type), cutoutMipped);
      RenderTypeLookup.setRenderLayer(TinkerWorld.ichorSlimeGrass.get(type), cutoutMipped);
      RenderTypeLookup.setRenderLayer(TinkerWorld.slimeFern.get(type), cutout);
      RenderTypeLookup.setRenderLayer(TinkerWorld.slimeTallGrass.get(type), cutout);
      RenderTypeLookup.setRenderLayer(TinkerWorld.slimeSapling.get(type), cutout);
    }
    RenderTypeLookup.setRenderLayer(TinkerWorld.enderSlimeVine.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerWorld.skySlimeVine.get(), cutout);

    // render types - slime blocks
    RenderType translucent = RenderType.getTranslucent();
    for (SlimeType type : SlimeType.TINKER) {
      RenderTypeLookup.setRenderLayer(TinkerWorld.slime.get(type), translucent);
    }

    // doors
    RenderTypeLookup.setRenderLayer(TinkerWorld.greenheart.getDoor(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerWorld.greenheart.getTrapdoor(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerWorld.skyroot.getDoor(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerWorld.skyroot.getTrapdoor(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerWorld.bloodshroom.getDoor(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerWorld.bloodshroom.getTrapdoor(), cutout);

    // skull rendering
    GenericHeadModel normalHead = new GenericHeadModel(0, 0, 64, 32);
    GenericHeadModel tinkersOverlayHead = new HeadWithOverlayModel(0, 0, 0, 16, 32, 32);
    SkullTileEntityRenderer.MODELS.put(TinkerHeadType.BLAZE, normalHead);
    SkullTileEntityRenderer.MODELS.put(TinkerHeadType.ENDERMAN, new GenericHeadModel(0, 0, 32, 16));
    SkullTileEntityRenderer.MODELS.put(TinkerHeadType.STRAY, tinkersOverlayHead);
    SkullTileEntityRenderer.SKINS.put(TinkerHeadType.BLAZE, new ResourceLocation("textures/entity/blaze.png"));
    SkullTileEntityRenderer.SKINS.put(TinkerHeadType.ENDERMAN, TConstruct.getResource("textures/entity/skull/enderman.png"));
    SkullTileEntityRenderer.SKINS.put(TinkerHeadType.STRAY, TConstruct.getResource("textures/entity/skull/stray.png"));
    // zombies
    SkullTileEntityRenderer.MODELS.put(TinkerHeadType.HUSK, new GenericHeadModel(0, 0, 64, 64));
    SkullTileEntityRenderer.MODELS.put(TinkerHeadType.DROWNED, tinkersOverlayHead);
    SkullTileEntityRenderer.SKINS.put(TinkerHeadType.HUSK, new ResourceLocation("textures/entity/zombie/husk.png"));
    SkullTileEntityRenderer.SKINS.put(TinkerHeadType.DROWNED, TConstruct.getResource("textures/entity/skull/drowned.png"));
    // spider
    GenericHeadModel spiderHead = new GenericHeadModel(32, 4, 64, 32);
    SkullTileEntityRenderer.MODELS.put(TinkerHeadType.SPIDER, spiderHead);
    SkullTileEntityRenderer.MODELS.put(TinkerHeadType.CAVE_SPIDER, spiderHead);
    SkullTileEntityRenderer.SKINS.put(TinkerHeadType.SPIDER, new ResourceLocation("textures/entity/spider/spider.png"));
    SkullTileEntityRenderer.SKINS.put(TinkerHeadType.CAVE_SPIDER, new ResourceLocation("textures/entity/spider/cave_spider.png"));
  }

  @SubscribeEvent
  static void registerBlockColorHandlers(ColorHandlerEvent.Block event) {
    BlockColors blockColors = event.getBlockColors();

    // slime plants - blocks
    for (SlimeType type : SlimeType.values()) {
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
      (state, reader, pos, index) -> getSlimeColorByPos(pos, SlimeType.SKY, SlimeColorizer.LOOP_OFFSET),
      TinkerWorld.skySlimeVine.get());
    blockColors.register(
      (state, reader, pos, index) -> getSlimeColorByPos(pos, SlimeType.ENDER, SlimeColorizer.LOOP_OFFSET),
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
  private static int getSlimeColorByPos(@Nullable BlockPos pos, SlimeType type, @Nullable BlockPos add) {
    if (pos == null) {
      return SlimeColorizer.getColorStatic(type);
    }
    if (add != null) {
      pos = pos.add(add);
    }

    return SlimeColorizer.getColorForPos(pos, type);
  }
}
