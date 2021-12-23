package slimeknights.tconstruct.smeltery;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.mantle.client.model.FaucetFluidLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.FluidTooltipHandler;
import slimeknights.tconstruct.library.client.model.block.CastingModel;
import slimeknights.tconstruct.library.client.model.block.ChannelModel;
import slimeknights.tconstruct.library.client.model.block.FluidTextureModel;
import slimeknights.tconstruct.library.client.model.block.MelterModel;
import slimeknights.tconstruct.library.client.model.block.TankModel;
import slimeknights.tconstruct.smeltery.client.SingleItemScreenFactory;
import slimeknights.tconstruct.smeltery.client.inventory.AlloyerScreen;
import slimeknights.tconstruct.smeltery.client.inventory.HeatingStructureScreen;
import slimeknights.tconstruct.smeltery.client.inventory.MelterScreen;
import slimeknights.tconstruct.smeltery.client.render.CastingTileEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.ChannelTileEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.FaucetTileEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.HeatingStructureTileEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.MelterTileEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.TankTileEntityRenderer;

@SuppressWarnings("unused")
@EventBusSubscriber(modid= TConstruct.MOD_ID, value= Dist.CLIENT, bus= Bus.MOD)
public class SmelteryClientEvents extends ClientEventBase {
  /**
   * Called by TinkerClient to add the resource listeners, runs during constructor
   */
  public static void addResourceListener(IReloadableResourceManager manager) {
    FaucetFluidLoader.initialize();
  }

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    // render layers
    RenderType cutout = RenderType.getCutout();
    // seared
    // casting
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedFaucet.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedBasin.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedTable.get(), cutout);
    // controller
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedMelter.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.smelteryController.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.foundryController.get(), cutout);
    // peripherals
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedDrain.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedDuct.get(), cutout);
    TinkerSmeltery.searedTank.forEach(tank -> RenderTypeLookup.setRenderLayer(tank, cutout));
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedLantern.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedGlass.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedGlassPane.get(), cutout);
    // scorched
    // casting
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.scorchedFaucet.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.scorchedBasin.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.scorchedTable.get(), cutout);
    // controller
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.scorchedAlloyer.get(), cutout);
    // peripherals
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.scorchedDrain.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.scorchedDuct.get(), cutout);
    TinkerSmeltery.scorchedTank.forEach(tank -> RenderTypeLookup.setRenderLayer(tank, cutout));
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.scorchedLantern.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.scorchedGlass.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.scorchedGlassPane.get(), cutout);

    // TESRs
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.tank.get(), TankTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.faucet.get(), FaucetTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.channel.get(), ChannelTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.table.get(), CastingTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.basin.get(), CastingTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.melter.get(), MelterTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.alloyer.get(), TankTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.smeltery.get(), HeatingStructureTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.foundry.get(), HeatingStructureTileEntityRenderer::new);

    // screens
    ScreenManager.registerFactory(TinkerSmeltery.melterContainer.get(), MelterScreen::new);
    ScreenManager.registerFactory(TinkerSmeltery.smelteryContainer.get(), HeatingStructureScreen::new);
    ScreenManager.registerFactory(TinkerSmeltery.singleItemContainer.get(), new SingleItemScreenFactory());
    ScreenManager.registerFactory(TinkerSmeltery.alloyerContainer.get(), AlloyerScreen::new);

    FluidTooltipHandler.init();
  }

  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(TConstruct.getResource("tank"), TankModel.LOADER);
    ModelLoaderRegistry.registerLoader(TConstruct.getResource("casting"), CastingModel.LOADER);
    ModelLoaderRegistry.registerLoader(TConstruct.getResource("melter"), MelterModel.LOADER);
    ModelLoaderRegistry.registerLoader(TConstruct.getResource("channel"), ChannelModel.LOADER);
    ModelLoaderRegistry.registerLoader(TConstruct.getResource("fluid_texture"), FluidTextureModel.LOADER);
  }
}
