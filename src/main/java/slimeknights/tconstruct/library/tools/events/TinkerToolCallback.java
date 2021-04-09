package slimeknights.tconstruct.library.tools.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import slimeknights.tconstruct.library.tools.item.ToolCore;

import net.minecraft.item.ItemStack;

public interface TinkerToolCallback {

  Event<TinkerToolCallback> EVENT = EventFactory.createArrayBacked(TinkerToolCallback.class, (liseners) -> (itemStack,tool) -> {
    for (TinkerToolCallback event:liseners) {
      event.interact(itemStack, (ToolCore) itemStack.getItem());
    }
  });

  void interact(ItemStack itemStack,ToolCore tool);

}
