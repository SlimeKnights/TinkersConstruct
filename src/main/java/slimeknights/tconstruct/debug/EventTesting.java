package slimeknights.tconstruct.debug;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.events.TinkerCraftingEvent;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.harvest.TinkerHarvestTools;

//@Mod.EventBusSubscriber
public class EventTesting {

  @SubscribeEvent
  public static void onToolCraft(TinkerCraftingEvent.ToolCraftingEvent event) {
    if(event.getItemStack().getItem() == TinkerHarvestTools.hammer) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public static void onToolPartCraft(TinkerCraftingEvent.ToolPartCraftingEvent event) {
    if(event.getItemStack().getItem() == TinkerTools.arrowHead) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public static void onToolModify(TinkerCraftingEvent.ToolModifyEvent event) {
    if(event.getModifiers().contains(TinkerModifiers.modDiamond)) {
      event.setCanceled(true);
    }
  }
}
