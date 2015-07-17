package tconstruct.tools;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import tconstruct.ClientProxy;
import tconstruct.library.Util;
import tconstruct.library.client.model.MaterialModelLoader;
import tconstruct.tools.block.BlockToolTable;

import static tconstruct.tools.TinkerTools.binding;
import static tconstruct.tools.TinkerTools.pickHead;
import static tconstruct.tools.TinkerTools.pickaxe;
import static tconstruct.tools.TinkerTools.toolRod;

public class ToolClientProxy extends ClientProxy {

  @Override
  public void registerModels() {
    // blocks
    Item tableItem = Item.getItemFromBlock(TinkerTools.toolTables);
    ModelLoader.setCustomModelResourceLocation(tableItem, BlockToolTable.TableTypes.CraftingStation.meta, ToolClientEvents.locCraftingStation);
    ModelLoader.setCustomModelResourceLocation(tableItem, BlockToolTable.TableTypes.StencilTable.meta, ToolClientEvents.locStencilTable);
    ModelLoader.setCustomModelResourceLocation(tableItem, BlockToolTable.TableTypes.PartBuilder.meta, ToolClientEvents.locPartBuilder);
    ModelLoader.setCustomModelResourceLocation(tableItem, BlockToolTable.TableTypes.ToolStation.meta, ToolClientEvents.locToolStation);
    tableItem = Item.getItemFromBlock(TinkerTools.toolForge);
    ModelLoader.setCustomModelResourceLocation(tableItem, 0, ToolClientEvents.locToolForge);

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
