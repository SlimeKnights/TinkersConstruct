package tconstruct.tools;

import net.minecraft.util.ResourceLocation;

import tconstruct.ClientProxy;

import static tconstruct.tools.TinkerTools.*;

public class ToolClientProxy extends ClientProxy {

  @Override
  public void registerModels() {
    registerToolModel(pickaxe);

    // parts
    registerMaterialItemModel(pickHead);
    registerMaterialItemModel(binding);
    registerMaterialItemModel(toolrod);
  }
}
