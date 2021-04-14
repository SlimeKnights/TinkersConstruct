package slimeknights.tconstruct.tables;

import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.resource.ReloadableResourceManager;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.tables.client.PatternGuiTextureLoader;
import slimeknights.tconstruct.tables.client.SlotInformationLoader;
import slimeknights.tconstruct.tables.client.TableTileEntityRenderer;
import slimeknights.tconstruct.tables.client.inventory.TinkerChestScreen;
import slimeknights.tconstruct.tables.client.inventory.table.CraftingStationScreen;
import slimeknights.tconstruct.tables.client.inventory.table.PartBuilderScreen;
import slimeknights.tconstruct.tables.client.inventory.table.TinkerStationScreen;

@SuppressWarnings("unused")
public class TableClientEvents extends ClientEventBase {

  /**
   * Called by TinkerClient to add the resource listeners, runs during constructor
   */
  public static void addResourceListener(ReloadableResourceManager manager) {
    manager.registerListener(PatternGuiTextureLoader.INSTANCE);
    manager.registerListener(SlotInformationLoader.INSTANCE);
  }

//  this looks like baked models might be needed here
//  @SubscribeEvent
//  static void registerModelLoader(ModelRegistryEvent event) {
//    ModelLoaderRegistry.registerLoader(Util.getResource("table"), TableModel.LOADER);
//  }

  @Override
  public void onInitializeClient() {
    HandledScreens.register(TinkerTables.craftingStationContainer, CraftingStationScreen::new);
    HandledScreens.register(TinkerTables.tinkerStationContainer, TinkerStationScreen::new);
    HandledScreens.register(TinkerTables.partBuilderContainer, PartBuilderScreen::new);
    HandledScreens.register(TinkerTables.tinkerChestContainer, TinkerChestScreen::new);

    BlockEntityRendererRegistry.INSTANCE.register(TinkerTables.craftingStationTile, TableTileEntityRenderer::new);
    BlockEntityRendererRegistry.INSTANCE.register(TinkerTables.tinkerStationTile, TableTileEntityRenderer::new);
    BlockEntityRendererRegistry.INSTANCE.register(TinkerTables.partBuilderTile, TableTileEntityRenderer::new);
  }
}
