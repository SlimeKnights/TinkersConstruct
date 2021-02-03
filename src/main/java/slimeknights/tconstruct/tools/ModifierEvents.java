package slimeknights.tconstruct.tools;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

/**
 * Event subscriber for modifier events
 */
@EventBusSubscriber(modid = TConstruct.modID, bus = Bus.FORGE)
public class ModifierEvents {

  @SubscribeEvent
  static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
    // technically works on anything that has the tic_modifiers tag
    ToolStack tool = ToolStack.from(event.getPlayer().getHeldItemMainhand());
    if (!tool.isBroken()) {
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getModifier().onBreakSpeed(tool, entry.getLevel(), event);
      }
    }
  }
}
