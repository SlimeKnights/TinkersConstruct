package slimeknights.tconstruct.smeltery;

import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.mantle.client.model.FaucetFluidLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.model.block.CastingModel;
import slimeknights.tconstruct.library.client.model.block.ChannelModel;
import slimeknights.tconstruct.library.client.model.block.FluidTextureModel;
import slimeknights.tconstruct.library.client.model.block.MelterModel;
import slimeknights.tconstruct.library.client.model.block.TankModel;
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
import slimeknights.tconstruct.smeltery.item.TankItem;
import slimeknights.tconstruct.smeltery.tileentity.DrainTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.DuctTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.ITankTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.tank.ISmelteryTankHandler;

@SuppressWarnings("unused")
@EventBusSubscriber(modid= TConstruct.modID, value= Dist.CLIENT, bus= Bus.MOD)
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
    // casting
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedFaucet.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.castingBasin.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.castingTable.get(), cutout);
    // controller
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedMelter.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.smelteryController.get(), cutout);
    // peripherals
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedDrain.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedDuct.get(), cutout);
    TinkerSmeltery.searedTank.forEach(tank -> RenderTypeLookup.setRenderLayer(tank, cutout));
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedGlass.get(), cutout);
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedGlassPane.get(), cutout);

    // TESRs
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.tank.get(), TankTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.faucet.get(), FaucetTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.channel.get(), ChannelTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.table.get(), CastingTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.basin.get(), CastingTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.melter.get(), MelterTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.smeltery.get(), SmelteryTileEntityRenderer::new);

    // screens
    ScreenManager.registerFactory(TinkerSmeltery.melterContainer.get(), MelterScreen::new);
    ScreenManager.registerFactory(TinkerSmeltery.smelteryContainer.get(), SmelteryScreen::new);
    ScreenManager.registerFactory(TinkerSmeltery.singleItemContainer.get(), new SingleItemScreenFactory());

    FluidTooltipHandler.init();
  }

  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(Util.getResource("tank"), TankModel.LOADER);
    ModelLoaderRegistry.registerLoader(Util.getResource("casting"), CastingModel.LOADER);
    ModelLoaderRegistry.registerLoader(Util.getResource("melter"), MelterModel.LOADER);
    ModelLoaderRegistry.registerLoader(Util.getResource("channel"), ChannelModel.LOADER);
    ModelLoaderRegistry.registerLoader(Util.getResource("fluid_texture"), FluidTextureModel.LOADER);
  }

  @SubscribeEvent
  static void blockColors(ColorHandlerEvent.Block event) {
    BlockColors colors = event.getBlockColors();
    IBlockColor handler = (state, world, pos, index) -> {
      if (pos != null && world != null) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ITankTileEntity) {
          FluidStack fluid = ((ITankTileEntity)te).getTank().getFluid();
          return fluid.getFluid().getAttributes().getColor(fluid);
        }
      }
      return -1;
    };
    TinkerSmeltery.searedTank.forEach(tank -> colors.register(handler, tank));
    colors.register(handler, TinkerSmeltery.searedMelter.get());

    // color the extra fluid textures
    colors.register((state, world, pos, index) -> {
      if (index == 1 && world != null && pos != null) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ISmelteryTankHandler) {
          FluidStack bottom = ((ISmelteryTankHandler)te).getTank().getFluidInTank(0);
          if (!bottom.isEmpty()) {
            return bottom.getFluid().getAttributes().getColor(bottom);
          }
        }
      }
      return -1;
    }, TinkerSmeltery.smelteryController.get());
    colors.register((state, world, pos, index) -> {
      if (index == 1 && world != null && pos != null) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof DrainTileEntity) {
          return ((DrainTileEntity)te).getDisplayFluid().getAttributes().getColor();
        }
      }
      return -1;
    }, TinkerSmeltery.searedDrain.get());
    colors.register((state, world, pos, index) -> {
      if (index == 1 && world != null && pos != null) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof DuctTileEntity) {
          return ((DuctTileEntity)te).getItemHandler().getFluid().getAttributes().getColor();
        }
      }
      return -1;
    }, TinkerSmeltery.searedDuct.get());
  }

  @SubscribeEvent
  static void itemColors(ColorHandlerEvent.Item event) {
    event.getItemColors().register((stack, index) -> {
      FluidTank tank = TankItem.getFluidTank(stack);
      if (!tank.isEmpty()) {
        FluidStack fluid = tank.getFluid();
        return fluid.getFluid().getAttributes().getColor(fluid);
      }
      return -1;
    }, TinkerSmeltery.searedTank.values().toArray(new Block[0]));
  }
}
