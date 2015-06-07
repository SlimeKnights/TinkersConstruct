package tconstruct.tools;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import tconstruct.ClientProxy;
import tconstruct.library.Util;
import tconstruct.library.client.CustomTextureCreator;
import tconstruct.library.client.model.MaterialModelLoader;
import tconstruct.library.tinkering.modifiers.IModifier;

import static tconstruct.tools.TinkerTools.*;

public class ToolClientProxy extends ClientProxy {

  @Override
  public void registerModels() {
    //MinecraftForge.EVENT_BUS.register(new EventHandlerClient());

    registerToolModel(pickaxe);

    // parts
    registerPartModel(pickHead);
    registerPartModel(binding);
    registerPartModel(toolrod);
    
    registerModifierModel(TinkerTools.diamondMod, Util.getResource("models/item/modifiers/Diamond"));
    registerModifierModel(TinkerTools.fortifyMod, Util.getResource("models/item/modifiers/Fortify"));
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
