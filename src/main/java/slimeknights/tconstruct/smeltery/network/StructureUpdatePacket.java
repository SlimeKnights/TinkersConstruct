package slimeknights.tconstruct.smeltery.network;

import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Packet sent when the smeltery or foundry structure changes
 */
@AllArgsConstructor
public class StructureUpdatePacket implements IThreadsafePacket {
  private final BlockPos pos;
  private final BlockPos minPos;
  private final BlockPos maxPos;
  private final List<BlockPos> tanks;

  public StructureUpdatePacket(FriendlyByteBuf buffer) {
    pos = buffer.readBlockPos();
    minPos = buffer.readBlockPos();
    maxPos = buffer.readBlockPos();
    int count = buffer.readVarInt();
    tanks = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      tanks.add(buffer.readBlockPos());
    }
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeBlockPos(minPos);
    buffer.writeBlockPos(maxPos);
    buffer.writeVarInt(tanks.size());
    for (BlockPos tank : tanks) {
      buffer.writeBlockPos(tank);
    }
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  private static class HandleClient {
    private static void handle(StructureUpdatePacket packet) {
      BlockEntityHelper.get(HeatingStructureBlockEntity.class, Minecraft.getInstance().level, packet.pos)
                       .ifPresent(te -> te.setStructureSize(packet.minPos, packet.maxPos, packet.tanks));
    }
  }
}
