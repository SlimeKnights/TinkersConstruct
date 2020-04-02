package slimeknights.tconstruct.tables;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.containers.TableContainerTypes;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.tables.client.inventory.chest.PartChestScreen;
import slimeknights.tconstruct.tables.client.inventory.chest.PatternChestScreen;

@Pulse(id = TinkerPulseIds.TINKER_TABLES_PULSE_ID, description = "Everything related to the tables.", forced = true)
@ObjectHolder(TConstruct.modID)
public class TinkerTables extends TinkerPulse {

  @SubscribeEvent
  public void setupClient(final FMLClientSetupEvent event) {
    ScreenManager.registerFactory(TableContainerTypes.pattern_chest, PatternChestScreen::new);
    ScreenManager.registerFactory(TableContainerTypes.part_chest, PartChestScreen::new);
  }
}
