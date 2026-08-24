package slimeknights.tconstruct.smeltery.network;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.BlockEntityPacket;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;

/**
 * Packet for when the flowing state changes on a channel side.
 * TODO 1.21: make a record.
 */
@RequiredArgsConstructor
@ToString
public class ChannelFlowPacket implements BlockEntityPacket<ChannelBlockEntity> {
	private final BlockPos pos;
	private final Direction side;
	private final boolean flow;

	public ChannelFlowPacket(FriendlyByteBuf buffer) {
		pos = buffer.readBlockPos();
		side = buffer.readEnum(Direction.class);
		flow = buffer.readBoolean();
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeEnum(side);
		buffer.writeBoolean(flow);
	}

  @Override
  public BlockPos pos() {
    return pos;
  }

  @Override
  public Class<ChannelBlockEntity> type() {
    return ChannelBlockEntity.class;
  }

  @Override
  public void handleBlockEntity(Context context, ChannelBlockEntity be) {
    be.setFlow(side, flow);
  }
}
