package slimeknights.tconstruct.smeltery;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.common.ClientProxy;

public class SmelteryClientProxy extends ClientProxy {

  @Override
  public void preInit() {
    super.preInit();

    //MinecraftForge.EVENT_BUS.register(new SmelteryClientEvents());
  }

  @Override
  public void registerModels() {

  }

  @Override
  public void init() {
    Minecraft minecraft = Minecraft.getInstance();

    /*
    TODO
    // slime channels
    ItemColors colors = minecraft.getItemColors();

    colors.register((itemStack, tintIndex) -> {
      if (!itemStack.hasTag()) {
        return 0xFFFFFF;
      }

      FluidStack fluid = FluidStack.loadFluidStackFromNBT(itemStack.getTag());

      if (fluid != null && fluid.getAmount() > 0 && fluid.getFluid() != null) {
        return fluid.getFluid().getAttributes().getColor(fluid);
      }

      return 0xFFFFFF;
    }, WorldBlocks.blue_slime_vine);*/
    super.init();
  }
}
