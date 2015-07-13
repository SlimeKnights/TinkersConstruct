package tconstruct.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import tconstruct.ClientProxy;
import tconstruct.library.Util;
import tconstruct.library.client.model.MaterialModelLoader;
import tconstruct.tools.block.ToolTableBlock;

import static tconstruct.tools.TinkerTools.binding;
import static tconstruct.tools.TinkerTools.pickHead;
import static tconstruct.tools.TinkerTools.pickaxe;
import static tconstruct.tools.TinkerTools.toolRod;

public class ToolClientProxy extends ClientProxy {

  @Override
  public void registerModels() {
    // blocks

    Item tableItem = Item.getItemFromBlock(TinkerTools.toolTables);
    ModelLoader.setCustomModelResourceLocation(tableItem, ToolTableBlock.TableTypes.StencilTable.meta, ToolClientEvents.locStencilTable);
    ModelLoader.setCustomModelResourceLocation(tableItem, ToolTableBlock.TableTypes.PartBuilder.meta, ToolClientEvents.locPartBuilder);
    ModelLoader.setCustomModelResourceLocation(tableItem, ToolTableBlock.TableTypes.ToolStation.meta, ToolClientEvents.locToolStation);
    ModelLoader.setCustomModelResourceLocation(tableItem, ToolTableBlock.TableTypes.ToolForge.meta, ToolClientEvents.locToolForge);

    // todo: tool forge item model register

    registerToolModel(pickaxe);

    // parts
    registerPartModel(pickHead);
    registerPartModel(binding);
    registerPartModel(toolRod);

    registerModifierModel(TinkerTools.diamondMod, Util.getResource("models/item/modifiers/Diamond"));
    registerModifierModel(TinkerTools.fortifyMod, Util.getResource("models/item/modifiers/Fortify"));
  }

  public ResourceLocation registerPartModel(Item item) {
    ResourceLocation itemLocation = getItemLocation(item);
    if(itemLocation == null) {
      return null;
    }

    String path = "parts/" + itemLocation.getResourcePath() + MaterialModelLoader.EXTENSION;

    return registerMaterialModel(item, new ResourceLocation(itemLocation.getResourceDomain(), path));
  }
}
