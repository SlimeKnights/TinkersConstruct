package slimeknights.tconstruct.smeltery;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
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
import slimeknights.tconstruct.library.client.model.TankModel;
import slimeknights.tconstruct.smeltery.block.SearedTankBlock;
import slimeknights.tconstruct.smeltery.client.render.TankTileEntityRenderer;
import slimeknights.tconstruct.smeltery.item.TankItem;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

@SuppressWarnings("unused")
@EventBusSubscriber(modid= TConstruct.modID, value= Dist.CLIENT, bus= Bus.MOD)
public class SmelteryClientEvents extends ClientEventBase {
  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedGlass.get(), RenderType.getCutout());
    for (SearedTankBlock.TankType tankType : SearedTankBlock.TankType.values()) {
      RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedTank.get(tankType), RenderType.getCutout());
    }
    RenderTypeLookup.setRenderLayer(TinkerSmeltery.searedFaucet.get(), RenderType.getCutout());

    ClientRegistry.bindTileEntityRenderer(TinkerSmeltery.tank.get(), TankTileEntityRenderer::new);
  }

  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(Util.getResource("tank"), TankModel.Loader.INSTANCE);
  }

  @SubscribeEvent
  static void blockColors(ColorHandlerEvent.Block event) {
    event.getBlockColors().register((state, world, pos, index) -> {
      if (pos != null && world != null) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TankTileEntity) {
          FluidStack fluid = ((TankTileEntity)te).getInternalTank().getFluid();
          return fluid.getFluid().getAttributes().getColor(fluid);
        }
      }
      return -1;
    }, TinkerSmeltery.searedTank.values().toArray(new Block[0]));
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
