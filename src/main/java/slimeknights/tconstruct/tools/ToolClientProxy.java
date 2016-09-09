package slimeknights.tconstruct.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.tools.common.block.BlockToolTable;
import slimeknights.tconstruct.tools.common.client.RenderEvents;

import static slimeknights.tconstruct.tools.TinkerTools.shard;
import static slimeknights.tconstruct.tools.TinkerTools.sharpeningKit;

public class ToolClientProxy extends ClientProxy {

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


}
