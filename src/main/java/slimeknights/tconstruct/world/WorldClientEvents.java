package slimeknights.tconstruct.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.renderer.BlueSlimeRenderer;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.client.SlimeColorizer;
import slimeknights.tconstruct.world.client.slime.BlueColorReloadListener;
import slimeknights.tconstruct.world.client.slime.OrangeColorReloadListener;
import slimeknights.tconstruct.world.client.slime.PurpleColorReloadListener;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
@EventBusSubscriber(modid=TConstruct.modID, value=Dist.CLIENT, bus=Bus.MOD)
public class WorldClientEvents extends ClientEventBase {

  public static SlimeColorizer slimeColorizer = new SlimeColorizer();

  static {
    // TODO: find a proper event to use here instead
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft != null) {
      IResourceManager iManager = Minecraft.getInstance().getResourceManager();
      if (iManager instanceof IReloadableResourceManager) {
        IReloadableResourceManager reloadable = (IReloadableResourceManager)iManager;
        reloadable.addReloadListener(new BlueColorReloadListener());
        reloadable.addReloadListener(new PurpleColorReloadListener());
        reloadable.addReloadListener(new OrangeColorReloadListener());
      }
    }
  }

  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(TinkerWorld.blueSlimeEntity.get(), BlueSlimeRenderer.BLUE_SLIME_FACTORY);
    RenderingRegistry.registerEntityRenderingHandler(TinkerTools.indestructibleItem.get(), manager -> new ItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));

    // render types - ores
    RenderTypeLookup.setRenderLayer(TinkerWorld.cobaltOre.get(), RenderType.getCutoutMipped());
    RenderTypeLookup.setRenderLayer(TinkerWorld.arditeOre.get(), RenderType.getCutoutMipped());

    // render types - slime plants
    for (SlimeGrassBlock.FoliageType type : SlimeGrassBlock.FoliageType.values()) {
      RenderTypeLookup.setRenderLayer(TinkerWorld.slimeLeaves.get(type), RenderType.getCutoutMipped());
      RenderTypeLookup.setRenderLayer(TinkerWorld.vanillaSlimeGrass.get(type), RenderType.getCutoutMipped());
      RenderTypeLookup.setRenderLayer(TinkerWorld.greenSlimeGrass.get(type), RenderType.getCutoutMipped());
      RenderTypeLookup.setRenderLayer(TinkerWorld.blueSlimeGrass.get(type), RenderType.getCutoutMipped());
      RenderTypeLookup.setRenderLayer(TinkerWorld.purpleSlimeGrass.get(type), RenderType.getCutoutMipped());
      RenderTypeLookup.setRenderLayer(TinkerWorld.magmaSlimeGrass.get(type), RenderType.getCutoutMipped());
      RenderTypeLookup.setRenderLayer(TinkerWorld.slimeFern.get(type), RenderType.getCutout());
      RenderTypeLookup.setRenderLayer(TinkerWorld.slimeTallGrass.get(type), RenderType.getCutout());
      RenderTypeLookup.setRenderLayer(TinkerWorld.slimeSapling.get(type), RenderType.getCutout());
    }
    RenderTypeLookup.setRenderLayer(TinkerWorld.purpleSlimeVine.get(), RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(TinkerWorld.purpleSlimeVineMiddle.get(), RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(TinkerWorld.purpleSlimeVineEnd.get(), RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(TinkerWorld.blueSlimeVine.get(), RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(TinkerWorld.blueSlimeVineMiddle.get(), RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(TinkerWorld.blueSlimeVineEnd.get(), RenderType.getCutout());

    // render types - slime blocks
    for (SlimeBlock.SlimeType type : SlimeBlock.SlimeType.TINKER) {
      RenderTypeLookup.setRenderLayer(TinkerWorld.slime.get(type), RenderType.getTranslucent());
    }
  }

  @SubscribeEvent
  static void registerColorHandlers(ColorHandlerEvent.Item event) {
    BlockColors blockColors = event.getBlockColors();
    ItemColors itemColors = event.getItemColors();

    // slime plants - blocks
    for (SlimeGrassBlock.FoliageType type : SlimeGrassBlock.FoliageType.values()) {
      blockColors.register(
        (state, reader, pos, index) -> getSlimeColorByPos(pos, type, null),
        TinkerWorld.vanillaSlimeGrass.get(type), TinkerWorld.greenSlimeGrass.get(type), TinkerWorld.blueSlimeGrass.get(type),
        TinkerWorld.purpleSlimeGrass.get(type), TinkerWorld.magmaSlimeGrass.get(type)
      );
      blockColors.register(
        (state, reader, pos, index) -> getSlimeColorByPos(pos, type, SlimeColorizer.LOOP_OFFSET),
        TinkerWorld.slimeLeaves.get(type)
      );
      blockColors.register(
        (state, reader, pos, index) -> getSlimeColorByPos(pos, type, null),
        TinkerWorld.slimeFern.get(type), TinkerWorld.slimeTallGrass.get(type)
      );
    }
    // slime grass items
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.vanillaSlimeGrass);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.greenSlimeGrass);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.blueSlimeGrass);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.purpleSlimeGrass);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.magmaSlimeGrass);
    // plant items
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.slimeLeaves);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.slimeFern);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.slimeTallGrass);

    // vines
    blockColors.register(
      (state, reader, pos, index) -> getSlimeColorByPos(pos, SlimeGrassBlock.FoliageType.BLUE, SlimeColorizer.LOOP_OFFSET),
      TinkerWorld.blueSlimeVine.get(), TinkerWorld.blueSlimeVineMiddle.get(), TinkerWorld.blueSlimeVineEnd.get()
    );
    blockColors.register(
      (state, reader, pos, index) -> getSlimeColorByPos(pos, SlimeGrassBlock.FoliageType.PURPLE, SlimeColorizer.LOOP_OFFSET),
      TinkerWorld.purpleSlimeVine.get(), TinkerWorld.purpleSlimeVineMiddle.get(), TinkerWorld.purpleSlimeVineEnd.get()
    );
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.blueSlimeVine);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.blueSlimeVineMiddle);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.blueSlimeVineEnd);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.purpleSlimeVine);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.purpleSlimeVineMiddle);
    registerBlockItemColorAlias(blockColors, itemColors, TinkerWorld.purpleSlimeVineEnd);
  }

  /**
   * Block colors for a slime type
   * @param pos   Block position
   * @param type  Slime foilage color
   * @param add   Offset position
   * @return  Color for the given position, or the default if position is null
   */
  private static int getSlimeColorByPos(@Nullable BlockPos pos, SlimeGrassBlock.FoliageType type, @Nullable BlockPos add) {
    if (pos == null) {
      return SlimeColorizer.getColorStatic(type);
    }
    if (add != null) {
      pos = pos.add(add);
    }

    return SlimeColorizer.getColorForPos(pos, type);
  }
}
