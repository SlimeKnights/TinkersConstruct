package slimeknights.tconstruct.tables;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.containers.TableContainerTypes;
import slimeknights.tconstruct.tables.client.inventory.chest.PartChestScreen;
import slimeknights.tconstruct.tables.client.inventory.chest.PatternChestScreen;
import slimeknights.tconstruct.tables.client.inventory.table.CraftingStationScreen;
import slimeknights.tconstruct.tables.client.inventory.table.PartBuilderScreen;
import slimeknights.tconstruct.tables.client.renderer.CraftingStationTileEntityRenderer;
import slimeknights.tconstruct.tileentities.TablesTileEntities;

/**
 * Handles all the table for tool creation
 */
public class TinkerTables extends TinkerModule {

  @SubscribeEvent
  public void setupClient(final FMLClientSetupEvent event) {
    ScreenManager.registerFactory(TableContainerTypes.crafting_station, CraftingStationScreen::new);

    ScreenManager.registerFactory(TableContainerTypes.part_builder, PartBuilderScreen::new);

    ScreenManager.registerFactory(TableContainerTypes.pattern_chest, PatternChestScreen::new);
    ScreenManager.registerFactory(TableContainerTypes.part_chest, PartChestScreen::new);

    ClientRegistry.bindTileEntityRenderer(TablesTileEntities.crafting_station.get(), CraftingStationTileEntityRenderer::new);
  }
}
