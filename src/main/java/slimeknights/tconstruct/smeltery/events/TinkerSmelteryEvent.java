package slimeknights.tconstruct.smeltery.events;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.tconstruct.library.events.TinkerEvent;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;

public class TinkerSmelteryEvent extends TinkerEvent {

  public final BlockPos pos;
  public final TileSmeltery smeltery;

  public TinkerSmelteryEvent(BlockPos pos, TileSmeltery smeltery) {
    this.pos = pos;
    this.smeltery = smeltery;
  }

  /** Fired when an item finishes melting down in the smeltery */
  public static class OnMelting extends TinkerSmelteryEvent {

    public FluidStack result;
    /** Itemstack is not in the smeltery anymore */
    public final ItemStack itemStack;

    public OnMelting(BlockPos pos, TileSmeltery smeltery, ItemStack itemStack, FluidStack result) {
      super(pos, smeltery);
      this.itemStack = itemStack;
      this.result = result;
    }

    public static OnMelting fireEvent(TileSmeltery smeltery, ItemStack stack, FluidStack result) {
      OnMelting event = new OnMelting(smeltery.getPos(), smeltery, stack, result);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
    }
  }
}
