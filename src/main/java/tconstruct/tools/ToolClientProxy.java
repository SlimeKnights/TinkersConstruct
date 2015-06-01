package tconstruct.tools;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import tconstruct.ClientProxy;
import tconstruct.library.Util;
import tconstruct.library.client.model.MaterialModelLoader;
import tconstruct.library.tinkering.modifiers.IModifier;

import static tconstruct.tools.TinkerTools.*;

public class ToolClientProxy extends ClientProxy {

  @Override
  public void registerModels() {
    registerToolModel(pickaxe);

    // parts
    registerPartModel(pickHead);
    registerPartModel(binding);
    registerPartModel(toolrod);

    // todo: implement actual modifiers
    registerModifierModel(new IModifier() {
      @Override
      public String getIdentifier() {
        return "diamond";
      }
    }, new ResourceLocation(Util.RESOURCE, "models/item/modifiers/Diamond"));
  }

  public ResourceLocation registerPartModel(Item item) {
    ResourceLocation itemLocation = getItemLocation(item);
    if (itemLocation == null) {
      return null;
    }

    String path = "parts/" + itemLocation.getResourcePath() + MaterialModelLoader.EXTENSION;

    return registerMaterialModel(item, new ResourceLocation(itemLocation.getResourceDomain(), path));
  }
}
