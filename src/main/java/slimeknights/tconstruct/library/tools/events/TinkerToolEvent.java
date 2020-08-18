package slimeknights.tconstruct.library.tools.events;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import slimeknights.tconstruct.library.tools.ToolCore;

@Getter
public abstract class TinkerToolEvent extends Event {

  private final ItemStack itemStack;
  private final ToolCore tool;

  public TinkerToolEvent(ItemStack itemStack) {
    this.itemStack = itemStack;
    this.tool = (ToolCore) itemStack.getItem();
  }

  @Cancelable
  @Getter
  @Setter
  public static class ExtraBlockBreak extends TinkerToolEvent {

    private final PlayerEntity player;
    private final BlockState state;

    private int width;
    private int height;
    private int depth;
    private int distance;

    public ExtraBlockBreak(ItemStack itemStack, PlayerEntity player, BlockState state, int width, int height, int depth, int distance) {
      super(itemStack);
      this.player = player;
      this.state = state;
      this.width = width;
      this.height = height;
      this.depth = depth;
      this.distance = distance;
    }

    /**
     * Creates a new ExtraBlockBreak event and fires it on the event bus
     *
     * @param itemStack the tool stack
     * @param player the player using the tool
     * @param state the BlockState being mined
     * @param width the width for minming extra blocks
     * @param height the height for mining extra blocks
     * @param depth the depth for mining extra blocks
     * @param distance the distance to mine extra blocks within
     * @return the event
     */
    public static ExtraBlockBreak fireEvent(ItemStack itemStack, PlayerEntity player, BlockState state, int width, int height, int depth, int distance) {
      ExtraBlockBreak event = new ExtraBlockBreak(itemStack, player, state, width, height, depth, distance);

      MinecraftForge.EVENT_BUS.post(event);
      return event;
    }
  }
}
