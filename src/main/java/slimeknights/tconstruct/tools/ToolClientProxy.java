package slimeknights.tconstruct.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.library.client.model.MaterialModelLoader;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tools.block.BlockToolTable;
import slimeknights.tconstruct.tools.client.RenderEvents;

import static slimeknights.tconstruct.tools.TinkerTools.shard;

public class ToolClientProxy extends ClientProxy {

  @Override
  public void preInit() {
    super.preInit();

    MinecraftForge.EVENT_BUS.register(new ToolClientEvents());
  }

  @Override
  public void init() {
    toolBuildInfo();
    RenderEvents renderEvents = new RenderEvents();
    MinecraftForge.EVENT_BUS.register(renderEvents);
    ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(renderEvents);
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

    ItemBlockMeta slimeSand = (ItemBlockMeta) Item.getItemFromBlock(TinkerTools.slimeSand);
    slimeSand.registerItemModels();
    //ModelBakery.addVariantName(slimeSand, "tconstruct:SlimeSand#type=green", "tconstruct:SlimeSand#type=blue");
    //ModelLoader.setCustomModelResourceLocation(slimeSand, 0, new ModelResourceLocation("tconstruct:SlimeSand", "type=green"));
    //ModelLoader.setCustomModelResourceLocation(slimeSand, 1, new ModelResourceLocation("tconstruct:SlimeSand","type=blue"));


    // general items

    //registerItemModel(new ItemStack(materials, 1, 2), "SlimeCrystalRed");

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
    for(ToolCore tool : TinkerTools.tools) {
      registerToolModel(tool);
    }


    // parts
    registerPartModel(shard);
    for(ToolPart part : TinkerTools.toolparts) {
      registerPartModel(part);
    }

    registerModifierModel(TinkerTools.diamondMod, Util.getResource("models/item/modifiers/Diamond"));
    registerModifierModel(TinkerTools.fortifyMod, Util.getResource("models/item/modifiers/Fortify"));
    registerModifierModel(TinkerTools.redstoneMod, Util.getResource("models/item/modifiers/Redstone"));
  }

  public ResourceLocation registerPartModel(Item item) {
    ResourceLocation itemLocation = getItemLocation(item);
    if(itemLocation == null) {
      return null;
    }

    String path = "parts/" + itemLocation.getResourcePath() + MaterialModelLoader.EXTENSION;

    return registerMaterialModel(item, new ResourceLocation(itemLocation.getResourceDomain(), path));
  }

  private void toolBuildInfo() {
    // pickaxe
    ToolBuildGuiInfo info = new ToolBuildGuiInfo(TinkerTools.pickaxe);
    info.addSlotPosition(33-18, 42+18);
    info.addSlotPosition(33+20, 42-20);
    info.addSlotPosition(33, 42);
    TinkerRegistryClient.addToolBuilding(info);

    // shovel
    info = new ToolBuildGuiInfo(TinkerTools.shovel);
    info.addSlotPosition(33, 42);
    info.addSlotPosition(33+18, 42-18);
    info.addSlotPosition(33-20, 42+20);
    TinkerRegistryClient.addToolBuilding(info);

    // hatchet
    info = new ToolBuildGuiInfo(TinkerTools.hatchet);
    info.addSlotPosition(33-11, 42+11); // rod
    info.addSlotPosition(33-2, 42-20); // head
    info.addSlotPosition(33+18, 42-8); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // broadsword
    info = new ToolBuildGuiInfo(TinkerTools.broadSword);
    info.addSlotPosition(33-20-1, 42+20);
    info.addSlotPosition(33+20-1, 42-20);
    info.addSlotPosition(33-2-1, 42+2);
    TinkerRegistryClient.addToolBuilding(info);

    // hammer
    info = new ToolBuildGuiInfo(TinkerTools.hammer);
    info.addSlotPosition(33-10, 42+10); // handle
    info.addSlotPosition(33+13, 42-13); // head
    info.addSlotPosition(33+10+16, 42-10+16); // plate 1
    info.addSlotPosition(33+10-16, 42-10-16); // plate 2
    TinkerRegistryClient.addToolBuilding(info);
  }
}
