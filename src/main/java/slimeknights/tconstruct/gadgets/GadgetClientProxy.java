package slimeknights.tconstruct.gadgets;

import net.minecraftforge.common.MinecraftForge;

import slimeknights.tconstruct.common.ClientProxy;

public class GadgetClientProxy extends ClientProxy {

  @Override
  protected void registerModels() {
    super.registerModels();

    registerItemModel(TinkerGadgets.slimeSling);
    registerItemModel(TinkerGadgets.slimeBoots);
  }

  @Override
  public void postInit() {
    super.postInit();

    //MinecraftForge.EVENT_BUS.register(new GadgetClientEvents());
  }
}
