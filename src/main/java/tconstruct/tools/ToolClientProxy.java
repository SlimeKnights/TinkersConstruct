package tconstruct.tools;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.io.File;

import tconstruct.ClientProxy;
import tconstruct.library.client.model.MaterialModel;
import tconstruct.library.client.model.MaterialModelLoader;

import static tconstruct.tools.TinkerTools.*;

public class ToolClientProxy extends ClientProxy {

  @Override
  public void registerModels() {
    registerToolModel(pickaxe);

    // parts
    registerPartModel(pickHead);
    registerPartModel(binding);
    registerPartModel(toolrod);
  }

  public ResourceLocation registerPartModel(Item item) {
    ResourceLocation itemLocation = getItemLocation(item);
    if (itemLocation == null) {
      return null;
    }

    String path = "parts/" + itemLocation.getResourcePath() + MaterialModelLoader.MATERIALMODEL_EXTENSION;

    return registerMaterialModel(item, new ResourceLocation(itemLocation.getResourceDomain(), path));
  }
}
