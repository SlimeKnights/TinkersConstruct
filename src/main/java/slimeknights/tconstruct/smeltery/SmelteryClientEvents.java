package slimeknights.tconstruct.smeltery;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.resource.ReloadableResourceManager;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.smeltery.client.SingleItemScreenFactory;
import slimeknights.tconstruct.smeltery.client.inventory.MelterScreen;
import slimeknights.tconstruct.smeltery.client.inventory.SmelteryScreen;
import slimeknights.tconstruct.smeltery.client.render.CastingTileEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.ChannelTileEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.FaucetTileEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.MelterTileEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.SmelteryTileEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.TankTileEntityRenderer;

@SuppressWarnings("unused")
public class SmelteryClientEvents extends ClientEventBase {
  /**
   * Called by TinkerClient to add the resource listeners, runs during constructor
   */
  public static void addResourceListener(ReloadableResourceManager manager) {
//    FaucetFluidLoader.initialize();
  }

  @Override
  public void onInitializeClient() {
    // render layers
    RenderLayer cutout = RenderLayer.getCutout();
    // casting
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedFaucet.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.castingBasin.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.castingTable.get(), cutout);
    // controller
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedMelter.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.smelteryController.get(), cutout);
    // peripherals
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedDrain.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedDuct.get(), cutout);
    TinkerSmeltery.searedTank.forEach(tank -> BlockRenderLayerMap.INSTANCE.putBlock(tank, cutout));
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedGlass.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedGlassPane.get(), cutout);

    // TESRs
    BlockEntityRendererRegistry.INSTANCE.register(TinkerSmeltery.tank, TankTileEntityRenderer::new);
    BlockEntityRendererRegistry.INSTANCE.register(TinkerSmeltery.faucet, FaucetTileEntityRenderer::new);
    BlockEntityRendererRegistry.INSTANCE.register(TinkerSmeltery.channel, ChannelTileEntityRenderer::new);
    BlockEntityRendererRegistry.INSTANCE.register(TinkerSmeltery.table, CastingTileEntityRenderer::new);
    BlockEntityRendererRegistry.INSTANCE.register(TinkerSmeltery.basin, CastingTileEntityRenderer::new);
    BlockEntityRendererRegistry.INSTANCE.register(TinkerSmeltery.melter, MelterTileEntityRenderer::new);
    BlockEntityRendererRegistry.INSTANCE.register(TinkerSmeltery.smeltery, SmelteryTileEntityRenderer::new);

    // screens
    HandledScreens.register(TinkerSmeltery.melterContainer, MelterScreen::new);
    HandledScreens.register(TinkerSmeltery.smelteryContainer, SmelteryScreen::new);
    HandledScreens.register(TinkerSmeltery.singleItemContainer, new SingleItemScreenFactory());

    FluidTooltipHandler.init();
  }

//  @SubscribeEvent
//  static void registerModelLoaders(ModelRegistryEvent event) {
//    ModelLoaderRegistry.registerLoader(Util.getResource("tank"), TankModel.LOADER);
//    ModelLoaderRegistry.registerLoader(Util.getResource("casting"), CastingModel.LOADER);
//    ModelLoaderRegistry.registerLoader(Util.getResource("melter"), MelterModel.LOADER);
//    ModelLoaderRegistry.registerLoader(Util.getResource("channel"), ChannelModel.LOADER);
//    ModelLoaderRegistry.registerLoader(Util.getResource("fluid_texture"), FluidTextureModel.LOADER);
//  }
//
//  @SubscribeEvent
//  static void blockColors(ColorHandlerEvent.Block event) {
//    BlockColors colors = event.getBlockColors();
//    BlockColorProvider handler = (state, world, pos, index) -> {
//      if (pos != null && world != null) {
//        BlockEntity te = world.getBlockEntity(pos);
//        if (te instanceof ITankTileEntity) {
//          FluidVolume fluid = ((ITankTileEntity)te).getTank().getFluid();
//          return fluid.getFluid().getAttributes().getColor(fluid);
//        }
//      }
//      return -1;
//    };
//    TinkerSmeltery.searedTank.forEach(tank -> colors.registerColorProvider(handler, tank));
//    colors.registerColorProvider(handler, TinkerSmeltery.searedMelter.get());
//
//    // color the extra fluid textures
//    colors.registerColorProvider((state, world, pos, index) -> {
//      if (index == 1 && world != null && pos != null) {
//        BlockEntity te = world.getBlockEntity(pos);
//        if (te instanceof ISmelteryTankHandler) {
//          FluidVolume bottom = ((ISmelteryTankHandler)te).getTank().getFluidInTank(0);
//          if (!bottom.isEmpty()) {
//            return bottom.getFluid().getAttributes().getColor(bottom);
//          }
//        }
//      }
//      return -1;
//    }, TinkerSmeltery.smelteryController.get());
//    colors.registerColorProvider((state, world, pos, index) -> {
//      if (index == 1 && world != null && pos != null) {
//        BlockEntity te = world.getBlockEntity(pos);
//        if (te instanceof DrainTileEntity) {
//          return ((DrainTileEntity)te).getDisplayFluid().getAttributes().getColor();
//        }
//      }
//      return -1;
//    }, TinkerSmeltery.searedDrain.get());
//    colors.registerColorProvider((state, world, pos, index) -> {
//      if (index == 1 && world != null && pos != null) {
//        BlockEntity te = world.getBlockEntity(pos);
//        if (te instanceof DuctTileEntity) {
//          return ((DuctTileEntity)te).getItemHandler().getFluid().getAttributes().getColor();
//        }
//      }
//      return -1;
//    }, TinkerSmeltery.searedDuct.get());
//  }
//
//  @SubscribeEvent
//  static void itemColors(ColorHandlerEvent.Item event) {
//    event.getItemColors().register((stack, index) -> {
//      FluidTank tank = TankItem.getFluidTank(stack);
//      if (!tank.isEmpty()) {
//        FluidVolume fluid = tank.getFluid();
//        return fluid.getFluid().getAttributes().getColor(fluid);
//      }
//      return -1;
//    }, TinkerSmeltery.searedTank.values().toArray(new Block[0]));
//  }
}
