package slimeknights.tconstruct.tools.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import slimeknights.tconstruct.library.events.TinkerEvent;
import slimeknights.tconstruct.library.tools.ToolCore;

public abstract class TinkerToolEvent extends TinkerEvent {
  public final ItemStack itemStack;
  public final ToolCore tool;

  public TinkerToolEvent(ItemStack itemStack) {
    this.itemStack = itemStack;
    this.tool = (ToolCore) itemStack.getItem();
  }

  public static class ExtraBlockBreak extends TinkerToolEvent {
    public final EntityPlayer player;

    public int width;
    public int height;
    public int depth;
    public int distance;

    public ExtraBlockBreak(ItemStack itemStack, EntityPlayer player) {
      super(itemStack);
      this.player = player;
    }

    public static ExtraBlockBreak fireEvent(ItemStack itemStack, EntityPlayer player, int width, int height, int depth, int distance) {
      ExtraBlockBreak event = new ExtraBlockBreak(itemStack, player);
      event.width = width;
      event.height = height;
      event.depth = depth;
      event.distance = distance;

      MinecraftForge.EVENT_BUS.post(event);
      return event;
    }
  }
}
