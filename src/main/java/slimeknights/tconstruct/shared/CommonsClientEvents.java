package slimeknights.tconstruct.shared;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.state.BooleanProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.renderer.font.CustomFontRenderer;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.model.ConnectedModel;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.tools.ToolClientEvents;

@EventBusSubscriber(modid=TConstruct.modID, value=Dist.CLIENT, bus=Bus.MOD)
public class CommonsClientEvents extends ClientEventBase {

  public static Minecraft minecraft = Minecraft.getInstance();

  public static CustomFontRenderer fontRenderer;

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

    // register predicate to compare two pane blocks. Currently unused, but will be used in a later Forge update
    ConnectedModel.registerConnectionType(Util.getResource("pane"), (state, neighbor) -> {
      // must be the same block, and either both blocks must be center only, or neither are center only
      return state.getBlock() == neighbor.getBlock()
             && (safeGet(state, SixWayBlock.NORTH) || safeGet(state, SixWayBlock.EAST) || safeGet(state, SixWayBlock.SOUTH) || safeGet(state, SixWayBlock.WEST))
                == (safeGet(neighbor, SixWayBlock.NORTH) || safeGet(neighbor, SixWayBlock.EAST) || safeGet(neighbor, SixWayBlock.SOUTH) || safeGet(neighbor, SixWayBlock.WEST));

    });
  }

  /**
   * Gets a property from the state if it exists
   * @param state  State with property
   * @param prop   Property to get
   * @return  True if the property exists and is true, false if false or missing
   */
  private static boolean safeGet(BlockState state, BooleanProperty prop) {
    return state.has(prop) && state.get(prop);
  }

  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(Util.getResource("connected"), ConnectedModel.Loader.INSTANCE);
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

  @SubscribeEvent
  static void commonSetup(final FMLCommonSetupEvent event) {
    CommonsClientEvents.fontRenderer = new CustomFontRenderer(Minecraft.getInstance().fontRenderer);
    CommonsClientEvents.fontRenderer.setBidiFlag(Minecraft.getInstance().getLanguageManager().isCurrentLanguageBidirectional());

    Minecraft minecraft = Minecraft.getInstance();

    if (minecraft != null) {
      IResourceManager manager = Minecraft.getInstance().getResourceManager();
      if (manager instanceof IReloadableResourceManager) {
        ((IReloadableResourceManager) manager).addReloadListener(CommonsClientEvents.fontRenderer);
      }
    }
  }
}
