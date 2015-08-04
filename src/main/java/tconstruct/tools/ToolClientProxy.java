package tconstruct.tools;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;

import tconstruct.ClientProxy;
import tconstruct.library.TinkerRegistryClient;
import tconstruct.library.Util;
import tconstruct.library.client.CustomTextureCreator;
import tconstruct.library.client.ToolBuildGuiInfo;
import tconstruct.library.client.model.MaterialModelLoader;
import tconstruct.library.utils.TagUtil;
import tconstruct.tools.block.BlockToolTable;
import tconstruct.tools.item.Pattern;

import static tconstruct.tools.TinkerTools.binding;
import static tconstruct.tools.TinkerTools.largePlate;
import static tconstruct.tools.TinkerTools.pickHead;
import static tconstruct.tools.TinkerTools.pickaxe;
import static tconstruct.tools.TinkerTools.toolRod;

public class ToolClientProxy extends ClientProxy {

  @Override
  public void preInit() {
    super.preInit();

    TinkerMaterials.registerMaterialRendering();
    MinecraftForge.EVENT_BUS.register(new ToolClientEvents());
  }

  @Override
  public void init() {
    ToolBuildGuiInfo info = new ToolBuildGuiInfo(TinkerTools.pickaxe);
    info.addSlotPosition(33+20, 42-20);
    info.addSlotPosition(33, 42);
    info.addSlotPosition(33-20, 42+20);

    TinkerRegistryClient.addToolBuilding(info);
  }

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

    // general items

    // patterns
    final ResourceLocation patternLoc = getItemLocation(TinkerTools.pattern);
    CustomTextureCreator.patternModelLocation = new ResourceLocation(patternLoc.getResourceDomain(), "item/" + patternLoc.getResourcePath());

    ModelLoader.setCustomMeshDefinition(TinkerTools.pattern, new ItemMeshDefinition() {
      @Override
      public ModelResourceLocation getModelLocation(ItemStack stack) {
        NBTTagCompound tag = TagUtil.getTagSafe(stack);
        String suffix = tag.getString(Pattern.TAG_PARTTYPE);

        if(!suffix.isEmpty())
          suffix = "_" + suffix;

        return new ModelResourceLocation(new ResourceLocation(patternLoc.getResourceDomain(),
                                                              patternLoc.getResourcePath() + suffix),
                                         "inventory");
      }
    });

    // tools
    registerToolModel(pickaxe);


    // parts
    registerPartModel(pickHead);
    registerPartModel(binding);
    registerPartModel(toolRod);
    registerPartModel(largePlate);

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
