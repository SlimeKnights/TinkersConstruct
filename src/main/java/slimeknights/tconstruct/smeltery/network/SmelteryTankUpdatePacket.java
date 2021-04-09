package slimeknights.tconstruct.smeltery.network;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import lombok.AllArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.smeltery.tileentity.tank.ISmelteryTankHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Packet sent whenever the contents of the smeltery tank change
 */
public class SmelteryTankUpdatePacket implements IThreadsafePacket {
  private final BlockPos pos;
  private final List<FluidVolume> fluids;

  public SmelteryTankUpdatePacket(BlockPos pos, List<FluidVolume> fluids) {
    this.pos = pos;
    this.fluids = fluids;
  }

  @Override
  public void encode(PacketByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeVarInt(fluids.size());
    for (FluidVolume fluid : fluids) {
      fluid.toMcBuffer(buffer);
    }
  }

  @Override
  public void handleThreadsafe(PacketSender context) {
    HandleClient.handle(this);
  }

  private static class HandleClient {
    private static void handle(SmelteryTankUpdatePacket packet) {
      TileEntityHelper.getTile(ISmelteryTankHandler.class, MinecraftClient.getInstance().world, packet.pos).ifPresent(te -> te.updateFluidsFromPacket(packet.fluids));
    }
  }
}
