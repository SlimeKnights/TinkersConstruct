package slimeknights.tconstruct.smeltery.network;

import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;

/**
 * Packet sent when the smeltery structure changes
 */
@AllArgsConstructor
public class SmelteryStructureUpdatedPacket implements IThreadsafePacket {
  private final BlockPos pos;
  private final BlockPos minPos;
  private final BlockPos maxPos;

  public SmelteryStructureUpdatedPacket(PacketBuffer buffer) {
    pos = buffer.readBlockPos();
    minPos = buffer.readBlockPos();
    maxPos = buffer.readBlockPos();
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeBlockPos(minPos);
    buffer.writeBlockPos(maxPos);
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  private static class HandleClient {
    private static void handle(SmelteryStructureUpdatedPacket packet) {
      TileEntityHelper.getTile(SmelteryTileEntity.class, Minecraft.getInstance().world, packet.pos).ifPresent(te -> te.setStructureSize(packet.minPos, packet.maxPos));
    }
  }
}
