package slimeknights.tconstruct.smeltery.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.TileEntityHelper;

public class FluidUpdatePacket implements IThreadsafePacket {

  protected final BlockPos pos;
  protected final FluidStack fluid;

  public FluidUpdatePacket(BlockPos pos, FluidStack fluid) {
    this.pos = pos;
    this.fluid = fluid;
  }

  public FluidUpdatePacket(PacketBuffer buffer) {
    this.pos = buffer.readBlockPos();
    this.fluid = buffer.readFluidStack();
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeFluidStack(fluid);
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  /** Interface to implement for anything wishing to receive fluid updates */
  public interface IFluidPacketReceiver {

    /**
     * Updates the current fluid to the specified value
     *
     * @param fluid New fluidstack
     */
    void updateFluidTo(FluidStack fluid);
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {
    private static void handle(FluidUpdatePacket packet) {
      TileEntityHelper.getTile(IFluidPacketReceiver.class, Minecraft.getInstance().world, packet.pos).ifPresent(te -> te.updateFluidTo(packet.fluid));
    }
  }
}
