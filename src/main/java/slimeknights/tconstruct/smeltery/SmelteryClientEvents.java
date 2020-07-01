package slimeknights.tconstruct.smeltery;

import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
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
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.model.tesr.CastingModel;
import slimeknights.tconstruct.library.client.model.tesr.FluidsModel;
import slimeknights.tconstruct.library.client.model.tesr.TankModel;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.smeltery.block.SearedTankBlock;
import slimeknights.tconstruct.smeltery.client.FaucetFluidLoader;
import slimeknights.tconstruct.smeltery.client.inventory.MelterScreen;
import slimeknights.tconstruct.smeltery.client.render.CastingTileEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.FaucetTileEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.TankTileEntityRenderer;
import slimeknights.tconstruct.smeltery.item.TankItem;
import slimeknights.tconstruct.smeltery.tileentity.ITankTileEntity;

@SuppressWarnings("unused")
@EventBusSubscriber(modid= TConstruct.modID, value= Dist.CLIENT, bus= Bus.MOD)
public class SmelteryClientEvents extends ClientEventBase {
  /**
   * Called by TinkerClient to add the resource listeners, runs during constructor
   */
  public static void addResourceListener(IReloadableResourceManager manager) {
    manager.addReloadListener(FaucetFluidLoader.INSTANCE);
  }

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    // render layers
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedGlass.get(), RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedGlassPane.get(), RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedMelter.get(), RenderType.getCutout());
    for (SearedTankBlock.TankType tankType : SearedTankBlock.TankType.values()) {
      RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedTank.get(tankType), RenderType.getCutout());
    }
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedFaucet.get(), RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.castingBasin.get(), RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.castingTable.get(), RenderType.getCutout());

    // TESRs
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.tank.get(), TankTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.faucet.get(), FaucetTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.table.get(), CastingTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.basin.get(), CastingTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.melter.get(), TankTileEntityRenderer::new);

    // screens
    ScreenManager.registerFactory(TinkerSmeltery.melterContainer.get(), MelterScreen::new);

    FluidTooltipHandler.init();
  }

  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(Util.getResource("fluids"), FluidsModel.Loader.INSTANCE);
    ModelLoaderRegistry.registerLoader(Util.getResource("tank"), TankModel.Loader.INSTANCE);
    ModelLoaderRegistry.registerLoader(Util.getResource("casting"), CastingModel.Loader.INSTANCE);
  }

  @SubscribeEvent
  static void blockColors(ColorHandlerEvent.Block event) {
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
    event.getBlockColors().register(handler, TinkerSmeltery.searedTank.values().toArray(new Block[0]));
    event.getBlockColors().register(handler, TinkerSmeltery.searedMelter.get());
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
