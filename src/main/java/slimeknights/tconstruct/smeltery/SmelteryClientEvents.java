package slimeknights.tconstruct.smeltery;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.resource.ReloadableResourceManager;
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
  public static void addResourceListener(ReloadableResourceManager manager) {
    FaucetFluidLoader.initialize();
  }

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    // render layers
    RenderLayer cutout = RenderLayer.getCutout();
    // casting
    RenderLayers.setRenderLayer(TinkerSmeltery.searedFaucet.get(), cutout);
    RenderLayers.setRenderLayer(TinkerSmeltery.castingBasin.get(), cutout);
    RenderLayers.setRenderLayer(TinkerSmeltery.castingTable.get(), cutout);
    // controller
    RenderLayers.setRenderLayer(TinkerSmeltery.searedMelter.get(), cutout);
    RenderLayers.setRenderLayer(TinkerSmeltery.smelteryController.get(), cutout);
    // peripherals
    RenderLayers.setRenderLayer(TinkerSmeltery.searedDrain.get(), cutout);
    RenderLayers.setRenderLayer(TinkerSmeltery.searedDuct.get(), cutout);
    TinkerSmeltery.searedTank.forEach(tank -> RenderLayers.setRenderLayer(tank, cutout));
    RenderLayers.setRenderLayer(TinkerSmeltery.searedGlass.get(), cutout);
    RenderLayers.setRenderLayer(TinkerSmeltery.searedGlassPane.get(), cutout);

    // TESRs
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.tank.get(), TankTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.faucet.get(), FaucetTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.channel.get(), ChannelTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.table.get(), CastingTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.basin.get(), CastingTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.melter.get(), MelterTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.smeltery.get(), SmelteryTileEntityRenderer::new);

    // screens
    HandledScreens.register(TinkerSmeltery.melterContainer.get(), MelterScreen::new);
    HandledScreens.register(TinkerSmeltery.smelteryContainer.get(), SmelteryScreen::new);
    HandledScreens.register(TinkerSmeltery.singleItemContainer.get(), new SingleItemScreenFactory());

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
    BlockColorProvider handler = (state, world, pos, index) -> {
      if (pos != null && world != null) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ITankTileEntity) {
          FluidStack fluid = ((ITankTileEntity)te).getTank().getFluid();
          return fluid.getFluid().getAttributes().getColor(fluid);
        }
      }
      return -1;
    };
    TinkerSmeltery.searedTank.forEach(tank -> colors.registerColorProvider(handler, tank));
    colors.registerColorProvider(handler, TinkerSmeltery.searedMelter.get());

    // color the extra fluid textures
    colors.registerColorProvider((state, world, pos, index) -> {
      if (index == 1 && world != null && pos != null) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ISmelteryTankHandler) {
          FluidStack bottom = ((ISmelteryTankHandler)te).getTank().getFluidInTank(0);
          if (!bottom.isEmpty()) {
            return bottom.getFluid().getAttributes().getColor(bottom);
          }
        }
      }
      return -1;
    }, TinkerSmeltery.smelteryController.get());
    colors.registerColorProvider((state, world, pos, index) -> {
      if (index == 1 && world != null && pos != null) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof DrainTileEntity) {
          return ((DrainTileEntity)te).getDisplayFluid().getAttributes().getColor();
        }
      }
      return -1;
    }, TinkerSmeltery.searedDrain.get());
    colors.registerColorProvider((state, world, pos, index) -> {
      if (index == 1 && world != null && pos != null) {
        BlockEntity te = world.getBlockEntity(pos);
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
