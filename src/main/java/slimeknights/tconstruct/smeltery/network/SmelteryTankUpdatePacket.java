package slimeknights.tconstruct.smeltery.network;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.fluids.FluidUtil;
import slimeknights.tconstruct.smeltery.tileentity.tank.ISmelteryTankHandler;

import java.io.IOException;
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

  public SmelteryTankUpdatePacket(PacketByteBuf buffer) {
    pos = buffer.readBlockPos();
    int size = buffer.readVarInt();
    fluids = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      try {
        fluids.add(FluidVolume.fromMcBuffer(buffer));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
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
  public void handleThreadsafe(PlayerEntity player, PacketSender context) {
    TileEntityHelper.getTile(ISmelteryTankHandler.class, MinecraftClient.getInstance().world, this.pos).ifPresent(te -> te.updateFluidsFromPacket(this.fluids));
  }
}
