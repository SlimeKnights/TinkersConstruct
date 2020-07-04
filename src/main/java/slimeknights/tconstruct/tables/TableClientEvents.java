package slimeknights.tconstruct.tables;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.tables.client.PatternGuiTextureLoader;
import slimeknights.tconstruct.tables.client.inventory.chest.PartChestScreen;
import slimeknights.tconstruct.tables.client.inventory.chest.PatternChestScreen;
import slimeknights.tconstruct.tables.client.inventory.table.CraftingStationScreen;
import slimeknights.tconstruct.tables.client.inventory.table.PartBuilderScreen;
import slimeknights.tconstruct.tables.client.inventory.table.ToolStationScreen;
import slimeknights.tconstruct.tables.client.renderer.CraftingStationTileEntityRenderer;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = TConstruct.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class TableClientEvents extends ClientEventBase {

  /**
   * Called by TinkerClient to add the resource listeners, runs during constructor
   */
  public static void addResourceListener(IReloadableResourceManager manager) {
    manager.addReloadListener(PatternGuiTextureLoader.INSTANCE);
  }

  @SubscribeEvent
  public static void setupClient(final FMLClientSetupEvent event) {
    ScreenManager.registerFactory(TinkerTables.craftingStationContainer.get(), CraftingStationScreen::new);
    ScreenManager.registerFactory(TinkerTables.partBuilderContainer.get(), PartBuilderScreen::new);
    ScreenManager.registerFactory(TinkerTables.patternChestContainer.get(), PatternChestScreen::new);
    ScreenManager.registerFactory(TinkerTables.partChestContainer.get(), PartChestScreen::new);
    ScreenManager.registerFactory(TinkerTables.toolStationContainer.get(), ToolStationScreen::new);

    ClientRegistry.bindTileEntityRenderer(TinkerTables.craftingStationTile.get(), CraftingStationTileEntityRenderer::new);
  }
}
