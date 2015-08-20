package slimeknights.tconstruct.tools;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;

import slimeknights.tconstruct.ClientProxy;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.client.model.MaterialModelLoader;
import slimeknights.tconstruct.tools.block.BlockToolTable;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.tools.Pattern;

import static slimeknights.tconstruct.tools.TinkerTools.binding;
import static slimeknights.tconstruct.tools.TinkerTools.largePlate;
import static slimeknights.tconstruct.tools.TinkerTools.pickHead;
import static slimeknights.tconstruct.tools.TinkerTools.pickaxe;
import static slimeknights.tconstruct.tools.TinkerTools.shard;
import static slimeknights.tconstruct.tools.TinkerTools.toolRod;

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
    info.addSlotPosition(33-20, 42+20);
    info.addSlotPosition(33, 42);

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
    registerPartModel(shard);
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
