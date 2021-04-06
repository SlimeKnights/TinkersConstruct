package slimeknights.tconstruct.library.tools.events;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import slimeknights.tconstruct.library.tools.item.ToolCore;

@Getter
public abstract class TinkerToolEvent extends Event {

  private final ItemStack itemStack;
  private final ToolCore tool;

  public TinkerToolEvent(ItemStack itemStack) {
    this.itemStack = itemStack;
    this.tool = (ToolCore) itemStack.getItem();
  }
}
