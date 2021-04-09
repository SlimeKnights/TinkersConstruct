package slimeknights.tconstruct.smeltery.network;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.TileEntityHelper;

import java.io.IOException;

public class FluidUpdatePacket implements IThreadsafePacket {

  protected final BlockPos pos;
  protected final FluidVolume fluid;

  public FluidUpdatePacket(BlockPos pos, FluidVolume fluid) {
    this.pos = pos;
    this.fluid = fluid;
  }

  public FluidUpdatePacket(PacketByteBuf buffer) {
    try {
      this.pos = buffer.readBlockPos();
      this.fluid = FluidVolume.fromMcBuffer(buffer);
    } catch (IOException e) {
      throw new RuntimeException("An error occurred reading the fluid update packet!", e);
    }
  }

  @Override
  public void encode(PacketByteBuf buffer) {
    buffer.writeBlockPos(pos);
    fluid.toMcBuffer(buffer);
  }

  @Override
  public void handleThreadsafe(PlayerEntity player, PacketSender context) {
    HandleClient.handle(this);
  }

  /** Interface to implement for anything wishing to receive fluid updates */
  public interface IFluidPacketReceiver {

    /**
     * Updates the current fluid to the specified value
     *
     * @param fluid New fluidstack
     */
    void updateFluidTo(FluidVolume fluid);
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {
    private static void handle(FluidUpdatePacket packet) {
      TileEntityHelper.getTile(IFluidPacketReceiver.class, MinecraftClient.getInstance().world, packet.pos).ifPresent(te -> te.updateFluidTo(packet.fluid));
    }
  }
}
