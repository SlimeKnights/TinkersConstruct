package tconstruct.tools;

import tconstruct.ClientProxy;

import static tconstruct.tools.TinkerTools.*;

public class ToolClientProxy extends ClientProxy {

  @Override
  public void registerModels() {
    registerMultiModel(pickaxe);

    // parts
    registerMultiModel(pickHead);
    registerMultiModel(binding);
    registerMultiModel(toolrod);
  }
}
