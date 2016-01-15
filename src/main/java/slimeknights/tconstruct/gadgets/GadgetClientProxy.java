package slimeknights.tconstruct.gadgets;

import slimeknights.tconstruct.common.ClientProxy;

public class GadgetClientProxy extends ClientProxy {

  @Override
  protected void registerModels() {
    super.registerModels();

    registerItemModel(TinkerGadgets.slimeSling);
  }
}
