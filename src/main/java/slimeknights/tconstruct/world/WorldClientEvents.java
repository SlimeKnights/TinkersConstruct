package slimeknights.tconstruct.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.SkullBlock.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.particle.SlimeParticle;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.tools.client.SlimeskullArmorModel;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.world.client.PiglinHeadModel;
import slimeknights.tconstruct.world.client.SlimeColorReloadListener;
import slimeknights.tconstruct.world.client.SlimeColorizer;
import slimeknights.tconstruct.world.client.TerracubeRenderer;
import slimeknights.tconstruct.world.client.TinkerSlimeRenderer;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
@EventBusSubscriber(modid=TConstruct.MOD_ID, value=Dist.CLIENT, bus=Bus.MOD)
public class WorldClientEvents extends ClientEventBase {
  @SubscribeEvent
  static void addResourceListener(RegisterClientReloadListenersEvent event) {
    for (SlimeType type : SlimeType.values()) {
      event.registerReloadListener(new SlimeColorReloadListener(type));
    }
  }

  @SubscribeEvent
  static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
    ParticleEngine engine = Minecraft.getInstance().particleEngine;
    engine.register(TinkerWorld.skySlimeParticle.get(), new SlimeParticle.Factory(SlimeType.SKY));
    engine.register(TinkerWorld.enderSlimeParticle.get(), new SlimeParticle.Factory(SlimeType.ENDER));
    engine.register(TinkerWorld.terracubeParticle.get(), new SlimeParticle.Factory(Items.CLAY_BALL));
  }

  @SubscribeEvent
  static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(TinkerWorld.earthSlimeEntity.get(), SlimeRenderer::new);
    event.registerEntityRenderer(TinkerWorld.skySlimeEntity.get(), TinkerSlimeRenderer.SKY_SLIME_FACTORY);
    event.registerEntityRenderer(TinkerWorld.enderSlimeEntity.get(), TinkerSlimeRenderer.ENDER_SLIME_FACTORY);
    event.registerEntityRenderer(TinkerWorld.terracubeEntity.get(), TerracubeRenderer::new);
  }

  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    RenderType cutout = RenderType.cutout();
    RenderType cutoutMipped = RenderType.cutoutMipped();

    // render types - slime plants
    for (SlimeType type : SlimeType.values()) {
      if (type != SlimeType.BLOOD) {
        ItemBlockRenderTypes.setRenderLayer(TinkerWorld.slimeLeaves.get(type), cutoutMipped);
      }
      ItemBlockRenderTypes.setRenderLayer(TinkerWorld.vanillaSlimeGrass.get(type), cutoutMipped);
      ItemBlockRenderTypes.setRenderLayer(TinkerWorld.earthSlimeGrass.get(type), cutoutMipped);
      ItemBlockRenderTypes.setRenderLayer(TinkerWorld.skySlimeGrass.get(type), cutoutMipped);
      ItemBlockRenderTypes.setRenderLayer(TinkerWorld.enderSlimeGrass.get(type), cutoutMipped);
      ItemBlockRenderTypes.setRenderLayer(TinkerWorld.ichorSlimeGrass.get(type), cutoutMipped);
      ItemBlockRenderTypes.setRenderLayer(TinkerWorld.slimeFern.get(type), cutout);
      ItemBlockRenderTypes.setRenderLayer(TinkerWorld.slimeTallGrass.get(type), cutout);
      ItemBlockRenderTypes.setRenderLayer(TinkerWorld.slimeSapling.get(type), cutout);
    }
    ItemBlockRenderTypes.setRenderLayer(TinkerWorld.enderSlimeVine.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerWorld.skySlimeVine.get(), cutout);

    // render types - slime blocks
    RenderType translucent = RenderType.translucent();
    for (SlimeType type : SlimeType.TINKER) {
      ItemBlockRenderTypes.setRenderLayer(TinkerWorld.slime.get(type), translucent);
    }

    // doors
    ItemBlockRenderTypes.setRenderLayer(TinkerWorld.greenheart.getDoor(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerWorld.greenheart.getTrapdoor(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerWorld.skyroot.getDoor(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerWorld.skyroot.getTrapdoor(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerWorld.bloodshroom.getDoor(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerWorld.bloodshroom.getTrapdoor(), cutout);

    // skull rendering
    // TODO: mojang changed all of this
    SkullModel normalHead = null; //new SkullModel(0, 0, 64, 32);
    SkullModel tinkersOverlayHead = null; //new HeadWithOverlayModel(0, 0, 0, 16, 32, 32);
    registerHeadModel(TinkerHeadType.BLAZE, MaterialIds.blazingBone, normalHead, new ResourceLocation("textures/entity/blaze.png"));
    registerHeadModel(TinkerHeadType.ENDERMAN, MaterialIds.enderPearl, null/*new SkullModel(0, 0, 32, 16)*/, TConstruct.getResource("textures/entity/skull/enderman.png"));
    SlimeskullArmorModel.registerHeadModel(MaterialIds.gunpowder, normalHead, new ResourceLocation("textures/entity/creeper/creeper.png"));
    // skeleton
    SlimeskullArmorModel.registerHeadModel(MaterialIds.bone, normalHead, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
    SlimeskullArmorModel.registerHeadModel(MaterialIds.necroticBone, normalHead, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
    registerHeadModel(TinkerHeadType.STRAY, MaterialIds.bloodbone, tinkersOverlayHead, TConstruct.getResource("textures/entity/skull/stray.png"));
    // zombies
    SkullModel zombieHead = null; //new SkullModel(0, 0, 64, 64);
    SlimeskullArmorModel.registerHeadModel(MaterialIds.rottenFlesh, zombieHead, new ResourceLocation("textures/entity/zombie/zombie.png"));
    registerHeadModel(TinkerHeadType.HUSK, MaterialIds.iron, zombieHead, new ResourceLocation("textures/entity/zombie/husk.png"));
    registerHeadModel(TinkerHeadType.DROWNED, MaterialIds.copper, tinkersOverlayHead, TConstruct.getResource("textures/entity/skull/drowned.png"));
    // spider
    SkullModel spiderHead = null; //new SkullModel(32, 4, 64, 32);
    registerHeadModel(TinkerHeadType.SPIDER, MaterialIds.spider, spiderHead, new ResourceLocation("textures/entity/spider/spider.png"));
    registerHeadModel(TinkerHeadType.CAVE_SPIDER, MaterialIds.venom, spiderHead, new ResourceLocation("textures/entity/spider/cave_spider.png"));
    // piglins
    SkullModel piglinHead = new PiglinHeadModel(null);
    registerHeadModel(TinkerHeadType.PIGLIN,           MaterialIds.gold,     piglinHead, new ResourceLocation("textures/entity/piglin/piglin.png"));
    registerHeadModel(TinkerHeadType.PIGLIN_BRUTE,     MaterialIds.roseGold, piglinHead, new ResourceLocation("textures/entity/piglin/piglin_brute.png"));
    registerHeadModel(TinkerHeadType.ZOMBIFIED_PIGLIN, MaterialIds.pigIron,  piglinHead, new ResourceLocation("textures/entity/piglin/zombified_piglin.png"));
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
      pos = pos.offset(add);
    }

    return SlimeColorizer.getColorForPos(pos, type);
  }

  /** Registers a skull with the entity renderer and the slimeskull renderer */
  private static void registerHeadModel(Type skull, MaterialId materialId, SkullModel head, ResourceLocation texture) {
    //SkullBlockRenderer.MODEL_BY_TYPE.put(skull, head);
    SkullBlockRenderer.SKIN_BY_TYPE.put(skull, texture);
    SlimeskullArmorModel.registerHeadModel(materialId, head, texture);
  }
}
