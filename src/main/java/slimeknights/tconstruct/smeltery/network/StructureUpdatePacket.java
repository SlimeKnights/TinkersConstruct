package slimeknights.tconstruct.smeltery.network;

import lombok.AllArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.BlockEntityPacket;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Packet sent when the smeltery or foundry structure changes
 */
@AllArgsConstructor
public class StructureUpdatePacket implements BlockEntityPacket<HeatingStructureBlockEntity> {
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
  public BlockPos pos() {
    return pos;
  }

  @Override
  public Class<HeatingStructureBlockEntity> type() {
    return HeatingStructureBlockEntity.class;
  }

  @Override
  public void handleBlockEntity(Context context, HeatingStructureBlockEntity be) {
    be.setStructureSize(minPos, maxPos, tanks);
  }
}
