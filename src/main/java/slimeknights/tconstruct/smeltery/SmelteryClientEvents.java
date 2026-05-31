package slimeknights.tconstruct.smeltery;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.mantle.client.render.ChannelFluids;
import slimeknights.mantle.client.render.FaucetFluid;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.TinkerItemDisplays;
import slimeknights.tconstruct.library.client.model.block.FluidTextureModel;
import slimeknights.tconstruct.library.client.model.block.TankModel;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;
import slimeknights.tconstruct.smeltery.client.render.CastingBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.ChannelBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.FaucetBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.GaugeBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.HeatingStructureBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.ProxyTankBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.TankBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.TankInventoryBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.screen.AlloyerScreen;
import slimeknights.tconstruct.smeltery.client.screen.HeatingStructureScreen;
import slimeknights.tconstruct.smeltery.client.screen.MelterScreen;
import slimeknights.tconstruct.smeltery.client.screen.SingleItemScreenFactory;

@SuppressWarnings("unused")
@EventBusSubscriber(modid= TConstruct.MOD_ID, value= Dist.CLIENT, bus= Bus.MOD)
public class SmelteryClientEvents extends ClientEventBase {
  @SubscribeEvent
  static void addResourceListener(RegisterClientReloadListenersEvent event) {
    FaucetFluid.initialize(event);
    ChannelFluids.initialize(event);
  }

  @SubscribeEvent
  static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(TinkerSmeltery.tank.get(), TankBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.fluidCannon.get(), context -> new TankInventoryBlockEntityRenderer<>(BlockStateProperties.FACING));
    event.registerBlockEntityRenderer(TinkerSmeltery.faucet.get(), FaucetBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.channel.get(), ChannelBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.gauge.get(), GaugeBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.table.get(), CastingBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.basin.get(), CastingBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.proxyTank.get(), ProxyTankBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.melter.get(), context -> new TankInventoryBlockEntityRenderer<>(BlockStateProperties.HORIZONTAL_FACING));
    event.registerBlockEntityRenderer(TinkerSmeltery.alloyer.get(), TankBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.smeltery.get(), HeatingStructureBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.foundry.get(), HeatingStructureBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.castingTank.get(), context -> new TankInventoryBlockEntityRenderer<>(BlockStateProperties.HORIZONTAL_FACING));
  }

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    ToolModel.registerSmallTool(TinkerItemDisplays.MELTER);
    ToolModel.registerSmallTool(TinkerItemDisplays.CASTING_BASIN);
    ToolModel.registerSmallTool(TinkerItemDisplays.CASTING_TABLE);
  }

  @SubscribeEvent
  static void registerMenuScreens(RegisterMenuScreensEvent event) {
    event.register(TinkerSmeltery.melterContainer.get(), MelterScreen::new);
    event.register(TinkerSmeltery.smelteryContainer.get(), HeatingStructureScreen::new);
    event.register(TinkerSmeltery.singleItemContainer.get(), new SingleItemScreenFactory());
    event.register(TinkerSmeltery.alloyerContainer.get(), AlloyerScreen::new);
  }

  @SubscribeEvent
  static void registerModelLoaders(RegisterGeometryLoaders event) {
    event.register(TConstruct.getResource("tank"), TankModel.LOADER);
    event.register(TConstruct.getResource("fluid_texture"), FluidTextureModel.LOADER);
  }
}
