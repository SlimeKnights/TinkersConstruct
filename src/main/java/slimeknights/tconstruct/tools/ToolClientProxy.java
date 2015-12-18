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

import java.util.Locale;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.library.client.model.MaterialModelLoader;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tools.block.BlockToolTable;
import slimeknights.tconstruct.tools.client.RenderEvents;

import static slimeknights.tconstruct.tools.TinkerTools.shard;
import static slimeknights.tconstruct.tools.TinkerTools.sharpeningKit;

public class ToolClientProxy extends ClientProxy {

  @Override
  public void preInit() {
    super.preInit();
  }

  @Override
  public void init() {
    toolBuildInfo();
  }

  @Override
  public void postInit() {
    MinecraftForge.EVENT_BUS.register(new ToolClientEvents());
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
    final ResourceLocation patternLoc = ToolClientEvents.locBlankPattern;
    CustomTextureCreator.patternModelLocation = new ResourceLocation(patternLoc.getResourceDomain(), "item/" + patternLoc.getResourcePath());

    ModelLoader.setCustomMeshDefinition(TinkerTools.pattern, new ItemMeshDefinition() {
      @Override
      public ModelResourceLocation getModelLocation(ItemStack stack) {
        NBTTagCompound tag = TagUtil.getTagSafe(stack);
        String suffix = tag.getString(Pattern.TAG_PARTTYPE).toLowerCase(Locale.US);

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
    registerPartModel(sharpeningKit);
    for(ToolPart part : TinkerTools.toolparts) {
      registerPartModel(part);
    }

    registerModifierModel(TinkerTools.modDiamond, Util.getResource("models/item/modifiers/diamond"));
    registerModifierModel(TinkerTools.modHaste, Util.getResource("models/item/modifiers/haste"));
    registerModifierModel(TinkerTools.modSharpness, Util.getResource("models/item/modifiers/sharpness"));
    // we add a temporary modifier that does nothing to work around the model restrictions for the fortify modifier
    registerModifierModel(new Modifier("fortify") {
      @Override
      public void updateNBT(NBTTagCompound modifierTag) {
      }

      @Override
      public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {

      }

      @Override
      public boolean hasTexturePerMaterial() {
        return true;
      }
    }, Util.getResource("models/item/modifiers/fortify"));
  }

  public ResourceLocation registerPartModel(Item item) {
    ResourceLocation itemLocation = getItemLocation(item);
    if(itemLocation == null) {
      return null;
    }

    String path = "parts/" + itemLocation.getResourcePath() + MaterialModelLoader.EXTENSION;

    return registerMaterialModel(item, new ResourceLocation(itemLocation.getResourceDomain(), path));
  }

  public void toolBuildInfo() {
    // pickaxe
    ToolBuildGuiInfo info = new ToolBuildGuiInfo(TinkerTools.pickaxe);
    info.addSlotPosition(33-18, 42+18); // rod
    info.addSlotPosition(33+20, 42-20); // pick head
    info.addSlotPosition(33, 42); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // shovel
    info = new ToolBuildGuiInfo(TinkerTools.shovel);
    info.addSlotPosition(33, 42); // rod
    info.addSlotPosition(33+18, 42-18); // shovel head
    info.addSlotPosition(33-20, 42+20); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // hatchet
    info = new ToolBuildGuiInfo(TinkerTools.hatchet);
    info.addSlotPosition(33-11, 42+11); // rod
    info.addSlotPosition(33-2, 42-20); // head
    info.addSlotPosition(33+18, 42-8); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // mattock
    info = new ToolBuildGuiInfo(TinkerTools.mattock);
    info.addSlotPosition(33-11, 42+11); // rod
    info.addSlotPosition(33-2, 42-20); // axe head
    info.addSlotPosition(33+18, 42-8); // shovel head
    TinkerRegistryClient.addToolBuilding(info);

    // broadsword
    info = new ToolBuildGuiInfo(TinkerTools.broadSword);
    info.addSlotPosition(33-20-1, 42+20); // handle
    info.addSlotPosition(33+20-6, 42-20+5); // blade
    info.addSlotPosition(33-2-1, 42+2); // guard
    TinkerRegistryClient.addToolBuilding(info);

    // longsword
    info = new ToolBuildGuiInfo(TinkerTools.longSword);
    info.addSlotPosition(33-20-1, 42+20); // handle
    info.addSlotPosition(33+20-6, 42-20+5); // blade
    info.addSlotPosition(33-2-1, 42+2); // guard
    TinkerRegistryClient.addToolBuilding(info);
    // rapier
    // dagger

    // battlesign
    info = new ToolBuildGuiInfo(TinkerTools.battleSign);
    info.addSlotPosition(33-6, 42+18); // handle
    info.addSlotPosition(33-6, 42-8); // sign
    TinkerRegistryClient.addToolBuilding(info);

    // frypan
    info = new ToolBuildGuiInfo(TinkerTools.fryPan);
    info.addSlotPosition(33-20-1, 42+20); // handle
    info.addSlotPosition(33+2-1, 42-6); // pan
    TinkerRegistryClient.addToolBuilding(info);

    // hammer
    info = new ToolBuildGuiInfo(TinkerTools.hammer);
    info.addSlotPosition(33-10-2, 42+10); // handle
    info.addSlotPosition(33+13-2, 42-13); // head
    info.addSlotPosition(33+10+16-2, 42-10+16); // plate 1
    info.addSlotPosition(33+10-16-2, 42-10-16); // plate 2
    TinkerRegistryClient.addToolBuilding(info);

    // excavator
    info = new ToolBuildGuiInfo(TinkerTools.excavator);
    info.addSlotPosition(33-10+2, 42+4); // handle
    info.addSlotPosition(33+12, 42-16); // head
    info.addSlotPosition(33-8, 42-16); // plate
    info.addSlotPosition(33-10-16, 42+20); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // lumberaxe
    info = new ToolBuildGuiInfo(TinkerTools.lumberAxe);
    info.addSlotPosition(33+6-10+3, 42+4); // handle
    info.addSlotPosition(33+6-6, 42-20); // head
    info.addSlotPosition(33+6+14, 42-4); // plate
    info.addSlotPosition(33+6-10-16, 42+20); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // cleaver
    info = new ToolBuildGuiInfo(TinkerTools.cleaver);
    info.addSlotPosition(33-10-14, 42+10+12); // handle
    info.addSlotPosition(33-8, 42-10+4); // head
    info.addSlotPosition(33+14, 42-10-2); // plate/shield
    info.addSlotPosition(33+10-10, 42+10+6); // guard
    TinkerRegistryClient.addToolBuilding(info);

    // battleaxe
    info = new ToolBuildGuiInfo(TinkerTools.battleAxe);
    info.addSlotPosition(33-14, 42+10); // handle
    info.addSlotPosition(33+10-20, 42-10-10); // head 1
    info.addSlotPosition(33+10+6, 42-10+16); // head 2
    info.addSlotPosition(33+9, 42-13); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // scythe
    info = new ToolBuildGuiInfo(TinkerTools.scythe);
    info.addSlotPosition(33-10, 42+10); // handle
    info.addSlotPosition(33+13, 42-13); // head
    info.addSlotPosition(33+10+16, 42-10+16); // plate
    info.addSlotPosition(33+10-16, 42-10-16); // binding
    TinkerRegistryClient.addToolBuilding(info);
  }
}
