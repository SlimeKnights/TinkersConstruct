package slimeknights.tconstruct.library.events;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

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

    public final EntityPlayer player;
    public final IBlockState state;

    public int width;
    public int height;
    public int depth;
    public int distance;

    public ExtraBlockBreak(ItemStack itemStack, EntityPlayer player, IBlockState state) {
      super(itemStack);
      this.player = player;
      this.state = state;
    }

    public static ExtraBlockBreak fireEvent(ItemStack itemStack, EntityPlayer player, IBlockState state, int width, int height, int depth, int distance) {
      ExtraBlockBreak event = new ExtraBlockBreak(itemStack, player, state);
      event.width = width;
      event.height = height;
      event.depth = depth;
      event.distance = distance;

      MinecraftForge.EVENT_BUS.post(event);
      return event;
    }
  }

  public static class OnRepair extends TinkerToolEvent {

    public final int amount;

    public OnRepair(ItemStack itemStack, int amount) {
      super(itemStack);
      this.amount = amount;
    }

    public static boolean fireEvent(ItemStack itemStack, int amount) {
      OnRepair event = new OnRepair(itemStack, amount);
      return !MinecraftForge.EVENT_BUS.post(event);
    }
  }
}
