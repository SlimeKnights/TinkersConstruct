package slimeknights.tconstruct.library.tools.events;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import slimeknights.tconstruct.library.events.TinkerEvent;
import slimeknights.tconstruct.library.tools.ToolCore;

public abstract class TinkerToolEvent extends TinkerEvent {

  public final ItemStack itemStack;
  public final ToolCore tool;

  public TinkerToolEvent(ItemStack itemStack) {
    this.itemStack = itemStack;
    this.tool = (ToolCore) itemStack.getItem();
  }

  @Cancelable
  public static class ExtraBlockBreak extends TinkerToolEvent {

    public final PlayerEntity player;
    public final BlockState state;

    public int width;
    public int height;
    public int depth;
    public int distance;

    public ExtraBlockBreak(ItemStack itemStack, PlayerEntity player, BlockState state) {
      super(itemStack);
      this.player = player;
      this.state = state;
    }

    public static ExtraBlockBreak fireEvent(ItemStack itemStack, PlayerEntity player, BlockState state, int width, int height, int depth, int distance) {
      ExtraBlockBreak event = new ExtraBlockBreak(itemStack, player, state);
      event.width = width;
      event.height = height;
      event.depth = depth;
      event.distance = distance;

      MinecraftForge.EVENT_BUS.post(event);
      return event;
    }
  }
}
