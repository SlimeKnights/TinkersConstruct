package slimeknights.tconstruct.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.library.client.model.MaterialModelLoader;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.tools.block.BlockToolTable;
import slimeknights.tconstruct.tools.client.RenderEvents;
import slimeknights.tconstruct.tools.client.renderer.RenderShuriken;
import slimeknights.tconstruct.tools.entity.EntityShuriken;
import slimeknights.tconstruct.tools.modifiers.ModFortifyDisplay;

import static slimeknights.tconstruct.tools.TinkerTools.shard;
import static slimeknights.tconstruct.tools.TinkerTools.sharpeningKit;

public class ToolClientProxy extends ClientProxy {

  @Override
  public void init() {
    registerToolBuildInfo();
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
    // pattern/part chest
    ModelLoader.setCustomModelResourceLocation(tableItem, BlockToolTable.TableTypes.PatternChest.meta, ToolClientEvents.locPatternChest);
    ModelLoader.setCustomModelResourceLocation(tableItem, BlockToolTable.TableTypes.PartChest.meta, ToolClientEvents.locPartChest);

    tableItem = Item.getItemFromBlock(TinkerTools.toolForge);
    ModelLoader.setCustomModelResourceLocation(tableItem, 0, ToolClientEvents.locToolForge);

    // patterns
    final ResourceLocation patternLoc = ToolClientEvents.locBlankPattern;
    CustomTextureCreator.patternModelLocation = new ResourceLocation(patternLoc.getResourceDomain(), "item/" + patternLoc.getResourcePath());
    ModelLoader.setCustomMeshDefinition(TinkerTools.pattern, new PatternMeshDefinition(patternLoc));

    // parts
    registerPartModel(shard);
    registerPartModel(sharpeningKit);
  }

  private void registerToolBuildInfo() {
    // pickaxe
    ToolBuildGuiInfo info;

    info = new ToolBuildGuiInfo(TinkerTools.pickaxe);
    info.addSlotPosition(33 - 18, 42 + 18); // rod
    info.addSlotPosition(33 + 20, 42 - 20); // pick head
    info.addSlotPosition(33, 42); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // shovel
    info = new ToolBuildGuiInfo(TinkerTools.shovel);
    info.addSlotPosition(33, 42); // rod
    info.addSlotPosition(33 + 18, 42 - 18); // shovel head
    info.addSlotPosition(33 - 20, 42 + 20); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // hatchet
    info = new ToolBuildGuiInfo(TinkerTools.hatchet);
    info.addSlotPosition(33 - 11, 42 + 11); // rod
    info.addSlotPosition(33 - 2, 42 - 20); // head
    info.addSlotPosition(33 + 18, 42 - 8); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // mattock
    info = new ToolBuildGuiInfo(TinkerTools.mattock);
    info.addSlotPosition(33 - 11, 42 + 11); // rod
    info.addSlotPosition(33 - 2, 42 - 20); // axe head
    info.addSlotPosition(33 + 18, 42 - 8); // shovel head
    TinkerRegistryClient.addToolBuilding(info);


    // hammer
    info = new ToolBuildGuiInfo(TinkerTools.hammer);
    info.addSlotPosition(33 - 10 - 2, 42 + 10); // handle
    info.addSlotPosition(33 + 13 - 2, 42 - 13); // head
    info.addSlotPosition(33 + 10 + 16 - 2, 42 - 10 + 16); // plate 1
    info.addSlotPosition(33 + 10 - 16 - 2, 42 - 10 - 16); // plate 2
    TinkerRegistryClient.addToolBuilding(info);

    // excavator
    info = new ToolBuildGuiInfo(TinkerTools.excavator);
    info.addSlotPosition(33 - 10 + 2, 42 + 4); // handle
    info.addSlotPosition(33 + 12, 42 - 16); // head
    info.addSlotPosition(33 - 8, 42 - 16); // plate
    info.addSlotPosition(33 - 10 - 16, 42 + 20); // binding
    TinkerRegistryClient.addToolBuilding(info);

    // lumberaxe
    info = new ToolBuildGuiInfo(TinkerTools.lumberAxe);
    info.addSlotPosition(33 + 6 - 10 + 3, 42 + 4); // handle
    info.addSlotPosition(33 + 6 - 6, 42 - 20); // head
    info.addSlotPosition(33 + 6 + 14, 42 - 4); // plate
    info.addSlotPosition(33 + 6 - 10 - 16, 42 + 20); // binding
    TinkerRegistryClient.addToolBuilding(info);

    /*
    // scythe
    info = new ToolBuildGuiInfo(TinkerMeleeWeapons.scythe);
    info.addSlotPosition(33-10, 42+10); // handle
    info.addSlotPosition(33+13, 42-13); // head
    info.addSlotPosition(33+10+16, 42-10+16); // plate
    info.addSlotPosition(33+10-16, 42-10-16); // binding
    TinkerRegistryClient.addToolBuilding(info);*/
  }
}
