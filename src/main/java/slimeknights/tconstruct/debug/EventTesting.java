package slimeknights.tconstruct.debug;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.events.ToolStationEvent;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.harvest.TinkerHarvestTools;

//@Mod.EventBusSubscriber
public class EventTesting {

  @SubscribeEvent
  public static void onToolCraft(ToolStationEvent.ToolCraftingEvent event) {
    if(event.getItemStack().getItem() == TinkerHarvestTools.hammer) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public static void onToolCraft(ToolStationEvent.ToolModifyEvent event) {
    if(event.getModifiers().contains(TinkerModifiers.modDiamond)) {
      event.setCanceled(true);
    }
  }
}
